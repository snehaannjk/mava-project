package com.GoAero.dao;

import com.GoAero.db.DBConnection;
import com.GoAero.model.Booking;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Booking entity
 */
public class BookingDAO implements BaseDAO<Booking, Integer> {

    private static final String INSERT_BOOKING = 
        "INSERT INTO bookings (user_id, flight_id, departure_airport_id, destination_airport_id, " +
        "departure_time, destination_time, PNR, date_of_departure, date_of_destination, " +
        "amount, payment_status, booking_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_BOOKING_BY_ID = 
        "SELECT b.booking_id, b.user_id, b.flight_id, b.departure_airport_id, b.destination_airport_id, " +
        "b.departure_time, b.destination_time, b.PNR, b.date_of_departure, b.date_of_destination, " +
        "b.date_of_booking, b.amount, b.payment_status, b.booking_status, " +
        "u.first_name, u.last_name, u.email, " +
        "fd.flight_code, fd.flight_name, fo.company_name, " +
        "da.airport_code as dep_code, da.city as dep_city, " +
        "dest.airport_code as dest_code, dest.city as dest_city " +
        "FROM bookings b " +
        "JOIN users u ON b.user_id = u.user_id " +
        "JOIN flight_data fd ON b.flight_id = fd.flight_id " +
        "JOIN flight_owners fo ON fd.company_id = fo.owner_id " +
        "JOIN airports da ON b.departure_airport_id = da.airport_id " +
        "JOIN airports dest ON b.destination_airport_id = dest.airport_id " +
        "WHERE b.booking_id = ?";
    
    private static final String SELECT_ALL_BOOKINGS = 
        "SELECT b.booking_id, b.user_id, b.flight_id, b.departure_airport_id, b.destination_airport_id, " +
        "b.departure_time, b.destination_time, b.PNR, b.date_of_departure, b.date_of_destination, " +
        "b.date_of_booking, b.amount, b.payment_status, b.booking_status, " +
        "u.first_name, u.last_name, u.email, " +
        "fd.flight_code, fd.flight_name, fo.company_name, " +
        "da.airport_code as dep_code, da.city as dep_city, " +
        "dest.airport_code as dest_code, dest.city as dest_city " +
        "FROM bookings b " +
        "JOIN users u ON b.user_id = u.user_id " +
        "JOIN flight_data fd ON b.flight_id = fd.flight_id " +
        "JOIN flight_owners fo ON fd.company_id = fo.owner_id " +
        "JOIN airports da ON b.departure_airport_id = da.airport_id " +
        "JOIN airports dest ON b.destination_airport_id = dest.airport_id " +
        "ORDER BY b.date_of_booking DESC";
    
    private static final String UPDATE_BOOKING = 
        "UPDATE bookings SET user_id = ?, flight_id = ?, departure_airport_id = ?, destination_airport_id = ?, " +
        "departure_time = ?, destination_time = ?, PNR = ?, date_of_departure = ?, date_of_destination = ?, " +
        "amount = ?, payment_status = ?, booking_status = ? WHERE booking_id = ?";
    
    private static final String DELETE_BOOKING = 
        "DELETE FROM bookings WHERE booking_id = ?";
    
    private static final String COUNT_BOOKINGS = 
        "SELECT COUNT(*) FROM bookings";
    
    private static final String SELECT_BOOKING_BY_PNR = 
        "SELECT b.booking_id, b.user_id, b.flight_id, b.departure_airport_id, b.destination_airport_id, " +
        "b.departure_time, b.destination_time, b.PNR, b.date_of_departure, b.date_of_destination, " +
        "b.date_of_booking, b.amount, b.payment_status, b.booking_status, " +
        "u.first_name, u.last_name, u.email, " +
        "fd.flight_code, fd.flight_name, fo.company_name, " +
        "da.airport_code as dep_code, da.city as dep_city, " +
        "dest.airport_code as dest_code, dest.city as dest_city " +
        "FROM bookings b " +
        "JOIN users u ON b.user_id = u.user_id " +
        "JOIN flight_data fd ON b.flight_id = fd.flight_id " +
        "JOIN flight_owners fo ON fd.company_id = fo.owner_id " +
        "JOIN airports da ON b.departure_airport_id = da.airport_id " +
        "JOIN airports dest ON b.destination_airport_id = dest.airport_id " +
        "WHERE b.PNR = ?";

