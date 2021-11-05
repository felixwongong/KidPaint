package Network.Server;

import Network.Client.Player;
import util.ByteArrayParser;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

public class RoomServer {
    ServerSocket srvSocket;
    HashMap<String, Socket> cSocketList = new HashMap<>();

    protected RoomServer() throws IOException {
        srvSocket = new ServerSocket(0, 10, InetAddress.getLocalHost());
    }

    protected void start() throws IOException {
        while (true) {
            System.out.printf("Room server listening at port %d...\n", getPort());
            Socket cSocket = srvSocket.accept();
            DataInputStream in = new DataInputStream(cSocket.getInputStream());
            DataInputStream out = new DataInputStream(cSocket.getInputStream());

            int nameLen = in.readInt();
            byte[] nameBuffer = new byte[nameLen];
            in.read(nameBuffer, 0, nameLen);

            try {
                List playerList = ByteArrayParser.byte2List(nameBuffer);
                for(Object player: playerList) {
                    Player p = (Player) player;
                    System.out.println(p.name);
                }
                System.out.println(playerList);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            synchronized (cSocketList) {
                cSocketList.put("no-name", cSocket);
            }
        }
    }

    protected int getPort() {
        return srvSocket.getLocalPort();
    }

    protected String getInetAddress() throws UnknownHostException {
        return srvSocket.getInetAddress().getHostAddress();
    }
}
