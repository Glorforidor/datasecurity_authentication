package datasecurity_authentication;


public class User {
    private String name, pass, salt;

    public User(String Name, String Pass, String Salt) {
        name = Name;
        pass = Pass;
        salt = Salt;
    }

    public User(String Name, String Pass) {
        name = Name;
        pass = Pass;
    }

    public String getPass() {
        return pass;
    }

    public String getSalt() {
        return salt;
    }

    public String getName() {
        return name;
    }
}
