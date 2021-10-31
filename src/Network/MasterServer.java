package Network;

import java.io.DataInputStream;
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

        while (true) {
            System.out.printf("Master server listening at port %d...\n", port);
            Socket cSocket = masterSrvSocket.accept();
            Thread t = new Thread(() -> {
                try {
                    serve(cSocket);
                } catch (IOException e) {
                    System.err.println("Master connection dropped.");
                }
            });
            t.start();
        }
    }

    private void serve(Socket clientSocket) throws IOException {
        byte[] buffer = new byte[1024];
        System.out.printf("Established a connection to host %s:%d\n\n", clientSocket.getInetAddress(),
                clientSocket.getPort());

        DataInputStream in = new DataInputStream(clientSocket.getInputStream());

        while (true) {
            int cmdLen = in.readInt();
            byte[] cmdBuffer = new byte[cmdLen];
            in.read(cmdBuffer, 0, cmdLen);
            String cmd = new String(cmdBuffer, 0, cmdLen);
            System.out.println(cmd);
        }
    }

    public boolean createRoom(String name) {
        if(roomList.containsKey(name)) {
            System.out.println("Room has already existed");
            return false;
        }
        try {
            RoomServer newServer = new RoomServer();
            synchronized (roomList){
                roomList.put(name, newServer);
            }
            Thread t = new Thread(() -> {
                try {
                    newServer.start();
                } catch (IOException e) {
                    System.err.println("Server " + name + " down");
                }
                synchronized (roomList) {
                    roomList.remove(newServer);
                }
            });
            t.start();
        } catch (IOException e) {
            System.out.println("Cannot create server");
        }
        return true;
    }
}
