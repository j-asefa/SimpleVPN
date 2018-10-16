import ca.ubc.cpen442.vpn.model.Crypto;
import org.junit.Test;
import javax.crypto.*;
import static org.junit.Assert.*;

public class CryptoTest {
    @Test
    public void testAES() {
        String message = "testmessage";
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (Exception e) {
            System.out.println("error in generating AES");
            return;
        }
        keyGen.init(128); // for example
        SecretKey secretKey = keyGen.generateKey();
        byte[] key = secretKey.getEncoded();
        byte[] cyphertext = null;
        try {
            cyphertext = Crypto.encryptAES(message.getBytes(),key);
        } catch (Exception e) {
            System.out.println("error in encrypting AES");
            return;
        }
        byte[] plaintext = null;
        try {
            plaintext = Crypto.decryptAES(cyphertext,key);
        } catch (Exception e) {
            System.out.println("error in decrypting AES");
            return;
        }
        assertEquals(plaintext, message.getBytes());
    }
}