package com.GoAero.model;

import java.sql.Timestamp;

/**
 * FlightOwner model class representing airline companies in the flight booking system
 */
public class FlightOwner {
    private int ownerId;
    private String companyName;
    private String companyCode;
    private String contactInfo;
    private int flightCount;
    private String password; // For flight owner login
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public FlightOwner() {}

    // Constructor without ID (for new flight owners)
    public FlightOwner(String companyName, String companyCode, String contactInfo, String password) {
        this.companyName = companyName;
        this.companyCode = companyCode;
        this.contactInfo = contactInfo;
        this.password = password;
        this.flightCount = 0;
    }

    // Constructor with all fields
    public FlightOwner(int ownerId, String companyName, String companyCode, String contactInfo,
                       int flightCount, String password, Timestamp createdAt, Timestamp updatedAt) {
        this.ownerId = ownerId;
        this.companyName = companyName;
        this.companyCode = companyCode;
        this.contactInfo = contactInfo;
        this.flightCount = flightCount;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public int getFlightCount() {
        return flightCount;
    }

    public void setFlightCount(int flightCount) {
        this.flightCount = flightCount;
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

    // Utility methods
    public String getDisplayName() {
        return companyCode + " - " + companyName;
    }

    @Override
    public String toString() {
        return "FlightOwner{" +
                "ownerId=" + ownerId +
                ", companyName='" + companyName + '\'' +
                ", companyCode='" + companyCode + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", flightCount=" + flightCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FlightOwner that = (FlightOwner) obj;
        return ownerId == that.ownerId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(ownerId);
    }
}