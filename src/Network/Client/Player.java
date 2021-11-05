package Network.Client;

import java.io.Serializable;

public class Player implements Serializable {
    int id;
    public String name;
    public Player(int id, String name) {
        this.name = name;
        this.id = id;
    }
}
