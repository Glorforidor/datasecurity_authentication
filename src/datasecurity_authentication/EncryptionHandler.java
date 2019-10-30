package datasecurity_authentication;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

public class EncryptionHandler {
    private static String algo = "AES/CBC/PKCS5Padding";

    /**
     * encrypt encrypts string messages and return a new Message with encrypted data and the IV.
     * @param secretKey the key to encrypt with
     * @param plaintext the plaintext to encrypt
     * @return a new Message
     * @throws Exception
     */
    public static Message encrypt(Key secretKey, String plaintext) throws Exception {
        return encrypt(secretKey, plaintext.getBytes("UTF-8"));
    }

    public static Message encrypt(Key secretKey, byte[] plaintext) throws Exception{
        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] data = cipher.doFinal(plaintext);
        return new Message(data, cipher.getIV());
    }

    public static String decrypt(Key secretKey, Message msg) throws Exception {
        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(msg.getIv()));

        byte[] data = cipher.doFinal(msg.getData());
        return new String(data);
    }
}