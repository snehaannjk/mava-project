package com.GoAero.ui;

import com.GoAero.model.Admin;
import com.GoAero.model.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Main dashboard for administrators with modern tabbed management interface
 */
public class AdminDashboard extends JFrame {
    // Professional color scheme (consistent with other pages)
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private static final Color ACCENT_ORANGE = new Color(255, 152, 0);
    private static final Color DARK_BLUE = new Color(13, 71, 161);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color HOVER_BLUE = new Color(30, 136, 229);
    private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    private static final Color BACKGROUND_GRAY = new Color(250, 250, 250);
    private static final Color CARD_WHITE = Color.WHITE;
    private static final Color DANGER_RED = new Color(244, 67, 54);

    private Admin currentAdmin;
    private JTabbedPane tabbedPane;
    private JLabel welcomeLabel, subtitleLabel;
    private JButton logoutButton;

    public AdminDashboard() {
        currentAdmin = SessionManager.getInstance().getCurrentAdmin();
        if (currentAdmin == null) {
            JOptionPane.showMessageDialog(null, "Admin access required.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        setTitle("GoAero - Admin Dashboard");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Modern welcome label
        welcomeLabel = new JLabel("Welcome, Administrator " + currentAdmin.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(DARK_BLUE);

        // Subtitle label
        subtitleLabel = new JLabel("System Administration & Management Portal");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));

        // Modern styled logout button
        logoutButton = createStyledButton("🚪 Logout", DANGER_RED, Color.WHITE, 14);
        logoutButton.setPreferredSize(new Dimension(120, 40));

        // Modern styled tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(CARD_WHITE);
        tabbedPane.setForeground(DARK_BLUE);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add management panels with icons
        tabbedPane.addTab("👥 Users", new UserManagementPanel());
        tabbedPane.addTab("✈ Airports", new AirportManagementPanel());
        tabbedPane.addTab("🏢 Flight Owners", new FlightOwnerManagementPanel());
        tabbedPane.addTab("🛫 Flights", new FlightManagementPanel());
        tabbedPane.addTab("📋 Bookings", new BookingManagementPanel());
        tabbedPane.addTab("📊 Reports", new ReportsPanel());

        // Style individual tabs
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setBackgroundAt(i, LIGHT_GRAY);
            tabbedPane.setForegroundAt(i, DARK_BLUE);
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GRAY);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_GRAY);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Header section
        JPanel headerSection = createHeaderSection();
        mainPanel.add(headerSection, BorderLayout.NORTH);

        // Tabbed content section
        JPanel contentSection = createContentSection();
        mainPanel.add(contentSection, BorderLayout.CENTER);

