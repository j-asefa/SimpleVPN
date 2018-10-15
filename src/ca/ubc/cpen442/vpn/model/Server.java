package ca.ubc.cpen442.vpn.model;

import ca.ubc.cpen442.vpn.model.exceptions.ServerNotInitializedException;
import ca.ubc.cpen442.vpn.ui.VPNConsoleUI;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class Server {
    private VPNConsoleUI console;
    private int listeningPort; // The TCP port at which the server is listening.
    private ServerSocket recvSocket;
    private boolean didInitialize;
    private KeyPair serverKeyPair;
    private KeyAgreement keyAgreement;

    public Server(int listeningPort, VPNConsoleUI console) {
        this.listeningPort = listeningPort;
        this.console = console;
        this.didInitialize = false;
        this.keyAgreement = null;
    }

    /**
     * Prepares the server. To be called before starting listening for incoming connections.
     * 1. Creates a KeyPair for the server.
     * 2. Displays the public key hash, y, p and g on the console.
     */
    public void initialize() {
        this.didInitialize = true;
        console.log("Server is creating a DH KeyPairGenerator");
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            // will never be called
            e.printStackTrace();
        }
        assert keyPairGenerator != null;
        keyPairGenerator.initialize(2048);
        console.log("Server is generating a DH KeyPair");
        serverKeyPair = keyPairGenerator.generateKeyPair();
        console.log("Server DH KeyPair generated");
        byte[] encodedPublicKey = serverKeyPair.getPublic().getEncoded();
        String publicKeyHash = Crypto.getSHA2Hash(encodedPublicKey);
        String y = ((DHPublicKey) serverKeyPair.getPublic()).getY().toString();
        String g = ((DHPublicKey) serverKeyPair.getPublic()).getParams().getG().toString();
        String p = ((DHPublicKey) serverKeyPair.getPublic()).getParams().getP().toString();
        console.log("Server Public Key hash is " + publicKeyHash);
        console.log("Y ends in " + y.substring(y.length() - 5));
        console.log("G is " + g);
        console.log("P ends in " + p.substring(p.length() - 5));
        try {
            keyAgreement = KeyAgreement.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            // will never be called
            e.printStackTrace();
        }

        assert keyAgreement != null;
        try {
            keyAgreement.init(serverKeyPair.getPrivate());
        } catch (InvalidKeyException e) {
            // should never be called
            e.printStackTrace();
        }
        // public key is now ready to be sent over the network
        console.log("Server is ready to send (y, p, g)");

    }

    public void listen() throws IOException, ServerNotInitializedException {
        if (!this.didInitialize) {
            throw new ServerNotInitializedException();
        }
        recvSocket = new ServerSocket(listeningPort);
        // DO NOTHING FOR NOW
        Socket connectionSocket = recvSocket.accept();
        BufferedReader inFromClient =
                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        // outputStream can be used to send data back to the client
        DataOutputStream outputStream = new DataOutputStream(connectionSocket.getOutputStream());
        // Send the length of the public key in bytes
        outputStream.writeInt(serverKeyPair.getPublic().getEncoded().length);
        // Send the public key bytes
        outputStream.write(serverKeyPair.getPublic().getEncoded());
        console.log("Sent public key over the socket");
    }
}
