package com.GoAero.model;

/**
 * SessionManager class to track the currently logged-in user across the application
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private Admin currentAdmin;
    private FlightOwner currentFlightOwner;
    private UserType currentUserType;

    public enum UserType {
        USER,
        ADMIN,
        FLIGHT_OWNER,
        NONE
    }

    // Private constructor for singleton pattern
    private SessionManager() {
        this.currentUserType = UserType.NONE;
    }

    // Get singleton instance
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // User session methods
    public void loginUser(User user) {
        logout(); // Clear any existing session
        this.currentUser = user;
        this.currentUserType = UserType.USER;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isUserLoggedIn() {
        return currentUser != null && currentUserType == UserType.USER;
    }

    // Admin session methods
    public void loginAdmin(Admin admin) {
        logout(); // Clear any existing session
        this.currentAdmin = admin;
        this.currentUserType = UserType.ADMIN;
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public boolean isAdminLoggedIn() {
        return currentAdmin != null && currentUserType == UserType.ADMIN;
    }

    // Flight Owner session methods
    public void loginFlightOwner(FlightOwner flightOwner) {
        logout(); // Clear any existing session
        this.currentFlightOwner = flightOwner;
        this.currentUserType = UserType.FLIGHT_OWNER;
    }

    public FlightOwner getCurrentFlightOwner() {
        return currentFlightOwner;
    }

    public boolean isFlightOwnerLoggedIn() {
        return currentFlightOwner != null && currentUserType == UserType.FLIGHT_OWNER;
    }

    // General session methods
    public UserType getCurrentUserType() {
        return currentUserType;
    }

    public boolean isLoggedIn() {
        return currentUserType != UserType.NONE;
    }

    public String getCurrentUserDisplayName() {
        switch (currentUserType) {
            case USER:
                return currentUser != null ? currentUser.getFullName() : "Unknown User";
            case ADMIN:
                return currentAdmin != null ? currentAdmin.getUsername() : "Unknown Admin";
            case FLIGHT_OWNER:
                return currentFlightOwner != null ? currentFlightOwner.getCompanyName() : "Unknown Company";
            default:
                return "Not Logged In";
        }
    }

    public int getCurrentUserId() {
        switch (currentUserType) {
            case USER:
                return currentUser != null ? currentUser.getUserId() : -1;
            case ADMIN:
                return currentAdmin != null ? currentAdmin.getAdminId() : -1;
            case FLIGHT_OWNER:
                return currentFlightOwner != null ? currentFlightOwner.getOwnerId() : -1;
            default:
                return -1;
        }
    }

    public void logout() {
        this.currentUser = null;
        this.currentAdmin = null;
        this.currentFlightOwner = null;
        this.currentUserType = UserType.NONE;
    }

    // Utility method to check permissions
    public boolean hasPermission(String permission) {
        switch (permission.toLowerCase()) {
            case "user_booking":
                return isUserLoggedIn();
            case "admin_management":
                return isAdminLoggedIn();
            case "flight_management":
                return isAdminLoggedIn() || isFlightOwnerLoggedIn();
            case "view_all_bookings":
                return isAdminLoggedIn();
            case "view_own_flights":
                return isFlightOwnerLoggedIn();
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return "SessionManager{" +
                "currentUserType=" + currentUserType +
                ", currentUserDisplayName='" + getCurrentUserDisplayName() + '\'' +
                '}';
    }
}
