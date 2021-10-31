package Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public Client(String server, int port) throws IOException {
        Socket socket = new Socket(server, port);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        String cmd = "@create";
        int cmdLen = cmd.length();
        out.writeInt(cmdLen);
        out.writeBytes(cmd);

        Thread t = new Thread(() -> {
            byte[] buffer = new byte[1024];
            try {
                while (true) {
                    int len = in.readInt();
                    in.read(buffer, 0, len);
                    System.out.println(new String(buffer, 0, len));
                }
            } catch (IOException ex) {
                System.err.println("Connection dropped!");
                System.exit(-1);
            }
        });
        t.start();
    }

    public static void main(String[] args) {
        String s = "127.0.0.1";
        int port = 12345;
        try {
            new Client(s, port);
        } catch (IOException e) {
            System.err.printf("Unable to connect server %s:%d\n", s, port);
            System.exit(-1);
        }
    }
}