package datasecurity_authentication;

import java.io.Serializable;

/**
 * Message is used to encapsulate encrypted data with the initialization vector
 * that was used for encrypting the data.
 */
public class Message implements Serializable {
    /**
     * ensure that the class is serialised correctly
     */
    private static final long serialVersionUID = -25555653311404592L;
    private byte[] data;
    private byte[] iv;

    /**
     * Message constructs a new Message with encrypted data and the initialization vector.
     * 
     * @param data data that should be encrypted.
     * @param iv   the used initialization vector of the encrypted data.
     */
    public Message(byte[] data, byte[] iv) {
        this.data = data;
        this.iv = iv;
    }

    /**
     * getIv returns the initialization vector which was used to encrypt the data.
     * 
     * @return initialization vector
     */
    public byte[] getIv() {
        return iv;
    }

    /**
     * getData returns the encrypted data.
     * 
     * @return data
     */
    public byte[] getData() {
        return data;
    }
}