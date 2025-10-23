package com.GoAero.model;

/**
 * Airport model class representing airports in the flight booking system
 */
public class Airport {
    private int airportId;
    private String airportCode;
    private String airportName;
    private String city;
    private String country;

    // Default constructor
    public Airport() {}

    // Constructor without ID (for new airports)
    public Airport(String airportCode, String airportName, String city, String country) {
        this.airportCode = airportCode;
        this.airportName = airportName;
        this.city = city;
        this.country = country;
    }

    // Constructor with all fields
    public Airport(int airportId, String airportCode, String airportName, 
                   String city, String country) {
        this.airportId = airportId;
        this.airportCode = airportCode;
        this.airportName = airportName;
        this.city = city;
        this.country = country;
    }

    // Getters and Setters
    public int getAirportId() {
        return airportId;
    }

    public void setAirportId(int airportId) {
        this.airportId = airportId;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    // Utility methods
    public String getDisplayName() {
        return airportCode + " - " + airportName + " (" + city + ", " + country + ")";
    }

    public String getLocation() {
        return city + ", " + country;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "airportId=" + airportId +
                ", airportCode='" + airportCode + '\'' +
                ", airportName='" + airportName + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Airport airport = (Airport) obj;
        return airportId == airport.airportId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(airportId);
    }
}