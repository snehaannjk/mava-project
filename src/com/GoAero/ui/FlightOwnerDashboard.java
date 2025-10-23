package com.GoAero.ui;

import com.GoAero.model.FlightOwner;
import com.GoAero.model.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Main dashboard for flight owners (airline companies) with modern flight management interface
 */
public class FlightOwnerDashboard extends JFrame {
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

    private FlightOwner currentFlightOwner;
    private JTabbedPane tabbedPane;
    private JLabel welcomeLabel, subtitleLabel;
    private JButton logoutButton;

    public FlightOwnerDashboard() {
        currentFlightOwner = SessionManager.getInstance().getCurrentFlightOwner();
        if (currentFlightOwner == null) {
            JOptionPane.showMessageDialog(null, "Flight owner access required.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        setTitle("GoAero - " + currentFlightOwner.getCompanyName() + " Airline Portal");
        setSize(1500, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(1200, 800));

        // Set the window to open in maximized state
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Enhanced welcome label with airline branding
        welcomeLabel = new JLabel("✈️ " + currentFlightOwner.getCompanyName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(DARK_BLUE);

        // Enhanced subtitle with more context
        subtitleLabel = new JLabel("Airline Code: " + currentFlightOwner.getCompanyCode() + 
                                  " | Professional Flight Management Portal");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));

        // Enhanced logout button with better styling
        logoutButton = createStyledButton("🚪 Secure Logout", DANGER_RED, Color.WHITE, 14);
        logoutButton.setPreferredSize(new Dimension(140, 42));
        logoutButton.setToolTipText("Securely logout from your airline portal");

        // Professional styled tabbed pane with enhanced design
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 15));
        tabbedPane.setBackground(CARD_WHITE);
        tabbedPane.setForeground(DARK_BLUE);
        tabbedPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // Add enhanced management panels with better icons and descriptions
        tabbedPane.addTab("✈️ Flight Operations", new OwnerFlightManagementPanel());
        tabbedPane.setToolTipTextAt(0, "Manage your airline's flight schedules and operations");
        
        tabbedPane.addTab("📊 Analytics & Reports", new OwnerBookingStatsPanel());
        tabbedPane.setToolTipTextAt(1, "View booking statistics and performance analytics");
        
        tabbedPane.addTab("🏢 Airline Profile", new OwnerProfilePanel());
        tabbedPane.setToolTipTextAt(2, "Manage your company profile and settings");

