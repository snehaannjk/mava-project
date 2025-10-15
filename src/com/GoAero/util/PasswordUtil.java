package com.GoAero.util;

import java.util.Random;

/**
 * Utility class for password validation and generation (plain-text storage)
 */
public class PasswordUtil {

    private static final Random random = new Random();

    /**
     * Stores a password as plain text (no hashing)
     * @param password The plain text password
     * @return The same password (no transformation)
     */
    public static String storePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return password;
    }

    /**
     * Hashes a password (alias for storePassword for backward compatibility)
     * @param password The plain text password
     * @return The same password (no transformation)
     */
    public static String hashPassword(String password) {
        return storePassword(password);
    }

    /**
     * Verifies a password against a stored password
     * @param password The plain text password to verify
     * @param storedPassword The stored plain text password
     * @return true if the passwords match, false otherwise
     */
    public static boolean verifyPassword(String password, String storedPassword) {
        if (password == null || storedPassword == null) {
            return false;
        }
        return password.equals(storedPassword);
    }

    /**
     * Validates password strength
     * @param password The password to validate
     * @return true if password meets minimum requirements
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        // Check for at least one letter and one number
        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        return hasLetter && hasDigit;
    }

    /**
     * Gets password requirements message
     * @return String describing password requirements
     */
    public static String getPasswordRequirements() {
        return "";
    }

    /**
     * Generates a random password
     * @param length The length of the password to generate
     * @return A randomly generated password
     */
    public static String generateRandomPassword(int length) {
        if (length < 6) {
            length = 6;
        }

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();

        // Ensure at least one letter and one number
        password.append(chars.charAt(random.nextInt(26))); // Uppercase letter
        password.append(chars.charAt(random.nextInt(26) + 26)); // Lowercase letter
        password.append(chars.charAt(random.nextInt(10) + 52)); // Number

        // Fill the rest randomly
        for (int i = 3; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        // Shuffle the password
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }
}
