package datasecurity_authentication;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionHandler {
    private static String algo = "AES/CBC/PKCS5Padding";
    private static EncryptionHandler instance;
    public static SecretKeySpec key;

    private EncryptionHandler() {
        SecureRandom rand = new SecureRandom();
        byte[] keyBytes = new byte[16];
        rand.nextBytes(keyBytes);

        // create symmetric key
        key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
    }

    public static EncryptionHandler getInstance() {
        if (instance == null) {
            instance = new EncryptionHandler(); 
        }

        return instance;
    }


    /**
     * encrypt encrypts string messages and return a new Message with encrypted data and the IV.
     * @param secretKey the key to encrypt with
     * @param plaintext the plaintext to encrypt
     * @return a new Message
     * @throws Exception
     */
    public Message encrypt(String plaintext) throws Exception {
        return encrypt(plaintext.getBytes("UTF-8"));
    }

    public Message encrypt(byte[] plaintext) throws Exception{
        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] data = cipher.doFinal(plaintext);
        return new Message(data, cipher.getIV());
    }

    public String decrypt(Message msg) throws Exception {
        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(msg.getIv()));

        byte[] data = cipher.doFinal(msg.getData());
        return new String(data);
    }
}