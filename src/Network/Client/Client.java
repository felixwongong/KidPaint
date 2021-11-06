package Network.Client;

import java.io.IOException;

public class Client implements IRpcCallback{
    public static void main(String[] args) throws IOException {
        Client c = new Client();
    }

    public JavaNetwork network;

    private void addRpcMethod(){
        network.view.addRpc(this, "rpcMethod");
    }

    private Client(String server, int port) throws IOException {
        this.network = JavaNetwork.getInstance(server, port);
    }

    public Client() throws IOException {
        this("127.0.0.1", 12345);
    }

    public void createRoom(String roomName, String playerName) throws IOException {
        this.network.createRoom(roomName, playerName);
        this.network.joinRoom(roomName, playerName);
    }

    public void joinRoom(String roomName, String playerName) throws IOException {
        this.network.joinRoom(roomName, playerName);
    }

    public void rpcMethod(String rpcArg) {
        System.out.println("rpc call: " + rpcArg);
    }
}