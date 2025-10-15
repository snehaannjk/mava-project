package com.GoAero.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating unique Passenger Name Record (PNR) codes
 */
public class PNRGenerator {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int PNR_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a unique PNR code
     * Format: 6 random alphanumeric characters
     * @return A unique PNR string
     */
    public static String generatePNR() {
        StringBuilder pnr = new StringBuilder();
        
        for (int i = 0; i < PNR_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            pnr.append(CHARACTERS.charAt(index));
        }
        
        return pnr.toString();
    }

    /**
     * Generates a PNR with timestamp prefix
     * Format: YYMMDD + 6 random characters
     * @return A timestamped PNR string
     */
    public static String generateTimestampedPNR() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyMMdd"));
        
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < PNR_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            randomPart.append(CHARACTERS.charAt(index));
        }
        
        return timestamp + randomPart.toString();
    }

    /**
     * Generates a PNR with airline prefix
     * Format: Airline code + 6 random characters
     * @param airlineCode The airline code (2-3 characters)
     * @return A PNR string with airline prefix
     */
    public static String generatePNRWithAirline(String airlineCode) {
        if (airlineCode == null || airlineCode.trim().isEmpty()) {
            return generatePNR();
        }
        
        String cleanAirlineCode = airlineCode.trim().toUpperCase();
        if (cleanAirlineCode.length() > 3) {
            cleanAirlineCode = cleanAirlineCode.substring(0, 3);
        }
        
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < PNR_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            randomPart.append(CHARACTERS.charAt(index));
        }
        
        return cleanAirlineCode + randomPart.toString();
    }

    /**
     * Validates PNR format
     * @param pnr The PNR to validate
     * @return true if PNR format is valid
     */
    public static boolean isValidPNR(String pnr) {
        if (pnr == null || pnr.trim().isEmpty()) {
            return false;
        }
        
        String cleanPNR = pnr.trim().toUpperCase();
        
        // Check length (should be between 6 and 12 characters)
        if (cleanPNR.length() < 6 || cleanPNR.length() > 12) {
            return false;
        }
        
        // Check that all characters are alphanumeric
        for (char c : cleanPNR.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Formats PNR to standard format (uppercase, no spaces)
     * @param pnr The PNR to format
     * @return Formatted PNR
     */
    public static String formatPNR(String pnr) {
        if (pnr == null) {
            return null;
        }
        return pnr.trim().toUpperCase().replaceAll("\\s+", "");
    }

    /**
     * Generates a booking reference number (longer than PNR)
     * Format: 10 random alphanumeric characters
     * @return A booking reference string
     */
    public static String generateBookingReference() {
        StringBuilder reference = new StringBuilder();
        
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(CHARACTERS.length());
            reference.append(CHARACTERS.charAt(index));
        }
        
        return reference.toString();
    }

    /**
     * Generates a confirmation code
     * Format: 8 random alphanumeric characters
     * @return A confirmation code string
     */
    public static String generateConfirmationCode() {
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        
        return code.toString();
    }

    /**
     * Generates a PNR ensuring it doesn't start with certain characters
     * (avoiding confusion with numbers like 0, O, 1, I)
     * @return A clear PNR string
     */
    public static String generateClearPNR() {
        String clearCharacters = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder pnr = new StringBuilder();
        
        for (int i = 0; i < PNR_LENGTH; i++) {
            int index = random.nextInt(clearCharacters.length());
            pnr.append(clearCharacters.charAt(index));
        }
        
        return pnr.toString();
    }
}
