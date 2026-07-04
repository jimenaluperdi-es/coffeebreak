package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {

    private PasswordUtils() {
    }

    public static String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            byte[] saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);
            return Base64.getEncoder().encodeToString(saltAndHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean checkPassword(String password, String storedHash) {
        try {
            byte[] saltAndHash = Base64.getDecoder().decode(storedHash);
            byte[] salt = new byte[16];
            System.arraycopy(saltAndHash, 0, salt, 0, salt.length);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            byte[] storedHashBytes = new byte[saltAndHash.length - salt.length];
            System.arraycopy(saltAndHash, salt.length, storedHashBytes, 0, storedHashBytes.length);
            return MessageDigest.isEqual(hashedPassword, storedHashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error checking password", e);
        }
    }
}
