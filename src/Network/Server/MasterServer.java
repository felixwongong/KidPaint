package Network.Server;

import Network.Client.Room;
import util.ByteArrayParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterServer {
    ServerSocket masterSrvSocket;

    private Map<String, RoomServer> roomServerDictionary = new HashMap<>();

    public static void main(String[] args) throws IOException {
        new MasterServer(12345);
    }

    public MasterServer(int port) throws IOException {
        //create base room for testing
        createRoom("testRoom1");
        createRoom("testRoom2");
        createRoom("testRoom3");
        //

        masterSrvSocket = new ServerSocket(port);
        System.out.printf("Master server listening at port %d...\n", port);

        while (true) {
            Socket cSocket = masterSrvSocket.accept();
            Thread t = new Thread(() -> {
                try {
                    sendRoomList(cSocket);
                } catch (IOException e) {
                    System.err.println("Master connection to client socket dropped.");
                }
            });
            t.start();
        }
    }

    private void sendRoomList(Socket clientSocket) throws IOException {
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        int len = in.readInt();
        byte[] cmdByte = new byte[len];
        in.read(cmdByte, 0, len);
        String cmd = new String(cmdByte, 0, len);
        if(cmd.equals("/getRoomList")){
            List<Room> roomList = new ArrayList<>();
            for(Map.Entry<String, RoomServer> roomServerEntry: roomServerDictionary.entrySet()){
                RoomServer roomServer = roomServerEntry.getValue();
                Room room = new Room(roomServer.getInetAddress(), roomServer.getPort(), roomServerEntry.getKey());
                roomList.add(room);
            }
            byte[] roomListByte = ByteArrayParser.list2Byte(roomList);
            out.writeInt(roomListByte.length);
            out.write(roomListByte, 0, roomListByte.length);
        }
    }

    private void serve(Socket clientSocket) throws IOException {
        System.out.printf("Established a connection to host %s:%d\n\n", clientSocket.getInetAddress(),
                clientSocket.getPort());

        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        while (true) {
            int cmdLen = in.readInt();
            byte[] cmdBuffer = new byte[cmdLen];
            in.read(cmdBuffer, 0, cmdLen);
            String cmd = new String(cmdBuffer, 0, cmdLen);
            doCommand(out, cmd.split("\\s+"));
        }
    }

    private void doCommand(DataOutputStream out, String[] cmds) {
        switch (cmds[0]) {
            case "/createRoom":
                try {
                    createRoom(cmds[1]);
                } catch (IOException e) {
                    OutputString(out, "Some error happen on creating room");
                }
                break;
            case  "/getRoom":
                try{
                    getRoom(out, cmds[1]);
                }catch (IOException e){
                    OutputString(out, "Some error happen on joining room");
                }
                break;
        }
    }

    public boolean createRoom(String name) throws IOException {
        if(roomServerDictionary.containsKey(name)) {
            System.out.println("Room has already existed");
            return false;
        }
            RoomServer newServer = new RoomServer();
            synchronized (roomServerDictionary){
                roomServerDictionary.put(name, newServer);
            }
            Thread t = new Thread(() -> {
                try {
                    System.out.println("Room (" + name + ") has been created successfully");
                    newServer.start();
                } catch (IOException e) {
                    System.err.println("Server " + name + " down");
                }
                synchronized (roomServerDictionary) {
                    roomServerDictionary.remove(newServer);
                }
            });
            t.start();

        return true;
    }

    public boolean getRoom(DataOutputStream out, String name) throws IOException {
        if(!roomServerDictionary.containsKey(name)) {
            System.out.println("Room does not exist");
            return false;
        }

        RoomServer targetRoom = roomServerDictionary.get(name);
        OutputString(out, targetRoom.getInetAddress());
        out.writeInt(targetRoom.getPort());
        return true;
    }

    private void OutputString(DataOutputStream out, String outStr) {
        int len = outStr.length();
        try {
            out.writeInt(len);
            out.write(outStr.getBytes(), 0, len);
            out.flush();
        } catch (IOException e) {
            System.out.println("String output error");
        }
    }
}
