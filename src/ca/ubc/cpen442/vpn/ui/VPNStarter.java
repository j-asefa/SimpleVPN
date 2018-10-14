package ca.ubc.cpen442.vpn.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VPNStarter extends Frame implements ActionListener {

    private Label scQuestionLabel;
    private Button serverButton;
    private Button clientButton;

    public VPNStarter() {
        setLayout(new FlowLayout());
        setTitle("CPEN 442 VPN");
        setSize(500, 100);
        scQuestionLabel = new Label("Do you want to be the server or the client?");
        add(scQuestionLabel);
        serverButton = new Button("Server");
        add(serverButton);
        clientButton = new Button("Client");
        add(clientButton);
        serverButton.addActionListener(this);
        clientButton.addActionListener(this);
        setVisible(true);
    }

    public static void main(String[] args) {
        VPNStarter app = new VPNStarter();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(serverButton)) {
            System.out.println("Will start server.");
            setVisible(false);
            VPNServerUI serverUI = new VPNServerUI();
        } else if (evt.getSource().equals(clientButton)) {
            System.out.println("Will start client.");
            setVisible(false);
            VPNClientUI clientUI = new VPNClientUI();
        }

    }
}
