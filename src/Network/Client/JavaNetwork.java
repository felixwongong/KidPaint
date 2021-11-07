package Network.Client;

import util.ByteArrayParser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class JavaNetwork {
    private static JavaNetwork instance = null;
    public JavaView view;

    List<Player> PlayerList;
    public List<Room> RoomList;

    public static JavaNetwork getInstance(String server, int port) throws IOException {
        if(instance == null) {
            instance = new JavaNetwork(server, port);
            return instance;
        }
        return instance;
    }

    private JavaNetwork(String server, int port) throws IOException {
        this.view = new JavaView();
        this.PlayerList = new ArrayList<>();
        SocketStream masterStream = connectToServer(server, port);
        this.RoomList = getRoomsFromServer(masterStream);
    }

    private SocketStream connectToServer(String server, int port) {
        try {
            Socket cSocket = new Socket(server, port);
            DataInputStream in = new DataInputStream(cSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
            return new SocketStream(cSocket, in, out);
        } catch (IOException e) {
            System.out.println("Error: Connection to master");
        }
        return null;
    }

    private List<Room> getRoomsFromServer(SocketStream stream) throws IOException {
        DataInputStream in = stream.in;
        DataOutputStream out = stream.out;

        String cmd = "/getRoomList";
        out.writeInt(cmd.length());
        out.write(cmd.getBytes(), 0, cmd.length());

        int len = in.readInt();
        byte[] buffer = new byte[len];
        in.read(buffer, 0, len);
        List<Room> rooms = ByteArrayParser.byte2List(buffer);
        System.out.println(rooms);
        stream.socket.close();
        return rooms;
    }

    protected void joinRoom(String server, int port) {
        SocketStream roomStream = connectToServer(server, port);
        
    }

    private class SocketStream {
        Socket socket;
        DataInputStream in;
        DataOutputStream out;
        SocketStream(Socket socket, DataInputStream in, DataOutputStream out) {
            this.socket = socket;
            this.in = in;
            this.out = out;
        }
    }
/*
    protected void createRoom(String roomName, String playerName) throws IOException {
        String cmd = "/createRoom " + roomName;
        int cmdLen = cmd.length();
        this.out.writeInt(cmdLen);
        this.out.write(cmd.getBytes(), 0, cmdLen);
    }

    //1,Send request to MasterServer
    //2, receive callback (roomUrl) from MasterServer
    //3, Send request and connect to RoomServer
    protected void joinRoom(String roomName, String playerName) throws IOException {
        String cmd = "/getRoom " + roomName;
        int cmdLen = cmd.length();
        this.out.writeInt(cmdLen);
        this.out.write(cmd.getBytes(), 0, cmdLen);

        int urlLen = 0;
        urlLen = this.in.readInt();
        byte[] urlBuffer = new byte[urlLen];
        this.in.read(urlBuffer, 0, urlLen);
        int port = this.in.readInt();
        String address = new String(urlBuffer, 0, urlLen);

        this.cSocket = new Socket(address, port);
        this.out = new DataOutputStream(cSocket.getOutputStream());
        this.in = new DataInputStream(cSocket.getInputStream());

        updateServerPlayerList(playerName);

        networkCallback();
    }

    private void updateServerPlayerList(String playerName) throws IOException {
        Player player = new Player(PlayerList.size(), playerName);

        byte[] playerByte = ByteArrayParser.object2Byte(player);
        this.out.writeInt(playerByte.length);
        this.out.write(playerByte, 0, playerByte.length);

        int playerListLen = in.readInt();
        byte[] playerListBuffer = new byte[playerListLen];
        in.read(playerListBuffer, 0, playerListLen);
        PlayerList = ByteArrayParser.byte2List(playerListBuffer);
        for(Player p: PlayerList) {
            System.out.println(p.name);
        }
    }


    protected void networkCallback() {
        Thread t = new Thread(() -> {
            byte[] buffer = new byte[1024];
            try {
                while (true) {
                    int len = this.in.readInt();
                    this.in.read(buffer, 0, len);
                    System.out.println(new String(buffer, 0, len));
                }
            } catch (IOException ex) {
                System.err.println("Connection dropped!");
                System.exit(-1);
            }
        });
        t.start();
    }



    //tmp
    protected void simpleChat(DataOutputStream out) throws IOException {
        while (true){
            System.out.println("Write your message");
            Scanner scanner = new Scanner(System.in);

            String str = scanner.nextLine();
            out.writeInt(str.length());
            out.write(str.getBytes(), 0, str.length());
        }
    }
*/
}
