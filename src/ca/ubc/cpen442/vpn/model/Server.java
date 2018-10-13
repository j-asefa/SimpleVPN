package ca.ubc.cpen442.vpn.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int listeningPort; // The TCP port at which the server is listening.
    private ServerSocket recvSocket;

    public Server(int listeningPort) {
        this.listeningPort = listeningPort;
    }

    public void listen() throws IOException {
        recvSocket = new ServerSocket(listeningPort);
        while (true) {
            Socket connectionSocket = recvSocket.accept();
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            // outputStream can be used to send data back to the client
            DataOutputStream outputStream = new DataOutputStream(connectionSocket.getOutputStream());
            String receivedMsg = inFromClient.readLine();
            System.out.println("Received this string across the network: " + receivedMsg);
            // TODO: do something with this string (pass it to the Crypto class for instance)
        }
    }
}
