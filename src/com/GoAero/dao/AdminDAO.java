package com.GoAero.dao;

import com.GoAero.db.DBConnection;
import com.GoAero.model.Admin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Admin entity
 */
public class AdminDAO implements BaseDAO<Admin, Integer> {

    private static final String INSERT_ADMIN = 
        "INSERT INTO admin_users (username, password_hash) VALUES (?, ?)";
    
    private static final String SELECT_ADMIN_BY_ID = 
        "SELECT admin_id, username, password_hash, created_at, updated_at FROM admin_users WHERE admin_id = ?";
    
    private static final String SELECT_ALL_ADMINS = 
        "SELECT admin_id, username, password_hash, created_at, updated_at FROM admin_users ORDER BY created_at DESC";
    
    private static final String UPDATE_ADMIN = 
        "UPDATE admin_users SET username = ?, password_hash = ? WHERE admin_id = ?";
    
    private static final String DELETE_ADMIN = 
        "DELETE FROM admin_users WHERE admin_id = ?";
    
    private static final String SELECT_ADMIN_BY_USERNAME = 
        "SELECT admin_id, username, password_hash, created_at, updated_at FROM admin_users WHERE username = ?";
    
    private static final String COUNT_ADMINS = 
        "SELECT COUNT(*) FROM admin_users";
    
    private static final String CHECK_USERNAME_EXISTS = 
        "SELECT COUNT(*) FROM admin_users WHERE username = ? AND admin_id != ?";

    @Override
    public Admin create(Admin admin) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_ADMIN, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, admin.getUsername());
            stmt.setString(2, admin.getPasswordHash());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        admin.setAdminId(generatedKeys.getInt(1));
                        return admin;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Admin findById(Integer id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ADMIN_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Admin> findAll() {
        List<Admin> admins = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_ADMINS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                admins.add(mapResultSetToAdmin(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return admins;
    }

    @Override
    public boolean update(Admin admin) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ADMIN)) {
            
            stmt.setString(1, admin.getUsername());
            stmt.setString(2, admin.getPasswordHash());
            stmt.setInt(3, admin.getAdminId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_ADMIN)) {
            
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
             PreparedStatement stmt = conn.prepareStatement(COUNT_ADMINS);
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
     * Finds an admin by username
     * @param username The username to search for
     * @return The admin if found, null otherwise
     */
    public Admin findByUsername(String username) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ADMIN_BY_USERNAME)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if a username already exists for a different admin
     * @param username The username to check
     * @param excludeAdminId The admin ID to exclude from the check
     * @return true if username exists for another admin
     */
    public boolean usernameExists(String username, int excludeAdminId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_USERNAME_EXISTS)) {
            
            stmt.setString(1, username);
            stmt.setInt(2, excludeAdminId);
            
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
     * Checks if a username already exists
     * @param username The username to check
     * @return true if username exists
     */
    public boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }

    /**
     * Maps a ResultSet row to an Admin object
     * @param rs The ResultSet
     * @return An Admin object
     * @throws SQLException if database access error occurs
     */
    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getInt("admin_id"));
        admin.setUsername(rs.getString("username"));
        admin.setPasswordHash(rs.getString("password_hash"));
        admin.setCreatedAt(rs.getTimestamp("created_at"));
        admin.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return admin;
    }
}