    @Override
    public Booking create(Booking booking) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_BOOKING, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getFlightId());
            stmt.setInt(3, booking.getDepartureAirportId());
            stmt.setInt(4, booking.getDestinationAirportId());
            stmt.setTimestamp(5, Timestamp.valueOf(booking.getDepartureTime()));
            stmt.setTimestamp(6, Timestamp.valueOf(booking.getDestinationTime()));
            stmt.setString(7, booking.getPnr());
            stmt.setDate(8, Date.valueOf(booking.getDateOfDeparture()));
            stmt.setDate(9, Date.valueOf(booking.getDateOfDestination()));
            stmt.setBigDecimal(10, booking.getAmount());
            stmt.setString(11, booking.getPaymentStatus().getDisplayName());
            stmt.setString(12, booking.getBookingStatus().getDisplayName());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setBookingId(generatedKeys.getInt(1));
                        return booking;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Booking findById(Integer id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BOOKING_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_BOOKINGS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bookings;
    }

    @Override
    public boolean update(Booking booking) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_BOOKING)) {
            
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getFlightId());
            stmt.setInt(3, booking.getDepartureAirportId());
            stmt.setInt(4, booking.getDestinationAirportId());
            stmt.setTimestamp(5, Timestamp.valueOf(booking.getDepartureTime()));
            stmt.setTimestamp(6, Timestamp.valueOf(booking.getDestinationTime()));
            stmt.setString(7, booking.getPnr());
            stmt.setDate(8, Date.valueOf(booking.getDateOfDeparture()));
            stmt.setDate(9, Date.valueOf(booking.getDateOfDestination()));
            stmt.setBigDecimal(10, booking.getAmount());
            stmt.setString(11, booking.getPaymentStatus().getDisplayName());
            stmt.setString(12, booking.getBookingStatus().getDisplayName());
            stmt.setInt(13, booking.getBookingId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_BOOKING)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean exists(Integer id) {
        return findById(id) != null;
    }

    @Override
    public long count() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_BOOKINGS);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Finds a booking by PNR
     * @param pnr The PNR to search for
     * @return The booking if found, null otherwise
     */
    public Booking findByPNR(String pnr) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BOOKING_BY_PNR)) {
            
            stmt.setString(1, pnr);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds bookings by user ID
     * @param userId The user ID
     * @return List of bookings for the user
     */
    public List<Booking> findByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        
        String query = SELECT_ALL_BOOKINGS.replace("ORDER BY b.date_of_booking DESC", 
            "WHERE b.user_id = ? ORDER BY b.date_of_booking DESC");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return bookings;
    }

    /**
     * Updates booking status
     * @param bookingId The booking ID
     * @param status The new booking status
     * @return true if update was successful
     */
    public boolean updateBookingStatus(int bookingId, Booking.BookingStatus status) {
        String query = "UPDATE bookings SET booking_status = ? WHERE booking_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status.getDisplayName());
            stmt.setInt(2, bookingId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates payment status
     * @param bookingId The booking ID
     * @param status The new payment status
     * @return true if update was successful
     */
    public boolean updatePaymentStatus(int bookingId, Booking.PaymentStatus status) {
        String query = "UPDATE bookings SET payment_status = ? WHERE booking_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status.getDisplayName());
            stmt.setInt(2, bookingId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if PNR already exists
     * @param pnr The PNR to check
     * @return true if PNR exists
     */
    public boolean pnrExists(String pnr) {
        return findByPNR(pnr) != null;
    }

    /**
     * Maps a ResultSet row to a Booking object
     * @param rs The ResultSet
     * @return A Booking object
     * @throws SQLException if database access error occurs
     */
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setFlightId(rs.getInt("flight_id"));
        booking.setDepartureAirportId(rs.getInt("departure_airport_id"));
        booking.setDestinationAirportId(rs.getInt("destination_airport_id"));
        booking.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
        booking.setDestinationTime(rs.getTimestamp("destination_time").toLocalDateTime());
        booking.setPnr(rs.getString("PNR"));
        booking.setDateOfDeparture(rs.getDate("date_of_departure").toLocalDate());
        booking.setDateOfDestination(rs.getDate("date_of_destination").toLocalDate());
        booking.setDateOfBooking(rs.getTimestamp("date_of_booking"));
        booking.setAmount(rs.getBigDecimal("amount"));
        booking.setPaymentStatus(Booking.PaymentStatus.fromString(rs.getString("payment_status")));
        booking.setBookingStatus(Booking.BookingStatus.fromString(rs.getString("booking_status")));
        
        // Set additional display fields
        booking.setUserFullName(rs.getString("first_name") + " " + rs.getString("last_name"));
        booking.setUserEmail(rs.getString("email"));
        booking.setFlightCode(rs.getString("flight_code"));
        booking.setFlightName(rs.getString("flight_name"));
        booking.setCompanyName(rs.getString("company_name"));
        booking.setDepartureAirportCode(rs.getString("dep_code"));
        booking.setDepartureCity(rs.getString("dep_city"));
        booking.setDestinationAirportCode(rs.getString("dest_code"));
        booking.setDestinationCity(rs.getString("dest_city"));
        
        return booking;
    }
}