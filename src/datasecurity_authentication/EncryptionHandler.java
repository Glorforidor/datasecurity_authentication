package datasecurity_authentication;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * EncryptionHandler handles all encryption and
 */
public class EncryptionHandler {
    private static String algo = "AES/CBC/PKCS5Padding";
    private static EncryptionHandler instance;
    public static SecretKeySpec key;

    private EncryptionHandler() {
        // SecureRandom rand = new SecureRandom();

        // It would be better if we did a DH key exchange to agree on a symmetric key
        // instead of hardcoding.
        // The selected bytes is of the most secure and unguessable password string...
        // :P
        byte[] keyBytes = { 80, 97, 36, 36, 119, 48, 114, 100, 33, 49, 50, 51, 52, 53, 54, 55 };
        // rand.nextBytes(keyBytes);

        // create symmetric key
        key = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
    }

    // lets ensure that only one EncryptionHandler exists. At this point it does not
    // make sense to have multiple of them.
    public static EncryptionHandler getInstance() {
        if (instance == null) {
            instance = new EncryptionHandler();
        }

        return instance;
    }

    /**
     * encrypt encrypts the data and return a new Message with encrypted data and
     * the IV.
     *
     * @param data the data to encrypt
     * @return a new Message
     * @throws Exception
     */
    public Message encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] cipherData = cipher.doFinal(data);
        return new Message(cipherData, cipher.getIV());
    }

    /**
     * decrypt decrypts the data withheld in the Message object and returns the
     * decrypted data.
     *
     * @param msg the Message to decrypt
     * @return the decrypted data
     * @throws Exception
     */
    public byte[] decrypt(Message msg) throws Exception {
        Cipher cipher = Cipher.getInstance(algo);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(msg.getIv()));

        byte[] data = cipher.doFinal(msg.getData());
        return data;
    }
}