package ca.ubc.cpen442.vpn.model;

import javax.xml.bind.DatatypeConverter;
import java.security.*;
import javax.crypto.*;

public class Crypto {

    public static String getSHA2Hash(String string) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // Should not be called
        }
        assert md != null;
        md.update(string.getBytes());
        return DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
    }

    public static String getSHA2Hash(byte[] bytes) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // Should not be called
        }
        assert md != null;
        md.update(bytes);
        return DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
    }

    public static byte[] getHMAC (String string, SecretKey key) throws InvalidKeyException {
        // Generate secret key for HmacSHA256
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            // Should not be called
        }

        // Get instance of Mac object implementing HmacSHA256, and
        // initialize it with key
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            // Should not be called
        }

        mac.init(key);
        return mac.doFinal(string.getBytes());
    }
}
