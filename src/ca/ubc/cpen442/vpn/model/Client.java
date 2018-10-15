package ca.ubc.cpen442.vpn.model;

import ca.ubc.cpen442.vpn.ui.VPNConsoleUI;

import javax.crypto.KeyAgreement;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static sun.security.pkcs11.wrapper.Functions.toHexString;

public class Client {
    private VPNConsoleUI consoleUI;
    private String remoteIP;
    private int remotePort; // The TCP port to which the client will connect.

    /**
     * Initializes the Client.
     *
     * @param remoteIP   The server IP we are connecting to.
     * @param remotePort The server port we are connecting to.
     * @param consoleUI  A Console window where we can log messages.
     */
    public Client(String remoteIP, int remotePort, VPNConsoleUI consoleUI) {
        consoleUI.log("Constructing client...");
        this.remoteIP = remoteIP;
        this.remotePort = remotePort;
        this.consoleUI = consoleUI;
    }

    /**
     * Connects to the remote server. Receives the server's public key.
     * Generates a DH public key based on the received key (and the parameters
     * y, p, g that come with it).
     * TODO finish this description, these are just the first steps!
     *
     * @throws IOException In case of a networking error.
     */
    public void connect() throws IOException {
        // TODO connect to the server and obtain the actual serverPublicKeyBytes
        Socket s = new Socket(remoteIP, remotePort);
        DataInputStream din = new DataInputStream(s.getInputStream());
        int numBytes = din.readInt(); // read number of public key bytes
        consoleUI.log("Received public key length: " + numBytes);
        byte[] serverPublicKeyBytes = new byte[numBytes];
        din.readFully(serverPublicKeyBytes, 0, serverPublicKeyBytes.length); // read the message

        consoleUI.log("Received public key bytes from the server");
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            // should never be called
            e.printStackTrace();
        }
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPublicKeyBytes);
        assert keyFactory != null;
        PublicKey serverPublicKey = null;
        try {
            serverPublicKey = keyFactory.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            // should never be called
            e.printStackTrace();
        }
        assert serverPublicKey != null;
        DHParameterSpec params = ((DHPublicKey) serverPublicKey).getParams();
        String publicKeyHash = Crypto.getMD5Hash(serverPublicKey.getEncoded());
        String y = ((DHPublicKey) serverPublicKey).getY().toString();
        String g = ((DHPublicKey) serverPublicKey).getParams().getG().toString();
        String p = ((DHPublicKey) serverPublicKey).getParams().getP().toString();
        consoleUI.log("Received Server Public Key hash is " + publicKeyHash);
        consoleUI.log("Received Y ends in " + y.substring(y.length() - 5));
        consoleUI.log("Received G ends in " + g);
        consoleUI.log("Received P ends in " + p.substring(p.length() - 5));
        consoleUI.log("Client will generate its keypair based on the received server public key");
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            // should never be called
            e.printStackTrace();
        }
        assert keyPairGenerator != null;
        try {
            keyPairGenerator.initialize(params);
        } catch (InvalidAlgorithmParameterException e) {
            // should never be called
            e.printStackTrace();
        }
        KeyPair clientKeyPair = keyPairGenerator.generateKeyPair();
        KeyAgreement clientKeyAgreement = null;
        try {
            clientKeyAgreement = KeyAgreement.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            // should never be called
            e.printStackTrace();
        }
        assert clientKeyAgreement != null;
        try {
            clientKeyAgreement.init(clientKeyPair.getPrivate());
        } catch (InvalidKeyException e) {
            // should never be called
            e.printStackTrace();
        }
        // this public key is ready to be sent to the server
        byte[] encoded = clientKeyPair.getPublic().getEncoded();
        consoleUI.log("Sending public key to server");

        DataOutputStream outputStream = new DataOutputStream(s.getOutputStream());
        // Send the length of the public key in bytes
        outputStream.writeInt(encoded.length);
        // Send the public key bytes
        outputStream.write(encoded);

        try {
            clientKeyAgreement.doPhase(serverPublicKey, true);
        } catch (InvalidKeyException e){
            e.printStackTrace();
        }

        int sharedSecLen = din.readInt();
        byte[] clientSharedSec = new byte[sharedSecLen];
        try {
            int clientSharedSecLen = clientKeyAgreement.generateSecret(clientSharedSec, 0);
        } catch (ShortBufferException e){
            e.printStackTrace();
        }
        int hexSecLen = toHexString(clientSharedSec).length();
        consoleUI.log("Final key ending in " + toHexString(clientSharedSec).substring(hexSecLen - 5) + " created");

    }
}
