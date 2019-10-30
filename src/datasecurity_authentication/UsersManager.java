package datasecurity_authentication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class UsersManager {

    // TODO: make a better key
    private static String passwdFile = "users.csv";

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

    public static void createDatabaseFile() {
        SecureRandom rand = new SecureRandom();
        // create salts
        byte[] salt = new byte[128];
        byte[] salt1 = new byte[128];
        byte[] salt2 = new byte[128];
        rand.nextBytes(salt);
        rand.nextBytes(salt1);
        rand.nextBytes(salt2);

        List<List<String>> rows = Arrays.asList(
                Arrays.asList("Soren", getSaltedHash("123", Base64.getEncoder().encodeToString(salt)),
                        Base64.getEncoder().encodeToString(salt)),
                Arrays.asList("David", getSaltedHash("456", Base64.getEncoder().encodeToString(salt)),
                        Base64.getEncoder().encodeToString(salt1)),
                Arrays.asList("Scott", getSaltedHash("789", Base64.getEncoder().encodeToString(salt)),
                        Base64.getEncoder().encodeToString(salt2)));

        try (FileWriter fw = new FileWriter(passwdFile)) {
            fw.append("Name");
            fw.append(",");
            fw.append("Password");
            fw.append(",");
            fw.append("Salt");
            fw.append("\n");

            for (List<String> rowData : rows) {
                fw.append(String.join(",", rowData));
                fw.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LinkedList<User> readUsers() {
        var list = new LinkedList<User>();
        try (BufferedReader br = new BufferedReader(new FileReader(passwdFile))) {
            String row;
            while ((row = br.readLine()) != null) {
                String[] data = row.split(",");
                // Skip header line
                if (data[0].equals("Name")){
                    continue;
                }
                list.add(new User(data[0], data[1], data[2]));
            }
        } catch(IOException e) {
            // TODO: better exception handling
            e.printStackTrace();
        }
        return list;
    }
}
