package datasecurity_authentication;

import java.io.Serializable;

public class Message implements Serializable {

    private byte[] data;
    private byte[] iv;


    public Message(byte[] data, byte[] iv) {
        this.data = data;
        this.iv = iv;
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getData() {
        return data;
    }
}