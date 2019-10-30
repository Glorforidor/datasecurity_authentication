package datasecurity_authentication;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DatabaseManager {

    // TODO: make a better key
    private static String passwdFile = "users";
    private static SecretKeySpec aesKey;
    private static String algo = "AES/CBC/PKCS5Padding";
    private static byte[] iv;

    public static void createDatabaseFile() {
        SecureRandom rand = new SecureRandom();
        byte[] keyBytes = new byte[16];
        rand.nextBytes(keyBytes);

        // create symmetric key
        aesKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");

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

        StringBuilder sb = new StringBuilder();
        sb.append("Name");
        sb.append(",");
        sb.append("Password");
        sb.append(",");
        sb.append("Salt");
        sb.append("\n");

        for (List<String> rowData : rows) {
            sb.append(String.join(",", rowData));
            sb.append("\n");
        }
        var inputBytes = sb.toString().getBytes();

        try (FileOutputStream outputStream = new FileOutputStream(passwdFile)) {
            Cipher cipher = Cipher.getInstance(algo);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);

            byte[] outputBytes = cipher.doFinal(inputBytes);
            iv = cipher.getIV();
            outputStream.write(outputBytes);
        } catch (IOException e) {

        } catch (InvalidKeyException e) {
            // TODO: better exception handling
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO: better exception handling
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO: better exception handling
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO: better exception handling
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO: better exception handling
            e.printStackTrace();
        } 
    }

    public static LinkedList<User> decrypt() {
        var map = new LinkedList<User>();
        try (FileInputStream fis = new FileInputStream(passwdFile)) {
            Cipher cipher = Cipher.getInstance(algo);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
            File file = new File(passwdFile);
            byte[] inputBytes = new byte[(int) file.length()];
            fis.read(inputBytes);

            var decrypted = cipher.doFinal(inputBytes);

            BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(decrypted)));
            String row;
            while ((row = br.readLine()) != null) {
                String[] data = row.split(",");
                // Skip header line
                if (data[0].equals("Name")){
                    continue;
                }
                map.add(new User(data[0], data[1], data[2]));
            }

            br.close();
        } catch (IOException e) {
            // TODO: better exception handling
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO: better exception handling
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO: better exception handling
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO: better exception handling
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return map;
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
