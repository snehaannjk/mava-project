package com.GoAero.ui;

import com.GoAero.model.SessionManager;
import com.GoAero.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Main dashboard for logged-in passengers with modern UI design
 */
public class UserDashboard extends JFrame {
    // Professional color scheme (consistent with LandingPage)
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private static final Color ACCENT_ORANGE = new Color(255, 152, 0);
    private static final Color DARK_BLUE = new Color(13, 71, 161);
    private static final Color HOVER_BLUE = new Color(30, 136, 229);
    private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    private static final Color BACKGROUND_GRAY = new Color(250, 250, 250);

    private JLabel welcomeLabel, subtitleLabel;
    private JButton searchFlightsButton, viewBookingsButton, profileButton, logoutButton;
    private User currentUser;

    public UserDashboard() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "Please login first.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        setTitle("GoAero - Passenger Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Welcome label with modern styling
        welcomeLabel = new JLabel("Welcome back, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(DARK_BLUE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Subtitle label
        subtitleLabel = new JLabel("What would you like to do today?");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create styled buttons with icons and modern design
        searchFlightsButton = createStyledButton("✈ Search & Book Flights", PRIMARY_BLUE, Color.WHITE, 18);
        searchFlightsButton.setPreferredSize(new Dimension(320, 70));
        searchFlightsButton.setMaximumSize(new Dimension(320, 70));

        viewBookingsButton = createStyledButton("📋 My Bookings", ACCENT_ORANGE, Color.WHITE, 18);
        viewBookingsButton.setPreferredSize(new Dimension(320, 70));
        viewBookingsButton.setMaximumSize(new Dimension(320, 70));

        profileButton = createStyledButton("👤 My Profile", SUCCESS_GREEN, Color.WHITE, 18);
        profileButton.setPreferredSize(new Dimension(320, 70));
        profileButton.setMaximumSize(new Dimension(320, 70));

        logoutButton = createStyledButton("🚪 Logout", new Color(244, 67, 54), Color.WHITE, 14);
        logoutButton.setPreferredSize(new Dimension(120, 45));
        logoutButton.setMaximumSize(new Dimension(120, 45));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GRAY);

        // Create main content panel with modern styling
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.setBackground(BACKGROUND_GRAY);
        mainContentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header section with welcome message and logout
        JPanel headerSection = createHeaderSection();
        mainContentPanel.add(headerSection, BorderLayout.NORTH);

        // Main dashboard content
        JPanel dashboardContent = createDashboardContent();
        mainContentPanel.add(dashboardContent, BorderLayout.CENTER);

