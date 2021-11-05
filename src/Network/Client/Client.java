package Network.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client implements IRpcCallback{
    JavaNetwork network;

    private void addRpcMethod(){
        network.view.addRpc(this, "rpcMethod");
    }

    private Client(String server, int port) throws IOException {
        this.network = JavaNetwork.getInstance(server, port);
        addRpcMethod();

    }

    public Client() throws IOException {
        this("127.0.0.1", 12345);
    }

    public void createRoom(String roomName) throws IOException {
        System.out.println(this.network);
        this.network.createRoom(roomName);
    }

    public void joinRoom(String roomName, String playerName) throws IOException {
        this.network.joinRoom(roomName, playerName);
    }


    public void rpcMethod() {
        System.out.println("rpc call");
    }
}