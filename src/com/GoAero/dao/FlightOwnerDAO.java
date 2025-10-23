package com.GoAero.dao;

import com.GoAero.db.DBConnection;
import com.GoAero.model.FlightOwner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for FlightOwner entity
 */
public class FlightOwnerDAO implements BaseDAO<FlightOwner, Integer> {

    private static final String INSERT_FLIGHT_OWNER = 
        "INSERT INTO flight_owners (company_name, company_code, contact_info, flight_count, password) VALUES (?, ?, ?, ?, ?)";
    
    private static final String SELECT_FLIGHT_OWNER_BY_ID = 
        "SELECT owner_id, company_name, company_code, contact_info, flight_count, password, created_at, updated_at FROM flight_owners WHERE owner_id = ?";
    
    private static final String SELECT_ALL_FLIGHT_OWNERS = 
        "SELECT owner_id, company_name, company_code, contact_info, flight_count, password, created_at, updated_at FROM flight_owners ORDER BY company_name";
    
    private static final String UPDATE_FLIGHT_OWNER = 
        "UPDATE flight_owners SET company_name = ?, company_code = ?, contact_info = ?, flight_count = ?, password = ? WHERE owner_id = ?";
    
    private static final String DELETE_FLIGHT_OWNER = 
        "DELETE FROM flight_owners WHERE owner_id = ?";
    
    private static final String SELECT_FLIGHT_OWNER_BY_CODE = 
        "SELECT owner_id, company_name, company_code, contact_info, flight_count, password, created_at, updated_at FROM flight_owners WHERE company_code = ?";
    
    private static final String COUNT_FLIGHT_OWNERS = 
        "SELECT COUNT(*) FROM flight_owners";
    
    private static final String CHECK_CODE_EXISTS = 
        "SELECT COUNT(*) FROM flight_owners WHERE company_code = ? AND owner_id != ?";
    
    private static final String UPDATE_FLIGHT_COUNT = 
        "UPDATE flight_owners SET flight_count = (SELECT COUNT(*) FROM flight_data WHERE company_id = ?) WHERE owner_id = ?";

    @Override
    public FlightOwner create(FlightOwner flightOwner) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_FLIGHT_OWNER, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, flightOwner.getCompanyName());
            stmt.setString(2, flightOwner.getCompanyCode());
            stmt.setString(3, flightOwner.getContactInfo());
            stmt.setInt(4, flightOwner.getFlightCount());
            stmt.setString(5, flightOwner.getPasswordHash());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        flightOwner.setOwnerId(generatedKeys.getInt(1));
                        return flightOwner;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FlightOwner findById(Integer id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_FLIGHT_OWNER_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFlightOwner(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<FlightOwner> findAll() {
        List<FlightOwner> flightOwners = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_FLIGHT_OWNERS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                flightOwners.add(mapResultSetToFlightOwner(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return flightOwners;
    }

    @Override
    public boolean update(FlightOwner flightOwner) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_FLIGHT_OWNER)) {
            
            stmt.setString(1, flightOwner.getCompanyName());
            stmt.setString(2, flightOwner.getCompanyCode());
            stmt.setString(3, flightOwner.getContactInfo());
            stmt.setInt(4, flightOwner.getFlightCount());
            stmt.setString(5, flightOwner.getPasswordHash());
            stmt.setInt(6, flightOwner.getOwnerId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_FLIGHT_OWNER)) {
            
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
             PreparedStatement stmt = conn.prepareStatement(COUNT_FLIGHT_OWNERS);
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
     * Finds a flight owner by company code
     * @param code The company code to search for
     * @return The flight owner if found, null otherwise
     */
    public FlightOwner findByCode(String code) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_FLIGHT_OWNER_BY_CODE)) {
            
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFlightOwner(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if a company code already exists for a different flight owner
     * @param code The company code to check
     * @param excludeOwnerId The owner ID to exclude from the check
     * @return true if code exists for another flight owner
     */
    public boolean codeExists(String code, int excludeOwnerId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_CODE_EXISTS)) {
            
            stmt.setString(1, code);
            stmt.setInt(2, excludeOwnerId);
            
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
     * Checks if a company code already exists
     * @param code The company code to check
     * @return true if code exists
     */
    public boolean codeExists(String code) {
        return findByCode(code) != null;
    }

    /**
     * Updates the flight count for a flight owner
     * @param ownerId The owner ID
     * @return true if update was successful
     */
    public boolean updateFlightCount(int ownerId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_FLIGHT_COUNT)) {
            
            stmt.setInt(1, ownerId);
            stmt.setInt(2, ownerId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets flight owners with their current flight counts
     * @return List of flight owners with updated flight counts
     */
    public List<FlightOwner> findAllWithFlightCounts() {
        List<FlightOwner> flightOwners = new ArrayList<>();
        
        String query = "SELECT fo.owner_id, fo.company_name, fo.company_code, fo.contact_info, " +
                      "fo.password, fo.created_at, fo.updated_at, " +
                      "COALESCE(COUNT(fd.flight_id), 0) as flight_count " +
                      "FROM flight_owners fo " +
                      "LEFT JOIN flight_data fd ON fo.owner_id = fd.company_id " +
                      "GROUP BY fo.owner_id, fo.company_name, fo.company_code, fo.contact_info, " +
                      "fo.password, fo.created_at, fo.updated_at " +
                      "ORDER BY fo.company_name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                FlightOwner flightOwner = new FlightOwner();
                flightOwner.setOwnerId(rs.getInt("owner_id"));
                flightOwner.setCompanyName(rs.getString("company_name"));
                flightOwner.setCompanyCode(rs.getString("company_code"));
                flightOwner.setContactInfo(rs.getString("contact_info"));
                flightOwner.setPasswordHash(rs.getString("password"));
                flightOwner.setFlightCount(rs.getInt("flight_count"));
                flightOwner.setCreatedAt(rs.getTimestamp("created_at"));
                flightOwner.setUpdatedAt(rs.getTimestamp("updated_at"));
                
                flightOwners.add(flightOwner);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return flightOwners;
    }

    /**
     * Maps a ResultSet row to a FlightOwner object
     * @param rs The ResultSet
     * @return A FlightOwner object
     * @throws SQLException if database access error occurs
     */
    private FlightOwner mapResultSetToFlightOwner(ResultSet rs) throws SQLException {
        FlightOwner flightOwner = new FlightOwner();
        flightOwner.setOwnerId(rs.getInt("owner_id"));
        flightOwner.setCompanyName(rs.getString("company_name"));
        flightOwner.setCompanyCode(rs.getString("company_code"));
        flightOwner.setContactInfo(rs.getString("contact_info"));
        flightOwner.setFlightCount(rs.getInt("flight_count"));
        flightOwner.setPasswordHash(rs.getString("password"));
        flightOwner.setCreatedAt(rs.getTimestamp("created_at"));
        flightOwner.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return flightOwner;
    }
}