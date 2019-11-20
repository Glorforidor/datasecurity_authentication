package datasecurity_authentication.models;

/**
 * User encapsulate the information about a user.
 */
public class User {
    private String name;
    private String pass;
    private String salt;
    private String role;

    /**
     * User constructs a new User with name and pass.
     * @param name name of the user.
     * @param pass password of the user.
     */
    public User(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    /**
     * User constructs a new User with name, pass and salt.
     * This constructor is mostly usefull when loading up users from users file.
     * @param name name of the user.
     * @param pass password of the user.
     * @param salt the salt used with the hashing of the password.
     */
    public User(String name, String pass, String salt) {
        this(name, pass);
        this.salt = salt;
    }

    /**
     * User constructs a new User with name, pass and salt.
     * This constructor is mostly usefull when loading up users from users file.
     * @param name name of the user.
     * @param pass password of the user.
     * @param salt the salt used with the hashing of the password.
     * @param role role of the user.
     */
    public User(String name, String pass, String salt, String role) {
        this(name, pass, salt);
        this.role = role;
    }

    /**
     * getPass returns the password of the user.
     * @return the password of the user.
     */
    public String getPass() {
        return pass;
    }

    /**
     * getSalt returns the salt.
     * @return the salt which was used to hash the password.
     */
    public String getSalt() {
        return salt;
    }

    /**
     * getName returns the name of the user.
     * @return the name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * getRole returns the role of the user.
     * @return the role of the user.
     */
    public String getRole() {return role; }
}