        // Footer section
        JPanel footerSection = createFooterSection();
        mainContentPanel.add(footerSection, BorderLayout.SOUTH);

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderSection() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Welcome section
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(BACKGROUND_GRAY);

        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(8));
        welcomePanel.add(subtitleLabel);

        // Logout button panel
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(BACKGROUND_GRAY);
        logoutPanel.add(logoutButton);

        headerPanel.add(welcomePanel, BorderLayout.CENTER);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createDashboardContent() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        // Search Flights button (primary action)
        gbc.gridx = 0; gbc.gridy = 0;
        searchFlightsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(searchFlightsButton, gbc);

        // View Bookings button
        gbc.gridy = 1;
        viewBookingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(viewBookingsButton, gbc);

        // Profile button
        gbc.gridy = 2;
        profileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(profileButton, gbc);

        return contentPanel;
    }

    private JPanel createFooterSection() {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(BACKGROUND_GRAY);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel footerLabel = new JLabel("GoAero Flight Booking System - Passenger Portal");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(120, 120, 120));

        footerPanel.add(footerLabel);
        return footerPanel;
    }

    private void setupEventListeners() {
        searchFlightsButton.addActionListener(e -> openSearchFlights());
        viewBookingsButton.addActionListener(e -> openBookingHistory());
        profileButton.addActionListener(e -> openProfile());
        logoutButton.addActionListener(e -> handleLogout());
    }

    /**
     * Creates a styled button with hover effects and modern design
     */
    private JButton createStyledButton(String text, Color bgColor, Color textColor, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, fontSize));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        // Add hover effects
        Color originalBg = bgColor;
        Color hoverColor = createHoverColor(bgColor);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBg);
            }
        });

        return button;
    }

    /**
     * Creates a hover color that's slightly lighter than the original
     */
    private Color createHoverColor(Color originalColor) {
        if (originalColor.equals(PRIMARY_BLUE)) {
            return HOVER_BLUE;
        } else if (originalColor.equals(ACCENT_ORANGE)) {
            return new Color(255, 167, 38);
        } else if (originalColor.equals(SUCCESS_GREEN)) {
            return new Color(102, 187, 106);
        } else {
            // For other colors, create a lighter version
            int r = Math.min(255, originalColor.getRed() + 20);
            int g = Math.min(255, originalColor.getGreen() + 20);
            int b = Math.min(255, originalColor.getBlue() + 20);
            return new Color(r, g, b);
        }
    }

    private void openSearchFlights() {
        SwingUtilities.invokeLater(() -> {
            new SearchFlights().setVisible(true);
            this.setVisible(false);
        });
    }

    private void openBookingHistory() {
        SwingUtilities.invokeLater(() -> {
            new BookingHistory().setVisible(true);
        });
    }

    private void openProfile() {
        SwingUtilities.invokeLater(() -> {
            new UserProfileDialog(this).setVisible(true);
        });
    }

    private void handleLogout() {
        // Create a custom styled confirmation dialog for passengers
        JDialog dialog = new JDialog(this, "Confirm Logout", true);
        dialog.setUndecorated(true);

        // Main content with rounded background and shadow
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 16;
                // shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(4, 8, getWidth() - 8, getHeight() - 8, arc, arc);
                // background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 12, arc, arc);
                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(20, 24, 18, 24));

        // Header (icon + title) with passenger-friendly design
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        header.setOpaque(false);
        JLabel passengerIcon = new JLabel("👋");
        passengerIcon.setFont(new Font("Arial", Font.BOLD, 28));
        header.add(passengerIcon);
        JLabel title = new JLabel("Leaving so soon?");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(DARK_BLUE);
        header.add(title);

        content.add(header, BorderLayout.NORTH);

        // Message section with passenger-focused messaging
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        messagePanel.setBorder(new EmptyBorder(10, 8, 20, 8));
        
        JLabel mainMessage = new JLabel("Are you sure you want to logout, " + currentUser.getFirstName() + "?");
        mainMessage.setFont(new Font("Arial", Font.PLAIN, 15));
        mainMessage.setForeground(new Color(60, 60, 60));
        
        JLabel subMessage = new JLabel("You can always come back to search for your next adventure!");
        subMessage.setFont(new Font("Arial", Font.ITALIC, 13));
        subMessage.setForeground(new Color(120, 120, 120));
        subMessage.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        messagePanel.add(mainMessage, BorderLayout.NORTH);
        messagePanel.add(subMessage, BorderLayout.CENTER);
        content.add(messagePanel, BorderLayout.CENTER);

        // Enhanced button panel with passenger-friendly labels
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        JButton stayBtn = createStyledButton("Stay", PRIMARY_BLUE, Color.WHITE, 14);
        stayBtn.setPreferredSize(new Dimension(130, 38));
        stayBtn.addActionListener(e -> dialog.dispose());
        
        JButton logoutBtn = createStyledButton("👋 Logout", new Color(244, 67, 54), Color.WHITE, 14);
        logoutBtn.setPreferredSize(new Dimension(110, 38));
        logoutBtn.addActionListener(e -> {
            dialog.dispose();
            performLogout();
        });
        
        btnPanel.add(stayBtn);
        btnPanel.add(logoutBtn);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(Math.max(450, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(this);

        // Smooth fade-in animation
        try {
            dialog.setOpacity(0f);
            Timer fadeTimer = new Timer(20, null);
            final float[] alpha = {0f};
            fadeTimer.addActionListener(ev -> {
                alpha[0] += 0.08f;
                if (alpha[0] >= 1f) {
                    alpha[0] = 1f;
                    fadeTimer.stop();
                }
                dialog.setOpacity(alpha[0]);
            });
            fadeTimer.start();
        } catch (Throwable ignored) {
            // setOpacity may not be supported on all platforms
        }

        dialog.setVisible(true);
    }

    private void performLogout() {
        SessionManager.getInstance().logout();
        
        // Create a custom styled farewell dialog for passengers
        JDialog farewellDialog = new JDialog(this, "Safe Travels!", true);
        farewellDialog.setUndecorated(true);

        // Main content with rounded background and gradient
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 16;
                // shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(4, 8, getWidth() - 8, getHeight() - 8, arc, arc);
                // background with travel-themed gradient
                GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(240, 248, 255));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 12, arc, arc);
                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(20, 22, 18, 22));

        // Farewell header with travel theme
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        header.setOpaque(false);
        JLabel farewell = new JLabel("✈️");
        farewell.setFont(new Font("Arial", Font.BOLD, 24));
        header.add(farewell);
        JLabel title = new JLabel("Safe Travels, " + currentUser.getFirstName() + "!");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(DARK_BLUE);
        header.add(title);

        content.add(header, BorderLayout.NORTH);

        // Message with passenger-focused content
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        messagePanel.setBorder(new EmptyBorder(10, 6, 15, 6));
        
        JLabel message = new JLabel("You've been successfully logged out. Redirecting....");
        message.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel subMessage = new JLabel("See you soon!");
        subMessage.setFont(new Font("Arial", Font.ITALIC, 12));
        subMessage.setForeground(new Color(100, 100, 100));
        subMessage.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        messagePanel.add(message, BorderLayout.NORTH);
        messagePanel.add(subMessage, BorderLayout.CENTER);
        content.add(messagePanel, BorderLayout.CENTER);

        farewellDialog.setContentPane(content);
        farewellDialog.pack();
        farewellDialog.setSize(Math.max(400, farewellDialog.getWidth()), farewellDialog.getHeight());
        farewellDialog.setLocationRelativeTo(this);

        // Auto-close timer (3.5 seconds for passenger context)
        Timer autoCloseTimer = new Timer(2500, e -> {
            farewellDialog.dispose();
            SwingUtilities.invokeLater(() -> {
                new LandingPage().setVisible(true);
                dispose();
            });
        });
        autoCloseTimer.setRepeats(false);
        autoCloseTimer.start();

        // Fade-in effect
        try {
            farewellDialog.setOpacity(0f);
            Timer fadeTimer = new Timer(20, null);
            final float[] alpha = {0f};
            fadeTimer.addActionListener(ev -> {
                alpha[0] += 0.08f;
                if (alpha[0] >= 1f) {
                    alpha[0] = 1f;
                    fadeTimer.stop();
                }
                farewellDialog.setOpacity(alpha[0]);
            });
            fadeTimer.start();
        } catch (Throwable ignored) {
            // setOpacity may not be supported on all platforms
        }

        farewellDialog.setVisible(true);
    }
}