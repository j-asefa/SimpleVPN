package ca.ubc.cpen442.vpn.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VPNClientUI extends Frame implements ActionListener {

    private Label scQuestionLabel;

    public VPNClientUI() {
        setLayout(new FlowLayout());
        setTitle("CPEN 442 VPN Client");
        setSize(800, 600);
        scQuestionLabel = new Label("TODO: implement this");
        add(scQuestionLabel);
        setVisible(true);
    }

    public static void main(String[] args) {
        VPNClientUI app = new VPNClientUI();
    }

    // ActionEvent handler - Called back upon button-click.
    @Override
    public void actionPerformed(ActionEvent evt) {

    }
}
