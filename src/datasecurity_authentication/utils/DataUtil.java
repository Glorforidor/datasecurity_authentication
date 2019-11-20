package datasecurity_authentication.utils;

import datasecurity_authentication.models.Session;
import datasecurity_authentication.models.User;

import java.nio.ByteBuffer;

/**
 * DataUtil is a utility class to combine byte arrays into classes aswell to
 * split classes into byte arrays.
 * 
 * This is going to be used when encrypting/decrypting of a class data.
 */
public class DataUtil {
    private static DataUtil instance;

    private DataUtil(){}

    public static DataUtil getInstance() {
        if (instance == null) {
            instance = new DataUtil();
        }

        return instance;
    }

    /**
     * CombineToSession combines the data into a Session.
     * 
     * @param data the data to split. The data must be of length 64, where the first
     *             32 bytes are the token and last 32 bytes are the count.
     * @return the Session from the data.
     */
    public Session combineToSession(byte[] data) {
        byte[] token = new byte[32];
        byte[] count = new byte[32];
        System.arraycopy(data, 0, token, 0, 32);
        System.arraycopy(data, 32, count, 0, 32);
        int c = ByteBuffer.wrap(count).getInt();
        return new Session(token, c);
    }

    /**
     * combineToUser combines the data into a User. 
     * 
     * @param data the data to split. The data must be of length 64, where the first
     *             32 bytes are the username and the last 32 bytes are the password.
     * @return the User from the data.
     */
    public User combineToUser(byte[] data) {
        byte[] username = new byte[32];
        byte[] password = new byte[32];
        System.arraycopy(data, 0, username, 0, 32);
        System.arraycopy(data, 32, password, 0, 32);
        return new User(new String(username).trim(), new String(password).trim());
    }

    /**
     * splitSession splits the Session into a data byte array.
     * @param session the Session to split.
     * @return byte array of the splitted Session.
     */
    public byte[] splitSession(Session session) {
        byte[] data = new byte[64];
        System.arraycopy(session.getToken(), 0, data, 0, 32);
        byte[] count = ByteBuffer.allocate(32).putInt(session.getCount()).array();
        System.arraycopy(count, 0, data, 32, 32);
        return data;
    }

    /**
     * incrementAndSplitSession increments the Session count and splits Session into a data byte array.
     * @param session the Session to increment and split.
     * @return byte array of the splitted Session.
     */
    public byte[] incrementAndSplitSession(Session session) {
        session.incrementCount();
        return splitSession(session);
    }

    /**
     * splitUser splits the username and password into a data byte array.
     * @param username the username to split.
     * @param password the password to split.
     * @return byte array of the splitted username nad password.
     */
    public byte[] splitUser(String username, String password) {
        byte[] data = new byte[64];
        var user = ByteBuffer.allocate(32).put(username.getBytes()).array();
        var pass = ByteBuffer.allocate(32).put(password.getBytes()).array();
        System.arraycopy(user, 0, data, 0, 32);
        System.arraycopy(pass, 0, data, 32, 32);
        return data;
    }
}