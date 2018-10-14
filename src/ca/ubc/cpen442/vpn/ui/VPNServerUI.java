package ca.ubc.cpen442.vpn.ui;

import ca.ubc.cpen442.vpn.model.Server;
import ca.ubc.cpen442.vpn.model.exceptions.ServerNotInitializedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class VPNServerUI extends Frame implements ActionListener {

    private static Server server;

    private VPNConsoleUI console;
    private Label whichPortLabel;
    private TextField tcpPortField;

    public VPNServerUI() {
        // Firstly, display the log console
        console = new VPNConsoleUI();
        // Initialize the server
        String listenPort = JOptionPane.showInputDialog("On which local TCP port do you want to listen for incoming connections?", "32888");
        server = new Server(Integer.parseInt(listenPort), console);
        console.log("Constructed server with listening TCP port " + listenPort);
        // Set up the UI
        setLayout(new FlowLayout());
        setTitle("CPEN 442 VPN Server");
        setSize(800, 600);
        setVisible(true);
        console.log("Will start listening.");
        server.initialize();
        try {
            server.listen();
        } catch (IOException e) {
            console.log("Server exception: " + e.getLocalizedMessage());
        } catch (ServerNotInitializedException e) {
            console.log("Attempted to listen before initializing server");
        }
    }

    public static void main(String[] args) {
        VPNServerUI app = new VPNServerUI();
    }

    // ActionEvent handler - Called back upon button-click.
    @Override
    public void actionPerformed(ActionEvent evt) {

    }
}