        // Footer section
        JPanel footerSection = createFooterSection();
        mainPanel.add(footerSection, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderSection() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        // Title section
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_GRAY);

        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);

        // Logout button panel
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(BACKGROUND_GRAY);
        logoutPanel.add(logoutButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentSection() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CARD_WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Content title
        JLabel contentTitle = new JLabel("System Management");
        contentTitle.setFont(new Font("Arial", Font.BOLD, 18));
        contentTitle.setForeground(DARK_BLUE);
        contentTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        contentPanel.add(contentTitle, BorderLayout.NORTH);
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createFooterSection() {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(BACKGROUND_GRAY);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel footerLabel = new JLabel("GoAero Flight Booking System - Administrative Portal");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(120, 120, 120));

        footerPanel.add(footerLabel);
        return footerPanel;
    }

    private void setupEventListeners() {
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
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
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
        if (originalColor.equals(DANGER_RED)) {
            return new Color(255, 87, 87);
        } else {
            // For other colors, create a lighter version
            int r = Math.min(255, originalColor.getRed() + 20);
            int g = Math.min(255, originalColor.getGreen() + 20);
            int b = Math.min(255, originalColor.getBlue() + 20);
            return new Color(r, g, b);
        }
    }

    private void handleLogout() {
        // Create a custom styled confirmation dialog
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

        // Header (icon + title)
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        header.setOpaque(false);
        JLabel logoutIcon = new JLabel("🚪");
        logoutIcon.setFont(new Font("Arial", Font.BOLD, 28));
        header.add(logoutIcon);
        JLabel title = new JLabel("Confirm Logout");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(DARK_BLUE);
        header.add(title);

        content.add(header, BorderLayout.NORTH);

        // Message section with better styling
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        messagePanel.setBorder(new EmptyBorder(10, 8, 20, 8));
        
        JLabel mainMessage = new JLabel("Are you sure you want to logout?");
        mainMessage.setFont(new Font("Arial", Font.PLAIN, 15));
        mainMessage.setForeground(new Color(60, 60, 60));
        
        JLabel subMessage = new JLabel("You will be returned to the login screen.");
        subMessage.setFont(new Font("Arial", Font.ITALIC, 13));
        subMessage.setForeground(new Color(120, 120, 120));
        subMessage.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        messagePanel.add(mainMessage, BorderLayout.NORTH);
        messagePanel.add(subMessage, BorderLayout.CENTER);
        content.add(messagePanel, BorderLayout.CENTER);

        // Enhanced button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        JButton cancelBtn = createStyledButton("Cancel", new Color(108, 117, 125), Color.WHITE, 14);
        cancelBtn.setPreferredSize(new Dimension(100, 38));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton logoutBtn = createStyledButton("🚪 Logout", DANGER_RED, Color.WHITE, 14);
        logoutBtn.setPreferredSize(new Dimension(110, 38));
        logoutBtn.addActionListener(e -> {
            dialog.dispose();
            performLogout();
        });
        
        btnPanel.add(cancelBtn);
        btnPanel.add(logoutBtn);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(Math.max(420, dialog.getWidth()), dialog.getHeight());
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
        
        // Create a custom styled success dialog
        JDialog successDialog = new JDialog(this, "Logout Successful", true);
        successDialog.setUndecorated(true);

        // Main content with rounded background and shadow
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 16;
                // shadow
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(4, 8, getWidth() - 8, getHeight() - 8, arc, arc);
                // background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 12, arc, arc);
                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(18, 20, 16, 20));

        // Success header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        header.setOpaque(false);
        JLabel successIcon = new JLabel("✅");
        successIcon.setFont(new Font("Arial", Font.BOLD, 22));
        header.add(successIcon);
        JLabel title = new JLabel("Logout Successful");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(DARK_BLUE);
        header.add(title);

        content.add(header, BorderLayout.NORTH);

        // Message
        JLabel message = new JLabel("You have been logged out successfully.");
        message.setFont(new Font("Arial", Font.PLAIN, 14));
        message.setBorder(new EmptyBorder(8, 6, 12, 6));
        content.add(message, BorderLayout.CENTER);

        // Auto-close timer and manual button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        content.add(btnPanel, BorderLayout.SOUTH);

        successDialog.setContentPane(content);
        successDialog.pack();
        successDialog.setSize(Math.max(350, successDialog.getWidth()), successDialog.getHeight());
        successDialog.setLocationRelativeTo(this);

        // Auto-close timer (3 seconds)
        Timer autoCloseTimer = new Timer(2000, e -> {
            successDialog.dispose();
            SwingUtilities.invokeLater(() -> {
                new LandingPage().setVisible(true);
                dispose();
            });
        });
        autoCloseTimer.setRepeats(false);
        autoCloseTimer.start();

        // Fade-in effect
        try {
            successDialog.setOpacity(0f);
            Timer fadeTimer = new Timer(20, null);
            final float[] alpha = {0f};
            fadeTimer.addActionListener(ev -> {
                alpha[0] += 0.08f;
                if (alpha[0] >= 1f) {
                    alpha[0] = 1f;
                    fadeTimer.stop();
                }
                successDialog.setOpacity(alpha[0]);
            });
            fadeTimer.start();
        } catch (Throwable ignored) {
            // setOpacity may not be supported on all platforms
        }

        successDialog.setVisible(true);
    }
}