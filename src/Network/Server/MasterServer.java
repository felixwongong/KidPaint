package Network.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MasterServer {
    ServerSocket masterSrvSocket;

    private Map<String, RoomServer> roomList = new HashMap<>();

    public static void main(String[] args) throws IOException {
        new MasterServer(12345);
    }

    public MasterServer(int port) throws IOException {
        masterSrvSocket = new ServerSocket(port);
        System.out.printf("Master server listening at port %d...\n", port);

        while (true) {
            Socket cSocket = masterSrvSocket.accept();
            Thread t = new Thread(() -> {
                try {
                    serve(cSocket);
                } catch (IOException e) {
                    System.err.println("Master connection to client socket dropped.");
                }
            });
            t.start();
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
                    createRoom(out, cmds[1]);
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

    public boolean createRoom(DataOutputStream out, String name) throws IOException {
        if(roomList.containsKey(name)) {
            System.out.println("Room has already existed");
            return false;
        }
            RoomServer newServer = new RoomServer();
            synchronized (roomList){
                roomList.put(name, newServer);
            }
            Thread t = new Thread(() -> {
                try {
                    System.out.println("Room (" + name + ") has been created successfully");
                    newServer.start();
                } catch (IOException e) {
                    System.err.println("Server " + name + " down");
                }
                synchronized (roomList) {
                    roomList.remove(newServer);
                }
            });
            t.start();

        return true;
    }

    public boolean getRoom(DataOutputStream out, String name) throws IOException {
        if(!roomList.containsKey(name)) {
            System.out.println("Room does not exist");
            return false;
        }

        RoomServer targetRoom = roomList.get(name);
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
