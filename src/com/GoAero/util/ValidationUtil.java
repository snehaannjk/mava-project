package com.GoAero.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtil {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // Phone number pattern (supports various formats)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[^A-Za-z]{10,15}$" // accept digits and common separators pre-cleaning
    );
    
    // Airport code pattern (3-4 uppercase letters)
    private static final Pattern AIRPORT_CODE_PATTERN = Pattern.compile(
        "^[A-Z]{3,4}$"
    );
    
    // Flight code: no regex validation anymore; auto-uppercase only (see formatFlightCode)
    // private static final Pattern FLIGHT_CODE_PATTERN = Pattern.compile(
    //     "^[A-Z]{2,3}[0-9]{1,4}$"
    // );

    // Company code pattern (2-5 uppercase letters/numbers)
    private static final Pattern COMPANY_CODE_PATTERN = Pattern.compile(
        "^[A-Z0-9]{2,5}$"
    );

    /**
     * Validates email format
     * @param email The email to validate
     * @return true if email is valid
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates phone number format
     * @param phone The phone number to validate
     * @return true if phone number is valid
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String cleanPhone = phone.replaceAll("[\\s()-]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    /**
     * Validates airport code format
     * @param code The airport code to validate
     * @return true if airport code is valid
     */
    public static boolean isValidAirportCode(String code) {
        return code != null && AIRPORT_CODE_PATTERN.matcher(code.trim().toUpperCase()).matches();
    }

    /**
     * Validates flight code format
     * @param code The flight code to validate
     * @return true (no validation; flight code is auto-uppercased only)
     */
    public static boolean isValidFlightCode(String code) {
        // No validation: accept any input. Callers should use formatFlightCode to uppercase.
        return true;
    }

    /**
     * Validates company code format
     * @param code The company code to validate
     * @return true if company code is valid
     */
    public static boolean isValidCompanyCode(String code) {
        return code != null && COMPANY_CODE_PATTERN.matcher(code.trim().toUpperCase()).matches();
    }

    /**getFlightCodeErrorMessage()
     * Validates that a string is not null or empty
     * @param value The string to validate
     * @return true if string is not null or empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates that a string has minimum length
     * @param value The string to validate
     * @param minLength The minimum length required
     * @return true if string meets minimum length
     */
    public static boolean hasMinLength(String value, int minLength) {
        return value != null && value.trim().length() >= minLength;
    }

    /**
     * Validates that a string has maximum length
     * @param value The string to validate
     * @param maxLength The maximum length allowed
     * @return true if string is within maximum length
     */
    public static boolean hasMaxLength(String value, int maxLength) {
        return value == null || value.trim().length() <= maxLength;
    }

    /**
     * Validates that a number is positive
     * @param value The number to validate
     * @return true if number is positive
     */
    public static boolean isPositive(int value) {
        return value > 0;
    }

    /**
     * Validates that a number is non-negative
     * @param value The number to validate
     * @return true if number is non-negative
     */
    public static boolean isNonNegative(int value) {
        return value >= 0;
    }

    /**
     * Validates date of birth (must be in the past and person must be at least 12 years old)
     * @param dateOfBirth The date of birth to validate
     * @return true if date of birth is valid
     */
    public static boolean isValidDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return false;
        
        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusYears(12); // Minimum age 12
        LocalDate maxDate = now.minusYears(120); // Maximum age 120
        
        return dateOfBirth.isBefore(minDate) && dateOfBirth.isAfter(maxDate);
    }

    /**
     * Validates that a date is in the future
     * @param date The date to validate
     * @return true if date is in the future
     */
    public static boolean isFutureDate(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    /**
     * Validates price (must be positive and reasonable)
     * @param price The price to validate
     * @return true if price is valid
     */
    public static boolean isValidPrice(double price) {
        return price > 0 && price <= 100000; // Max price $100,000
    }

    /**
     * Validates capacity (must be positive and reasonable for aircraft)
     * @param capacity The capacity to validate
     * @return true if capacity is valid
     */
    public static boolean isValidCapacity(int capacity) {
        return capacity > 0 && capacity <= 1000; // Max capacity 1000 passengers
    }

    /**
     * Cleans and formats phone number
     * @param phone The phone number to clean
     * @return Cleaned phone number
     */
    public static String cleanPhoneNumber(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("[\\s()-]", "");
    }

    /**
     * Formats airport code to uppercase
     * @param code The airport code to format
     * @return Formatted airport code
     */
    public static String formatAirportCode(String code) {
        if (code == null) return null;
        return code.trim().toUpperCase();
    }

    /**
     * Formats flight code to uppercase
     * @param code The flight code to format
     * @return Formatted flight code
     */
    public static String formatFlightCode(String code) {
        if (code == null) return null;
        return code.trim().toUpperCase();
    }

    /**
     * Formats company code to uppercase
     * @param code The company code to format
     * @return Formatted company code
     */
    public static String formatCompanyCode(String code) {
        if (code == null) return null;
        return code.trim().toUpperCase();
    }

    /**
     * Validates and parses a date string
     * @param dateString The date string to parse
     * @return LocalDate if valid, null otherwise
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateString.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Gets validation error message for email
     * @return Error message for invalid email
     */
    public static String getEmailErrorMessage() {
        return "Please enter a valid email address (e.g., user@example.com)";
    }

    /**
     * Gets validation error message for phone
     * @return Error message for invalid phone
     */
    public static String getPhoneErrorMessage() {
        return "Please enter a valid phone number (10-15 digits)";
    }

    /**
     * Gets validation error message for airport code
     * @return Error message for invalid airport code
     */
    public static String getAirportCodeErrorMessage() {
        return "Airport code must be 3-4 uppercase letters (e.g., JFK, LAX)";
    }

    /**
     * Gets validation error message for flight code
     * @return Info message (validation disabled)
     */
    public static String getFlightCodeErrorMessage() {
        return "Flight code accepts any format and will be auto-uppercased.";
    }

    /**
     * Gets validation error message for company code
     * @return Error message for invalid company code
     */
    public static String getCompanyCodeErrorMessage() {
        return "Company code must be 2-5 uppercase letters/numbers (e.g., AA, BA, DL123)";
    }
}
