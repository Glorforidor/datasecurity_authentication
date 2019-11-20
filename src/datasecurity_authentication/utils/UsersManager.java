package datasecurity_authentication.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datasecurity_authentication.models.User;

/**
 * UsersManager manages the creation of the users.csv and populate it with
 * predefined information. It is also used to read the users.csv.
 */
public class UsersManager {
    private static final String passwdFile = "users.csv";
    private static final String aclFile = "acl.policy";

    private static String getSaltedHash(String pass, String salt) {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sha.update((salt + pass).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(sha.digest());
    }

    /**
     * createUsersFile creates the users.csv and populate the file with predifined
     * users.
     */
    public static void createUsersFile() {
        SecureRandom rand = new SecureRandom();
        // create salts
        byte[] salt = new byte[128];
        byte[] salt1 = new byte[128];
        byte[] salt2 = new byte[128];
        byte[] salt3 = new byte[128];
        byte[] salt4 = new byte[128];
        byte[] salt5 = new byte[128];
        byte[] salt6 = new byte[128];
        byte[] salt7 = new byte[128];
        rand.nextBytes(salt);
        rand.nextBytes(salt1);
        rand.nextBytes(salt2);
        rand.nextBytes(salt3);
        rand.nextBytes(salt4);
        rand.nextBytes(salt5);
        rand.nextBytes(salt6);
        rand.nextBytes(salt7);

        List<List<String>> rows = Arrays.asList(
                Arrays.asList("alice", getSaltedHash("123", Base64.getEncoder().encodeToString(salt)),
                        Base64.getEncoder().encodeToString(salt)),
                Arrays.asList("ida", getSaltedHash("123", Base64.getEncoder().encodeToString(salt1)),
                        Base64.getEncoder().encodeToString(salt1)),
                Arrays.asList("cecilia", getSaltedHash("123", Base64.getEncoder().encodeToString(salt2)),
                        Base64.getEncoder().encodeToString(salt2)),
                Arrays.asList("erica", getSaltedHash("123", Base64.getEncoder().encodeToString(salt3)),
                        Base64.getEncoder().encodeToString(salt3)),
                Arrays.asList("david", getSaltedHash("123", Base64.getEncoder().encodeToString(salt4)),
                        Base64.getEncoder().encodeToString(salt4)),
                Arrays.asList("fred", getSaltedHash("123", Base64.getEncoder().encodeToString(salt5)),
                        Base64.getEncoder().encodeToString(salt5)),
                Arrays.asList("george", getSaltedHash("123", Base64.getEncoder().encodeToString(salt6)),
                        Base64.getEncoder().encodeToString(salt6)),
                Arrays.asList("henry", getSaltedHash("123", Base64.getEncoder().encodeToString(salt7)),
                        Base64.getEncoder().encodeToString(salt7)));

        try (FileWriter fw = new FileWriter(passwdFile)) {
            fw.append("name");
            fw.append(",");
            fw.append("password");
            fw.append(",");
            fw.append("salt");
            fw.append("\n");

            for (List<String> rowData : rows) {
                fw.append(String.join(",", rowData));
                fw.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * readUsers reads the users.csv and return a list of the users within.
     * 
     * @return list of users
     */
    public static ArrayList<User> readUsers() {
        var list = new ArrayList<User>();
        try (BufferedReader br = new BufferedReader(new FileReader(passwdFile))) {
            String row;
            while ((row = br.readLine()) != null) {
                String[] data = row.split(",");
                // Skip header line
                if (data[0].equals("Name")) {
                    continue;
                }
                list.add(new User(data[0], data[1], data[2]));
            }
        } catch (IOException e) {
            // TODO: better exception handling
            e.printStackTrace();
        }
        return list;
    }

    private static Map<String, String> readACL(String filename) {
        Map<String, String> m = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String row;
            while ((row = br.readLine()) != null) {
                String[] data = row.split(":");
                m.put(data[0], data[1]);
            }
        } catch (IOException e) {
            // TODO: better exception handling
            e.printStackTrace();
        }

        return m;
    }

    public static boolean isOperationAllowed(String username, String operation) {
        Map<String, String> userToOperation = readACL(aclFile);

        var operations = userToOperation.get(username);

        if (operations == null) {
            return false;
        }

        var found = false;
        var splitOperations = operations.split(",");
        for (String op : splitOperations) {
            if (op.equals(operation)) {
                found = true;
                break;
            }
        }

        return found;
    }
}
