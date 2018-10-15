package ca.ubc.cpen442.vpn.model;

import ca.ubc.cpen442.vpn.model.exceptions.ServerNotInitializedException;
import ca.ubc.cpen442.vpn.ui.VPNConsoleUI;

import javax.crypto.KeyAgreement;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.DHPublicKey;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static sun.security.pkcs11.wrapper.Functions.toHexString;

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
        String publicKeyHash = Crypto.getMD5Hash(encodedPublicKey);
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


        // Begin waiting for client public key
        DataInputStream din = new DataInputStream(connectionSocket.getInputStream());
        int numBytes = din.readInt(); // read number of public key bytes
        console.log("Received public key length: " + numBytes);
        byte[] serverPublicKeyBytes = new byte[numBytes];
        din.readFully(serverPublicKeyBytes, 0, serverPublicKeyBytes.length); // read the message

        // Use client public key to generate secret
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPublicKeyBytes);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            // should never be called
            e.printStackTrace();
        }
        assert keyFactory != null;
        PublicKey clientPublicKey = null;
        try {
            clientPublicKey = keyFactory.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            // should never be called
            e.printStackTrace();
        }
        assert clientPublicKey != null;

        try {
        keyAgreement.doPhase(clientPublicKey, true);
        } catch (InvalidKeyException e){
            e.printStackTrace();
        }

        byte[] sharedSecret = keyAgreement.generateSecret();
        int sharedSecLen = sharedSecret.length;
        int hexSecLen = toHexString(sharedSecret).length();

        // Send the length of the shared secret in bytes
        outputStream.writeInt(sharedSecLen);
        outputStream.write("Key size sent".getBytes());

        console.log("Final key ending in " + toHexString(sharedSecret).substring(hexSecLen - 5) + " created");
    }
}
