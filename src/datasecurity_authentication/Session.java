package datasecurity_authentication;

public class Session {
    private byte[] token;
    private int count;

    public Session(byte[] token, int count) {
        this.token = token;
        this.count = count;
    }

    public void incrementCount() {
        count++;
    }

    public byte[] getToken() {
        return token;
    }
    public int getCount() {
        return count;
    }
}