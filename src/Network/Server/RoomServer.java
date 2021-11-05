package Network.Server;

import Network.Client.Player;
import util.ByteArrayParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomServer {
    ServerSocket srvSocket;
    HashMap<String, Socket> cSocketList = new HashMap<>();
    List<Object> playerList = new ArrayList<>();

    protected RoomServer() throws IOException {
        srvSocket = new ServerSocket(0, 10, InetAddress.getLocalHost());
    }

    protected void start() throws IOException {
        while (true) {
            System.out.printf("Room server listening at port %d...\n", getPort());
            Socket cSocket = srvSocket.accept();
            DataInputStream in = new DataInputStream(cSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());

            UpdatePlayerList(in, out);

            synchronized (cSocketList) {
                cSocketList.put("no-name", cSocket);
            }
        }
    }

    private void UpdatePlayerList(DataInputStream in, DataOutputStream out) {
        Thread t = new Thread(()-> {
            try {
                int len = in.readInt();
                byte[] playerBuffer = new byte[len];
                in.read(playerBuffer, 0, len);
                Object player = ByteArrayParser.byte2Object(playerBuffer);
                playerList.add(player);


                byte[] playerListByte = ByteArrayParser.list2Byte(playerList);

                out.writeInt(playerListByte.length);
                out.write(playerListByte, 0, playerListByte.length);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    protected int getPort() {
        return srvSocket.getLocalPort();
    }

    protected String getInetAddress() throws UnknownHostException {
        return srvSocket.getInetAddress().getHostAddress();
    }
}
