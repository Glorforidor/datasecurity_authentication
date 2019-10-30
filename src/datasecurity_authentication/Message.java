package datasecurity_authentication;

public class Message {
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