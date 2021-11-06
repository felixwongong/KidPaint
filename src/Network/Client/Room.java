package Network.Client;

import java.io.Serializable;

public class Room implements Serializable {
    public String name;
    public String address;
    public int port;

    public Room(String address, int port, String roomName) {
        this.address = address;
        this.port = port;
        this.name = roomName;
    }
}
