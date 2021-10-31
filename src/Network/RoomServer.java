package Network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomServer {
    ServerSocket srvSocket;
    List<Socket> cSocketList = new ArrayList<>();

    protected RoomServer() throws IOException {
        srvSocket = new ServerSocket(0);
    }

    protected void start() throws IOException {
        while (true) {
            System.out.printf("Listening at port %d...\n", getPort());
            Socket cSocket = srvSocket.accept();

            synchronized (cSocketList) {
                cSocketList.add(cSocket);
            }

            Thread t = new Thread(() -> {
                try {
                    serve(cSocket);
                } catch (IOException e) {
                    System.err.println("connection dropped.");
                }
                synchronized (cSocketList) {
                    cSocketList.remove(cSocket);
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
            int len = in.readInt();
            in.read(buffer, 0, len);
            forward(buffer, len);
        }
    }

    private void forward(byte[] data, int len) {
        synchronized (cSocketList) {
            for (int i = 0; i < cSocketList.size(); i++) {
                try {
                    Socket socket = cSocketList.get(i);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeInt(len);
                    out.write(data, 0, len);
                } catch (IOException e) {
                    // the connection is dropped but the socket is not yet removed.
                }
            }
        }
    }

    protected int getPort() {
        return srvSocket.getLocalPort();
    }
}
