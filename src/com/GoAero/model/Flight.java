package com.GoAero.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Flight model class representing flights in the flight booking system
 */
public class Flight {
    private int flightId;
    private int companyId;
    private String flightCode;
    private String flightName;
    private int capacity;
    private int departureAirportId;
    private int destinationAirportId;
    private LocalDateTime departureTime;
    private LocalDateTime destinationTime;
    private BigDecimal price;
    
    // Additional fields for display purposes (not in database)
    private String companyName;
    private String companyCode;
    private String departureAirportCode;
    private String departureAirportName;
    private String departureCity;
    private String destinationAirportCode;
    private String destinationAirportName;
    private String destinationCity;
    private int availableSeats;

    // Default constructor
    public Flight() {}

    // Constructor without ID (for new flights)
    public Flight(int companyId, String flightCode, String flightName, int capacity,
                  int departureAirportId, int destinationAirportId, 
                  LocalDateTime departureTime, LocalDateTime destinationTime, BigDecimal price) {
        this.companyId = companyId;
        this.flightCode = flightCode;
        this.flightName = flightName;
        this.capacity = capacity;
        this.departureAirportId = departureAirportId;
        this.destinationAirportId = destinationAirportId;
        this.departureTime = departureTime;
        this.destinationTime = destinationTime;
        this.price = price;
        this.availableSeats = capacity; // Initially all seats are available
    }

    // Constructor with all fields
    public Flight(int flightId, int companyId, String flightCode, String flightName, int capacity,
                  int departureAirportId, int destinationAirportId, 
                  LocalDateTime departureTime, LocalDateTime destinationTime, BigDecimal price) {
        this.flightId = flightId;
        this.companyId = companyId;
        this.flightCode = flightCode;
        this.flightName = flightName;
        this.capacity = capacity;
        this.departureAirportId = departureAirportId;
        this.destinationAirportId = destinationAirportId;
        this.departureTime = departureTime;
        this.destinationTime = destinationTime;
        this.price = price;
        this.availableSeats = capacity; // Default to full capacity
    }

    // Getters and Setters
    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    public String getFlightName() {
        return flightName;
    }

    public void setFlightName(String flightName) {
        this.flightName = flightName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getDepartureAirportId() {
        return departureAirportId;
    }

    public void setDepartureAirportId(int departureAirportId) {
        this.departureAirportId = departureAirportId;
    }

    public int getDestinationAirportId() {
        return destinationAirportId;
    }

    public void setDestinationAirportId(int destinationAirportId) {
        this.destinationAirportId = destinationAirportId;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getDestinationTime() {
        return destinationTime;
    }

    public void setDestinationTime(LocalDateTime destinationTime) {
        this.destinationTime = destinationTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    // Additional display fields getters and setters
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

    public String getDepartureAirportCode() {
        return departureAirportCode;
    }

    public void setDepartureAirportCode(String departureAirportCode) {
        this.departureAirportCode = departureAirportCode;
    }

    public String getDepartureAirportName() {
        return departureAirportName;
    }

    public void setDepartureAirportName(String departureAirportName) {
        this.departureAirportName = departureAirportName;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getDestinationAirportCode() {
        return destinationAirportCode;
    }

    public void setDestinationAirportCode(String destinationAirportCode) {
        this.destinationAirportCode = destinationAirportCode;
    }

    public String getDestinationAirportName() {
        return destinationAirportName;
    }

    public void setDestinationAirportName(String destinationAirportName) {
        this.destinationAirportName = destinationAirportName;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    // Utility methods
    public String getRoute() {
        return (departureAirportCode != null ? departureAirportCode : "DEP") + 
               " → " + 
               (destinationAirportCode != null ? destinationAirportCode : "DEST");
    }

    public String getFullRoute() {
        return (departureCity != null ? departureCity : "Departure") + 
               " → " + 
               (destinationCity != null ? destinationCity : "Destination");
    }

    public boolean isAvailable() {
        return availableSeats > 0;
    }

    public double getOccupancyRate() {
        if (capacity == 0) return 0.0;
        return ((double) (capacity - availableSeats) / capacity) * 100.0;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightId=" + flightId +
                ", flightCode='" + flightCode + '\'' +
                ", flightName='" + flightName + '\'' +
                ", capacity=" + capacity +
                ", availableSeats=" + availableSeats +
                ", departureTime=" + departureTime +
                ", destinationTime=" + destinationTime +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Flight flight = (Flight) obj;
        return flightId == flight.flightId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(flightId);
    }
}
