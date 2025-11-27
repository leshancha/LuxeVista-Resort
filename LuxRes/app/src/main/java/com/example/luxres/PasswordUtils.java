package com.example.luxres;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import android.util.Log; // For logging errors

/**
 * Basic Password Hashing Utility (Example Only).
 * WARNING: Uses simple SHA-256 without salt. NOT recommended for production.
 * Use libraries like bcrypt or Argon2 for secure password storage.
 */
public class PasswordUtils {

    private static final String TAG = "PasswordUtils";

    /**
     * Hashes a password using SHA-256.
     * @param password The plain text password.
     * @return The SHA-256 hash as a hexadecimal string, or null on error.
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        try {
            // Get SHA-256 MessageDigest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Generate the hash bytes
            byte[] encodedhash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));

            // Convert byte array into signum representation
            // Convert byte array into hex string
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing password: SHA-256 algorithm not found.", e);
            return null; // Handle error appropriately in your app
        }
    }

    /**
     * Verifies a plain text password against a stored hash.
     * @param plainPassword The password entered by the user.
     * @param storedHash The hash retrieved from the database.
     * @return true if the passwords match, false otherwise.
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) {
            return false;
        }
        String newHash = hashPassword(plainPassword);
        return newHash != null && newHash.equals(storedHash);
    }
}
