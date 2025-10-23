package com.GoAero.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Booking model class representing flight bookings in the flight booking system
 */
public class Booking {
    private int bookingId;
    private int userId;
    private int flightId;
    private int departureAirportId;
    private int destinationAirportId;
    private LocalDateTime departureTime;
    private LocalDateTime destinationTime;
    private String pnr;
    private LocalDate dateOfDeparture;
    private LocalDate dateOfDestination;
    private Timestamp dateOfBooking;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private BookingStatus bookingStatus;
    
    // Additional fields for display purposes (not in database)
    private String userFullName;
    private String userEmail;
    private String flightCode;
    private String flightName;
    private String companyName;
    private String departureAirportCode;
    private String departureCity;
    private String destinationAirportCode;
    private String destinationCity;

    // Enums for status fields
    public enum PaymentStatus {
        PENDING("Pending"),
        COMPLETED("Completed"),
        FAILED("Failed");
        
        private final String displayName;
        
        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static PaymentStatus fromString(String status) {
            for (PaymentStatus ps : PaymentStatus.values()) {
                if (ps.displayName.equalsIgnoreCase(status)) {
                    return ps;
                }
            }
            return PENDING;
        }
    }

    public enum BookingStatus {
        PENDING("Pending"),
        CONFIRMED("Confirmed"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        BookingStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public static BookingStatus fromString(String status) {
            for (BookingStatus bs : BookingStatus.values()) {
                if (bs.displayName.equalsIgnoreCase(status)) {
                    return bs;
                }
            }
            return PENDING;
        }
    }

    // Default constructor
    public Booking() {}

    // Constructor without ID (for new bookings)
    public Booking(int userId, int flightId, int departureAirportId, int destinationAirportId,
                   LocalDateTime departureTime, LocalDateTime destinationTime, String pnr,
                   LocalDate dateOfDeparture, LocalDate dateOfDestination, BigDecimal amount,
                   PaymentStatus paymentStatus, BookingStatus bookingStatus) {
        this.userId = userId;
        this.flightId = flightId;
        this.departureAirportId = departureAirportId;
        this.destinationAirportId = destinationAirportId;
        this.departureTime = departureTime;
        this.destinationTime = destinationTime;
        this.pnr = pnr;
        this.dateOfDeparture = dateOfDeparture;
        this.dateOfDestination = dateOfDestination;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.bookingStatus = bookingStatus;
    }

    // Constructor with all fields
    public Booking(int bookingId, int userId, int flightId, int departureAirportId, int destinationAirportId,
                   LocalDateTime departureTime, LocalDateTime destinationTime, String pnr,
                   LocalDate dateOfDeparture, LocalDate dateOfDestination, Timestamp dateOfBooking,
                   BigDecimal amount, PaymentStatus paymentStatus, BookingStatus bookingStatus) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.flightId = flightId;
        this.departureAirportId = departureAirportId;
        this.destinationAirportId = destinationAirportId;
        this.departureTime = departureTime;
        this.destinationTime = destinationTime;
        this.pnr = pnr;
        this.dateOfDeparture = dateOfDeparture;
        this.dateOfDestination = dateOfDestination;
        this.dateOfBooking = dateOfBooking;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.bookingStatus = bookingStatus;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
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

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

    public LocalDate getDateOfDeparture() {
        return dateOfDeparture;
    }

    public void setDateOfDeparture(LocalDate dateOfDeparture) {
        this.dateOfDeparture = dateOfDeparture;
    }

    public LocalDate getDateOfDestination() {
        return dateOfDestination;
    }

    public void setDateOfDestination(LocalDate dateOfDestination) {
        this.dateOfDestination = dateOfDestination;
    }

    public Timestamp getDateOfBooking() {
        return dateOfBooking;
    }

    public void setDateOfBooking(Timestamp dateOfBooking) {
        this.dateOfBooking = dateOfBooking;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    // Additional display fields getters and setters
    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartureAirportCode() {
        return departureAirportCode;
    }

    public void setDepartureAirportCode(String departureAirportCode) {
        this.departureAirportCode = departureAirportCode;
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

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
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

    public boolean isCancellable() {
        return bookingStatus == BookingStatus.PENDING || bookingStatus == BookingStatus.CONFIRMED;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", pnr='" + pnr + '\'' +
                ", flightCode='" + flightCode + '\'' +
                ", amount=" + amount +
                ", paymentStatus=" + paymentStatus +
                ", bookingStatus=" + bookingStatus +
                ", dateOfBooking=" + dateOfBooking +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Booking booking = (Booking) obj;
        return bookingId == booking.bookingId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(bookingId);
    }
}