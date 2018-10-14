package ca.ubc.cpen442.vpn.model;

import ca.ubc.cpen442.vpn.ui.VPNConsoleUI;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

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
        byte[] serverPublicKeyBytes = null; // placeholder
        // TODO connect to the client and obtain the actual serverPublicKeyBytes
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
            // should never get called
        }
        assert serverPublicKey != null;
        DHParameterSpec params = ((DHPublicKey) serverPublicKey).getParams();
        System.out.println("Client will generate its keypair based on the received server public key");
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            // should never get called
        }
        assert keyPairGenerator != null;
        try {
            keyPairGenerator.initialize(params);
        } catch (InvalidAlgorithmParameterException e) {
            // should never get called
        }
        KeyPair clientKeyPair = keyPairGenerator.generateKeyPair();
        KeyAgreement clientKeyAgreement = null;
        try {
            clientKeyAgreement = KeyAgreement.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            // should never get called
        }
        assert clientKeyAgreement != null;
        try {
            clientKeyAgreement.init(clientKeyPair.getPrivate());
        } catch (InvalidKeyException e) {
            // should never get called
        }
        // this public key is ready to be sent to the server
        byte[] encoded = clientKeyPair.getPublic().getEncoded();
    }
}
