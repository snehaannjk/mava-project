package com.GoAero.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A utility class to handle the database connection.
 * This ensures we have a single, centralized point for managing the connection.
 */
public class DBConnection {

    // --- Database Credentials ---
    // URL format: jdbc:mysql://hostname:port/databasename
    private static final String DB_URL = "jdbc:mysql://localhost:3306/goAero";

    // Replace with your MySQL username (often 'root')
    private static final String USER = "root";

    // Replace with your MySQL password
    private static final String PASS = "QWERTY";
    // ----------------------------

    private static Connection connection = null;

    /**
     * Creates and returns a connection to the database.
     * This method is static so we can call it from anywhere without creating an object.
     * @return A Connection object or null if connection fails.
     */
    public static Connection getConnection() {
        try {
            // Register the MySQL JDBC driver.
            // This line is technically not required for modern JDBC drivers (4.0+),
            // but it's good practice for compatibility.
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
             System.out.println("Database connected successfully!");

        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
        return connection;
    }
}
