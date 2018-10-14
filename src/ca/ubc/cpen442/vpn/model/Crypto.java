package ca.ubc.cpen442.vpn.model;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Crypto {

    public static String getMD5Hash(String string) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // Should not be called
        }
        assert md != null;
        md.update(string.getBytes());
        return DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
    }

    public static String getMD5Hash(byte[] bytes) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // Should not be called
        }
        assert md != null;
        md.update(bytes);
        return DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
    }

}
