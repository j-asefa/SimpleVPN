package ca.ubc.cpen442.vpn.ui;

import ca.ubc.cpen442.vpn.model.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class VPNClientUI extends Frame implements ActionListener {

    private static Client client;

    private Label scQuestionLabel;
    private VPNConsoleUI console;


    public VPNClientUI() {
        // Firstly, display the log console
        console = new VPNConsoleUI();
        console.log("Client is starting. Will ask for remote IP and port.");
        String ip = JOptionPane.showInputDialog("What is the IP of the server?", "127.0.0.1");
        String port = JOptionPane.showInputDialog("What is the port of the server?", "32888");
        // Set up the UI
        setLayout(new FlowLayout());
        setTitle("CPEN 442 VPN Client");
        setSize(800, 600);
        scQuestionLabel = new Label("TODO: implement this");
        add(scQuestionLabel);
        setVisible(true);
        client = new Client(ip, Integer.parseInt(port), console);
        try {
            client.connect();
        } catch (IOException e) {
            console.log("Exception:" + e.getLocalizedMessage());
        }
    }

    public static void main(String[] args) {
        VPNClientUI app = new VPNClientUI();
    }

    // ActionEvent handler - Called back upon button-click.
    @Override
    public void actionPerformed(ActionEvent evt) {

    }
}
