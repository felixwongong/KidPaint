package Network;


import Network.Client.Client;
import Network.Client.Room;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

public class NetworkUI extends JFrame{
    Client client;
    public NetworkUI() throws IOException {
        client = new Client();

        setUI();
    }

    private void setUI() {
        this.setTitle("KidsPaint");
        this.setSize(400, 370);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = this.getContentPane();
        container.setLayout(new FlowLayout());

        JPanel roomListPanel = new JPanel(new GridLayout(0, 1));
        java.util.List<Room> roomList = client.network.RoomList;

        for(Room room: roomList) {
            JButton roomBtn = new JButton(room.name);
            roomBtn.setPreferredSize(new Dimension(300, 30));
            roomListPanel.add(roomBtn);
        }

        JScrollPane sp = new JScrollPane(roomListPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        sp.setPreferredSize(new Dimension(350, 150));

        container.add(sp);

        //Set Text Field
        JTextField username = new JTextField();
        username.setPreferredSize(new Dimension(350, 30));
        container.add(username);

        JTextField roomName = new JTextField();
        roomName.setPreferredSize(new Dimension(350, 30));
        container.add(roomName);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        container.add(panel);
        JButton createRoomBtn = new JButton("Create Room");
        createRoomBtn.setFont(new Font("Serif", Font.BOLD, 20));
        createRoomBtn.setBackground(new Color(255, 178, 102));
        panel.add(createRoomBtn);


        JButton joinRoomBtn = new JButton("Join Room");
        joinRoomBtn.setFont(new Font("Serif", Font.BOLD, 20));
        joinRoomBtn.setBackground(new Color(255, 178, 102));
        panel.add(joinRoomBtn);

        createRoomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.createRoom(roomName.getText(), username.getText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        joinRoomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    client.joinRoom(roomName.getText(), username.getText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        this.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        new NetworkUI();
    }
}
