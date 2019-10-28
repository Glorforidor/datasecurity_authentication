package datasecurity_authentication;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class DatabaseManager {
    public static void main(String[] args) {
        SecureRandom rand = new SecureRandom();
        byte[] salt = new byte[128];
        byte[] salt1 = new byte[128];
        byte[] salt2 = new byte[128];
        rand.nextBytes(salt);
        rand.nextBytes(salt1);
        rand.nextBytes(salt2);


        List<List<String>> rows = Arrays.asList(
                Arrays.asList("Soren", getSaltedHash("123", Base64.getEncoder().encodeToString(salt)),  Base64.getEncoder().encodeToString(salt)),
                Arrays.asList("David", getSaltedHash("456", Base64.getEncoder().encodeToString(salt)), Base64.getEncoder().encodeToString(salt1)),
                Arrays.asList("Scott", getSaltedHash("789", Base64.getEncoder().encodeToString(salt)),  Base64.getEncoder().encodeToString(salt2))
        );

        try (FileWriter csvWriter = new FileWriter("users.csv")) {
            csvWriter.append("Name");
            csvWriter.append(",");
            csvWriter.append("Password");
            csvWriter.append(",");
            csvWriter.append("Salt");
            csvWriter.append("\n");

            for (List<String> rowData : rows) {
                csvWriter.append(String.join(",", rowData));
                csvWriter.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

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



}
