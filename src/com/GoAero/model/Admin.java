package com.GoAero.model;

import java.sql.Timestamp;

/**
 * Admin model class representing administrative users in the flight booking system
 */
public class Admin {
    private int adminId;
    private String username;
    private String password;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public Admin() {}

    // Constructor without ID (for new admins)
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Constructor with all fields
    public Admin(int adminId, String username, String password,
                 Timestamp createdAt, Timestamp updatedAt) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Backward compatibility methods
    public String getPasswordHash() {
        return password;
    }

    public void setPasswordHash(String password) {
        this.password = password;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", username='" + username + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Admin admin = (Admin) obj;
        return adminId == admin.adminId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(adminId);
    }
}