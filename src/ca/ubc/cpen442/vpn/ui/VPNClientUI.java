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
    protected JTextField textbox;
    protected JButton sendbtn;
    
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
        
        
        scQuestionLabel = new Label("Enter text to be sent to server: ");
        add(scQuestionLabel);
        
        //Add editable text area
        textbox = new JTextField(50);
        add(textbox);
        
        // Add button to send text to server
        sendbtn = new JButton("Send");
        sendbtn.addActionListener(this);
        add(sendbtn);
        
        setVisible(true);
        if (port != null)
            client = new Client(ip, Integer.parseInt(port), console);
        else
            return;

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
    	
    	String secretMessage  = textbox.getText();
    	if(evt.getSource() == sendbtn) {
    		try {
				client.sendMessage(secretMessage);
			} catch (IOException e) {
				//Something bad has gone down, my dude
				e.printStackTrace();
			}
    		//Clear the box
    		textbox.setText("");
    	}
    	
    }
}
