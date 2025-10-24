package com.GoAero.dao;

import com.GoAero.db.DBConnection;
import com.GoAero.model.Flight;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Flight entity
 */
public class FlightDAO implements BaseDAO<Flight, Integer> {

    private static final String INSERT_FLIGHT = 
        "INSERT INTO flight_data (company_id, flight_code, flight_name, capacity, departure_airport_id, " +
        "destination_airport_id, departure_time, destination_time, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_FLIGHT_BY_ID = 
        "SELECT fd.flight_id, fd.company_id, fd.flight_code, fd.flight_name, fd.capacity, " +
        "fd.departure_airport_id, fd.destination_airport_id, fd.departure_time, fd.destination_time, fd.price, " +
        "fo.company_name, fo.company_code, " +
        "da.airport_code as dep_code, da.airport_name as dep_name, da.city as dep_city, " +
        "dest.airport_code as dest_code, dest.airport_name as dest_name, dest.city as dest_city " +
        "FROM flight_data fd " +
        "JOIN flight_owners fo ON fd.company_id = fo.owner_id " +
        "JOIN airports da ON fd.departure_airport_id = da.airport_id " +
        "JOIN airports dest ON fd.destination_airport_id = dest.airport_id " +
        "WHERE fd.flight_id = ?";
    
    private static final String SELECT_ALL_FLIGHTS = 
        "SELECT fd.flight_id, fd.company_id, fd.flight_code, fd.flight_name, fd.capacity, " +
        "fd.departure_airport_id, fd.destination_airport_id, fd.departure_time, fd.destination_time, fd.price, " +
        "fo.company_name, fo.company_code, " +
        "da.airport_code as dep_code, da.airport_name as dep_name, da.city as dep_city, " +
        "dest.airport_code as dest_code, dest.airport_name as dest_name, dest.city as dest_city " +
        "FROM flight_data fd " +
        "JOIN flight_owners fo ON fd.company_id = fo.owner_id " +
        "JOIN airports da ON fd.departure_airport_id = da.airport_id " +
        "JOIN airports dest ON fd.destination_airport_id = dest.airport_id " +
        "ORDER BY fd.departure_time";
    
    private static final String UPDATE_FLIGHT = 
        "UPDATE flight_data SET company_id = ?, flight_code = ?, flight_name = ?, capacity = ?, " +
        "departure_airport_id = ?, destination_airport_id = ?, departure_time = ?, destination_time = ?, price = ? " +
        "WHERE flight_id = ?";
    
    private static final String DELETE_FLIGHT = 
        "DELETE FROM flight_data WHERE flight_id = ?";
    
    private static final String COUNT_FLIGHTS = 
        "SELECT COUNT(*) FROM flight_data";
    
    private static final String CHECK_FLIGHT_CODE_EXISTS = 
        "SELECT COUNT(*) FROM flight_data WHERE flight_code = ? AND flight_id != ?";

