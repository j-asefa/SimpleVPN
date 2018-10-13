package ca.ubc.cpen442.vpn.ui;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VPNConsoleUI extends Frame {

    private TextArea textArea;

    public VPNConsoleUI() {
        setLayout(new FlowLayout());
        setTitle("CPEN 442 VPN Log Console");
        setSize(580, 250);
        textArea = new TextArea();
        add(textArea);
        setVisible(true);
    }

    public void log(String msg) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        textArea.appendText(sdf.format(new Date()) + ": " + msg + "\n");
    }
}