        // Enhanced tab styling with better visual hierarchy
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setBackgroundAt(i, new Color(248, 250, 252));
            tabbedPane.setForegroundAt(i, DARK_BLUE);
        }

        // Set selected tab appearance
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (i == selectedIndex) {
                    tabbedPane.setBackgroundAt(i, PRIMARY_BLUE);
                    tabbedPane.setForegroundAt(i, Color.WHITE);
                } else {
                    tabbedPane.setBackgroundAt(i, new Color(248, 250, 252));
                    tabbedPane.setForegroundAt(i, DARK_BLUE);
                }
            }
        });

        // Set initial selected tab styling
        tabbedPane.setBackgroundAt(0, PRIMARY_BLUE);
        tabbedPane.setForegroundAt(0, Color.WHITE);
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

        // Enhanced title section with airline branding card
        JPanel titleCard = new JPanel();
        titleCard.setLayout(new BoxLayout(titleCard, BoxLayout.Y_AXIS));
        titleCard.setBackground(CARD_WHITE);
        titleCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(20, 25, 20, 25)
        ));

        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add airline status indicator
        JLabel statusLabel = new JLabel("🟢 Portal Active");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(SUCCESS_GREEN);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleCard.add(welcomeLabel);
        titleCard.add(Box.createVerticalStrut(8));
        titleCard.add(subtitleLabel);
        titleCard.add(Box.createVerticalStrut(10));
        titleCard.add(statusLabel);

        // Enhanced logout panel with additional info
        JPanel logoutPanel = new JPanel();
        logoutPanel.setLayout(new BoxLayout(logoutPanel, BoxLayout.Y_AXIS));
        logoutPanel.setBackground(BACKGROUND_GRAY);
        logoutPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Quick stats panel
        JPanel quickStats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        quickStats.setBackground(CARD_WHITE);
        quickStats.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel flightCount = new JLabel("✈️ " + (currentFlightOwner.getFlightCount() > 0 ? 
                                              currentFlightOwner.getFlightCount() : "0") + " Flights");
        flightCount.setFont(new Font("Arial", Font.BOLD, 13));
        flightCount.setForeground(PRIMARY_BLUE);

        JLabel activeStatus = new JLabel("📊 Dashboard");
        activeStatus.setFont(new Font("Arial", Font.BOLD, 13));
        activeStatus.setForeground(ACCENT_ORANGE);

        quickStats.add(flightCount);
        quickStats.add(new JSeparator(SwingConstants.VERTICAL));
        quickStats.add(activeStatus);
        quickStats.add(logoutButton);

        logoutPanel.add(quickStats);

        headerPanel.add(titleCard, BorderLayout.WEST);
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

        // Enhanced content header with navigation breadcrumb
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel contentTitle = new JLabel("Airline Management Dashboard");
        contentTitle.setFont(new Font("Arial", Font.BOLD, 18));
        contentTitle.setForeground(DARK_BLUE);

        // Add breadcrumb navigation
        JLabel breadcrumb = new JLabel("Home > Airline Portal > " + currentFlightOwner.getCompanyCode());
        breadcrumb.setFont(new Font("Arial", Font.ITALIC, 12));
        breadcrumb.setForeground(new Color(120, 120, 120));

        headerPanel.add(contentTitle, BorderLayout.WEST);
        headerPanel.add(breadcrumb, BorderLayout.EAST);

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createFooterSection() {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BorderLayout());
        footerPanel.setBackground(BACKGROUND_GRAY);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Enhanced footer with airline stats
        JPanel footerContent = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        footerContent.setBackground(CARD_WHITE);
        footerContent.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(10, 20, 10, 20)
        ));

        JLabel footerLabel = new JLabel("GoAero Flight Booking System - Airline Portal");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(120, 120, 120));

        JLabel versionLabel = new JLabel("| Version 2.1 Professional");
        versionLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        versionLabel.setForeground(new Color(150, 150, 150));

        footerContent.add(footerLabel);
        footerContent.add(versionLabel);

        footerPanel.add(footerContent, BorderLayout.CENTER);
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
        } else if (originalColor.equals(PRIMARY_BLUE)) {
            return HOVER_BLUE;
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
        JLabel logoutIcon = new JLabel("✈");
        logoutIcon.setFont(new Font("Arial", Font.BOLD, 28));
        logoutIcon.setForeground(ACCENT_ORANGE);
        header.add(logoutIcon);
        JLabel title = new JLabel("Confirm Logout");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(DARK_BLUE);
        header.add(title);

        content.add(header, BorderLayout.NORTH);

        // Message section with airline-specific styling
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        messagePanel.setBorder(new EmptyBorder(10, 8, 20, 8));
        
        JLabel mainMessage = new JLabel("Are you sure you want to logout?");
        mainMessage.setFont(new Font("Arial", Font.PLAIN, 15));
        mainMessage.setForeground(new Color(60, 60, 60));
        
        JLabel subMessage = new JLabel("You will need to re-enter your airline credentials to access the portal again.");
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
        
        JButton cancelBtn = createStyledButton("Stay Logged In", new Color(108, 117, 125), Color.WHITE, 14);
        cancelBtn.setPreferredSize(new Dimension(130, 38));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton logoutBtn = createStyledButton("✈ Logout", DANGER_RED, Color.WHITE, 14);
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
        dialog.setSize(Math.max(460, dialog.getWidth()), dialog.getHeight());
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
                // background with subtle gradient
                GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(248, 252, 255));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 12, arc, arc);
                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(18, 20, 16, 20));

        // Success header with airline theme
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

        // Message with airline context
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        messagePanel.setBorder(new EmptyBorder(8, 6, 12, 6));
        
        JLabel message = new JLabel("Logged out suucessfully. Redirecting...");
        message.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel subMessage = new JLabel("Thank you for using GoAero's airline portal.");
        subMessage.setFont(new Font("Arial", Font.ITALIC, 12));
        subMessage.setForeground(new Color(100, 100, 100));
        subMessage.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        messagePanel.add(message, BorderLayout.NORTH);
        messagePanel.add(subMessage, BorderLayout.CENTER);
        content.add(messagePanel, BorderLayout.CENTER);

        // Auto-close timer and manual button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        content.add(btnPanel, BorderLayout.SOUTH);

        successDialog.setContentPane(content);
        successDialog.pack();
        successDialog.setSize(Math.max(380, successDialog.getWidth()), successDialog.getHeight());
        successDialog.setLocationRelativeTo(this);

        // Auto-close timer (2 seconds for airline context)
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
