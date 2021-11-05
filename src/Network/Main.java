package Network;

import Network.Client.Client;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        String s = "127.0.0.1";
        int port = 12345;
        try {
            Client master = new Client();

            master.createRoom(args[1]);
        } catch (IOException e) {
            System.err.printf("Unable to create server %s:%d\n", s, port);
            System.exit(-1);
        }
    }
}
