package ca.ubc.cpen442.vpn.model;

import javax.xml.bind.DatatypeConverter;
import java.security.*;
import javax.crypto.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

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

    public static byte[] getHMAC (String string) throws InvalidKeyException {
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
        SecretKey key = kg.generateKey();
        mac.init(key);
        return mac.doFinal(string.getBytes());
    }

    /**
     * Uses AES to encrypt the given byte array using the given key
     *
     * @param cipherText The data to decrypt
     * @param key key to use for AES encryption.
     */
    public static byte[] encryptAES(byte[] plainText, byte[] key) throws Exception
    {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(plainText);
    }

    /**
     * Uses AES to decrypt the given byte array using the given key.
     *
     * @param cipherText The data to decrypt
     * @param key key to use for AES decryption.
     */
    public static byte[] decryptAES(byte[] cipherText, byte[] key) throws Exception
    {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(cipherText);
    }
}
