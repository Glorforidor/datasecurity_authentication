package datasecurity_authentication;

import java.security.SecureRandom;

/**
 * Session encapsulates the token and the count that makes up the Session.
 */
public class Session {
    private byte[] token;
    private int count;

    /**
     * Session constructs a new Session with a random generated token and a count of
     * 0.
     */
    public Session() {
        this(generateSessionToken(), 0);
    }

    /**
     * Session constructs a new Session with the token and count.
     *
     * @param token the unique token
     * @param count the session count
     */
    public Session(byte[] token, int count) {
        this.token = token;
        this.count = count;
    }

    /**
     * generateSessionToken generate a random token.
     *
     * @return a random generated token.
     */
    public static byte[] generateSessionToken() {
        SecureRandom sr = new SecureRandom();
        byte[] token = new byte[32];
        sr.nextBytes(token);
        return token;
    }

    /**
     * incrementCount increaments the count by one.
     */
    public void incrementCount() {
        count++;
    }

    /**
     * getToken returns the token.
     *
     * @return the session token.
     */
    public byte[] getToken() {
        return token;
    }

    /**
     * getCount returns the count.
     *
     * @return the session count.
     */
    public int getCount() {
        return count;
    }
}