    @Override
    public Flight create(Flight flight) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_FLIGHT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, flight.getCompanyId());
            stmt.setString(2, flight.getFlightCode());
            stmt.setString(3, flight.getFlightName());
            stmt.setInt(4, flight.getCapacity());
            stmt.setInt(5, flight.getDepartureAirportId());
            stmt.setInt(6, flight.getDestinationAirportId());
            stmt.setTimestamp(7, Timestamp.valueOf(flight.getDepartureTime()));
            stmt.setTimestamp(8, Timestamp.valueOf(flight.getDestinationTime()));
            stmt.setBigDecimal(9, flight.getPrice());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        flight.setFlightId(generatedKeys.getInt(1));
                        return flight;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Flight findById(Integer id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_FLIGHT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFlight(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Flight> findAll() {
        List<Flight> flights = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_FLIGHTS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                flights.add(mapResultSetToFlight(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return flights;
    }

    @Override
    public boolean update(Flight flight) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_FLIGHT)) {
            
            stmt.setInt(1, flight.getCompanyId());
            stmt.setString(2, flight.getFlightCode());
            stmt.setString(3, flight.getFlightName());
            stmt.setInt(4, flight.getCapacity());
            stmt.setInt(5, flight.getDepartureAirportId());
            stmt.setInt(6, flight.getDestinationAirportId());
            stmt.setTimestamp(7, Timestamp.valueOf(flight.getDepartureTime()));
            stmt.setTimestamp(8, Timestamp.valueOf(flight.getDestinationTime()));
            stmt.setBigDecimal(9, flight.getPrice());
            stmt.setInt(10, flight.getFlightId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_FLIGHT)) {
            
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
             PreparedStatement stmt = conn.prepareStatement(COUNT_FLIGHTS);
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
     * Searches flights based on departure/destination airports and date
     * @param departureAirportId Departure airport ID
     * @param destinationAirportId Destination airport ID
     * @param departureDate Departure date
     * @return List of matching flights
     */
    public List<Flight> searchFlights(int departureAirportId, int destinationAirportId, LocalDate departureDate) {
        List<Flight> flights = new ArrayList<>();
        
        String query = SELECT_ALL_FLIGHTS.replace("ORDER BY fd.departure_time", 
            "WHERE fd.departure_airport_id = ? AND fd.destination_airport_id = ? " +
            "AND DATE(fd.departure_time) = ? ORDER BY fd.departure_time");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, departureAirportId);
            stmt.setInt(2, destinationAirportId);
            stmt.setDate(3, Date.valueOf(departureDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    flights.add(mapResultSetToFlight(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return flights;
    }

    /**
     * Finds flights by company/owner ID
     * @param companyId The company ID
     * @return List of flights owned by the company
     */
    public List<Flight> findByCompanyId(int companyId) {
        List<Flight> flights = new ArrayList<>();
        
        String query = SELECT_ALL_FLIGHTS.replace("ORDER BY fd.departure_time", 
            "WHERE fd.company_id = ? ORDER BY fd.departure_time");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, companyId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    flights.add(mapResultSetToFlight(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return flights;
    }

    /**
     * Checks if a flight code already exists
     * @param flightCode The flight code to check
     * @param excludeFlightId The flight ID to exclude from check
     * @return true if code exists for another flight
     */
    public boolean flightCodeExists(String flightCode, int excludeFlightId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_FLIGHT_CODE_EXISTS)) {
            
            stmt.setString(1, flightCode);
            stmt.setInt(2, excludeFlightId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if a flight code already exists
     * @param flightCode The flight code to check
     * @return true if code exists
     */
    public boolean flightCodeExists(String flightCode) {
        return flightCodeExists(flightCode, -1);
    }

    /**
     * Gets available seats for a flight
     * @param flightId The flight ID
     * @return Number of available seats
     */
    public int getAvailableSeats(int flightId) {
        String query = "SELECT fd.capacity - COALESCE(COUNT(b.booking_id), 0) as available_seats " +
                      "FROM flight_data fd " +
                      "LEFT JOIN bookings b ON fd.flight_id = b.flight_id " +
                      "AND b.booking_status IN ('Pending', 'Confirmed') " +
                      "WHERE fd.flight_id = ? " +
                      "GROUP BY fd.flight_id, fd.capacity";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, flightId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("available_seats");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // If no bookings found, return full capacity
        Flight flight = findById(flightId);
        return flight != null ? flight.getCapacity() : 0;
    }

    /**
     * Maps a ResultSet row to a Flight object
     * @param rs The ResultSet
     * @return A Flight object
     * @throws SQLException if database access error occurs
     */
    private Flight mapResultSetToFlight(ResultSet rs) throws SQLException {
        Flight flight = new Flight();
        flight.setFlightId(rs.getInt("flight_id"));
        flight.setCompanyId(rs.getInt("company_id"));
        flight.setFlightCode(rs.getString("flight_code"));
        flight.setFlightName(rs.getString("flight_name"));
        flight.setCapacity(rs.getInt("capacity"));
        flight.setDepartureAirportId(rs.getInt("departure_airport_id"));
        flight.setDestinationAirportId(rs.getInt("destination_airport_id"));
        flight.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
        flight.setDestinationTime(rs.getTimestamp("destination_time").toLocalDateTime());
        flight.setPrice(rs.getBigDecimal("price"));
        
        // Set additional display fields
        flight.setCompanyName(rs.getString("company_name"));
        flight.setCompanyCode(rs.getString("company_code"));
        flight.setDepartureAirportCode(rs.getString("dep_code"));
        flight.setDepartureAirportName(rs.getString("dep_name"));
        flight.setDepartureCity(rs.getString("dep_city"));
        flight.setDestinationAirportCode(rs.getString("dest_code"));
        flight.setDestinationAirportName(rs.getString("dest_name"));
        flight.setDestinationCity(rs.getString("dest_city"));
        
        return flight;
    }
}
