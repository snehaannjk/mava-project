package com.GoAero.dao;

import com.GoAero.db.DBConnection;
import com.GoAero.model.Airport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Airport entity
 */
public class AirportDAO implements BaseDAO<Airport, Integer> {

    private static final String INSERT_AIRPORT = 
        "INSERT INTO airports (airport_code, airport_name, city, country) VALUES (?, ?, ?, ?)";
    
    private static final String SELECT_AIRPORT_BY_ID = 
        "SELECT airport_id, airport_code, airport_name, city, country FROM airports WHERE airport_id = ?";
    
    private static final String SELECT_ALL_AIRPORTS = 
        "SELECT airport_id, airport_code, airport_name, city, country FROM airports ORDER BY airport_code";
    
    private static final String UPDATE_AIRPORT = 
        "UPDATE airports SET airport_code = ?, airport_name = ?, city = ?, country = ? WHERE airport_id = ?";
    
    private static final String DELETE_AIRPORT = 
        "DELETE FROM airports WHERE airport_id = ?";
    
    private static final String SELECT_AIRPORT_BY_CODE = 
        "SELECT airport_id, airport_code, airport_name, city, country FROM airports WHERE airport_code = ?";
    
    private static final String COUNT_AIRPORTS = 
        "SELECT COUNT(*) FROM airports";
    
    private static final String CHECK_CODE_EXISTS = 
        "SELECT COUNT(*) FROM airports WHERE airport_code = ? AND airport_id != ?";
    
    private static final String SEARCH_AIRPORTS = 
        "SELECT airport_id, airport_code, airport_name, city, country FROM airports " +
        "WHERE airport_code LIKE ? OR airport_name LIKE ? OR city LIKE ? OR country LIKE ? " +
        "ORDER BY airport_code";

    @Override
    public Airport create(Airport airport) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_AIRPORT, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, airport.getAirportCode());
            stmt.setString(2, airport.getAirportName());
            stmt.setString(3, airport.getCity());
            stmt.setString(4, airport.getCountry());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        airport.setAirportId(generatedKeys.getInt(1));
                        return airport;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Airport findById(Integer id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_AIRPORT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAirport(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Airport> findAll() {
        List<Airport> airports = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_AIRPORTS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                airports.add(mapResultSetToAirport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return airports;
    }

    @Override
    public boolean update(Airport airport) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_AIRPORT)) {
            
            stmt.setString(1, airport.getAirportCode());
            stmt.setString(2, airport.getAirportName());
            stmt.setString(3, airport.getCity());
            stmt.setString(4, airport.getCountry());
            stmt.setInt(5, airport.getAirportId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_AIRPORT)) {
            
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
             PreparedStatement stmt = conn.prepareStatement(COUNT_AIRPORTS);
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
     * Finds an airport by its code
     * @param code The airport code to search for
     * @return The airport if found, null otherwise
     */
    public Airport findByCode(String code) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_AIRPORT_BY_CODE)) {
            
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAirport(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if an airport code already exists for a different airport
     * @param code The airport code to check
     * @param excludeAirportId The airport ID to exclude from the check
     * @return true if code exists for another airport
     */
    public boolean codeExists(String code, int excludeAirportId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_CODE_EXISTS)) {
            
            stmt.setString(1, code);
            stmt.setInt(2, excludeAirportId);
            
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
     * Checks if an airport code already exists
     * @param code The airport code to check
     * @return true if code exists
     */
    public boolean codeExists(String code) {
        return findByCode(code) != null;
    }

    /**
     * Searches airports by code, name, city, or country
     * @param searchTerm The term to search for
     * @return List of matching airports
     */
    public List<Airport> searchAirports(String searchTerm) {
        List<Airport> airports = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_AIRPORTS)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    airports.add(mapResultSetToAirport(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return airports;
    }

    /**
     * Gets airports by city
     * @param city The city to search for
     * @return List of airports in the city
     */
    public List<Airport> findByCity(String city) {
        List<Airport> airports = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT airport_id, airport_code, airport_name, city, country FROM airports WHERE city = ? ORDER BY airport_code")) {
            
            stmt.setString(1, city);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    airports.add(mapResultSetToAirport(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return airports;
    }

    /**
     * Gets airports by country
     * @param country The country to search for
     * @return List of airports in the country
     */
    public List<Airport> findByCountry(String country) {
        List<Airport> airports = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT airport_id, airport_code, airport_name, city, country FROM airports WHERE country = ? ORDER BY city, airport_code")) {
            
            stmt.setString(1, country);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    airports.add(mapResultSetToAirport(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return airports;
    }

    /**
     * Maps a ResultSet row to an Airport object
     * @param rs The ResultSet
     * @return An Airport object
     * @throws SQLException if database access error occurs
     */
    private Airport mapResultSetToAirport(ResultSet rs) throws SQLException {
        Airport airport = new Airport();
        airport.setAirportId(rs.getInt("airport_id"));
        airport.setAirportCode(rs.getString("airport_code"));
        airport.setAirportName(rs.getString("airport_name"));
        airport.setCity(rs.getString("city"));
        airport.setCountry(rs.getString("country"));
        
        return airport;
    }
}
