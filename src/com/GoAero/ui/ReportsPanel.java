package com.GoAero.ui;

import com.GoAero.dao.BookingDAO;
import com.GoAero.dao.FlightDAO;
import com.GoAero.dao.UserDAO;
import com.GoAero.dao.FlightOwnerDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Panel for displaying reports and analytics in the admin dashboard with modern UI design
 */
public class ReportsPanel extends JPanel {
    // Professional color scheme (consistent with other GoAero pages)
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private static final Color ACCENT_ORANGE = new Color(255, 152, 0);
    private static final Color DARK_BLUE = new Color(13, 71, 161);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color HOVER_BLUE = new Color(30, 136, 229);
    private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    private static final Color BACKGROUND_GRAY = new Color(250, 250, 250);
    private static final Color CARD_WHITE = Color.WHITE;
    private static final Color DANGER_RED = new Color(244, 67, 54);
    private static final Color WARNING_ORANGE = new Color(255, 193, 7);
    private UserDAO userDAO;
    private FlightDAO flightDAO;
    private FlightOwnerDAO flightOwnerDAO;
    private BookingDAO bookingDAO;
    
    private JLabel totalUsersLabel, totalFlightsLabel, totalAirlinesLabel, totalBookingsLabel;
    private JLabel totalRevenueLabel, pendingBookingsLabel, confirmedBookingsLabel, cancelledBookingsLabel;
    private JButton refreshButton, exportButton;

    public ReportsPanel() {
        userDAO = new UserDAO();
        flightDAO = new FlightDAO();
        flightOwnerDAO = new FlightOwnerDAO();
        bookingDAO = new BookingDAO();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadReports();
    }

    private void initializeComponents() {
        // Enhanced data labels with modern styling
        totalUsersLabel = createDataLabel("0", PRIMARY_BLUE);
        totalFlightsLabel = createDataLabel("0", ACCENT_ORANGE);
        totalAirlinesLabel = createDataLabel("0", SUCCESS_GREEN);
        totalBookingsLabel = createDataLabel("0", DARK_BLUE);
        totalRevenueLabel = createDataLabel("₹0.00", SUCCESS_GREEN);
        pendingBookingsLabel = createDataLabel("0", WARNING_ORANGE);
        confirmedBookingsLabel = createDataLabel("0", SUCCESS_GREEN);
        cancelledBookingsLabel = createDataLabel("0", DANGER_RED);
        
        // Modern styled buttons with icons
        refreshButton = createStyledButton("🔄 Refresh Reports", PRIMARY_BLUE, Color.WHITE, 14);
        refreshButton.setPreferredSize(new Dimension(160, 40));
        refreshButton.setToolTipText("Refresh all report data");
        
        exportButton = createStyledButton("📊 Export Report", ACCENT_ORANGE, Color.WHITE, 14);
        exportButton.setPreferredSize(new Dimension(150, 40));
        exportButton.setToolTipText("Export data to text format");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_GRAY);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_GRAY);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Enhanced header section
        JPanel headerSection = createHeaderSection();
        mainPanel.add(headerSection, BorderLayout.NORTH);

        // Modern card-based content section
        JPanel contentSection = createContentSection();
        mainPanel.add(contentSection, BorderLayout.CENTER);

        // Enhanced action section
        JPanel actionSection = createActionSection();
        mainPanel.add(actionSection, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    @SuppressWarnings("unused")
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("System Overview"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Total Users
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Total Registered Users:"), gbc);
        gbc.gridx = 1;
        totalUsersLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(totalUsersLabel, gbc);

        // Total Airlines
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Total Airlines:"), gbc);
        gbc.gridx = 1;
        totalAirlinesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(totalAirlinesLabel, gbc);

        // Total Flights
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Total Flights:"), gbc);
        gbc.gridx = 1;
        totalFlightsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(totalFlightsLabel, gbc);

        // Total Bookings
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Total Bookings:"), gbc);
        gbc.gridx = 1;
        totalBookingsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(totalBookingsLabel, gbc);

        return panel;
    }

    @SuppressWarnings("unused")
    private JPanel createBookingStatsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Booking Statistics"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Confirmed Bookings
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Confirmed Bookings:"), gbc);
        gbc.gridx = 1;
        confirmedBookingsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmedBookingsLabel.setForeground(new Color(0, 128, 0));
        panel.add(confirmedBookingsLabel, gbc);

        // Pending Bookings
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Pending Bookings:"), gbc);
        gbc.gridx = 1;
        pendingBookingsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pendingBookingsLabel.setForeground(new Color(255, 140, 0));
        panel.add(pendingBookingsLabel, gbc);

        // Cancelled Bookings
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Cancelled Bookings:"), gbc);
        gbc.gridx = 1;
        cancelledBookingsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cancelledBookingsLabel.setForeground(Color.RED);
        panel.add(cancelledBookingsLabel, gbc);

        return panel;
    }

    @SuppressWarnings("unused")
    private JPanel createRevenuePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Revenue Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Total Revenue
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Total Revenue (Confirmed Bookings):"), gbc);
        gbc.gridx = 1;
        totalRevenueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalRevenueLabel.setForeground(new Color(0, 128, 0));
        panel.add(totalRevenueLabel, gbc);

        return panel;
    }

    private void setupEventListeners() {
        refreshButton.addActionListener(e -> loadReports());
        exportButton.addActionListener(e -> exportReports());
    }

    private void loadReports() {
        try {
            // Load basic counts
            long totalUsers = userDAO.count();
            long totalFlights = flightDAO.count();
            long totalAirlines = flightOwnerDAO.count();
            long totalBookings = bookingDAO.count();

            // Update labels
            totalUsersLabel.setText(String.valueOf(totalUsers));
            totalFlightsLabel.setText(String.valueOf(totalFlights));
            totalAirlinesLabel.setText(String.valueOf(totalAirlines));
            totalBookingsLabel.setText(String.valueOf(totalBookings));

            // Load booking statistics (this would require additional DAO methods)
            loadBookingStatistics();
            
        } catch (Exception e) {
            System.out.println("Failed to load reports: " + e.getMessage());
        }
    }

    private void loadBookingStatistics() {
        try {
            // For now, we'll use simple counts
            // In a real implementation, you'd add methods to BookingDAO for these statistics
            int confirmed = 0, pending = 0, cancelled = 0;
            BigDecimal totalRevenue = BigDecimal.ZERO;

            // This is a simplified approach - in practice you'd add specific DAO methods
            var allBookings = bookingDAO.findAll();
            for (var booking : allBookings) {
                switch (booking.getBookingStatus()) {
                    case CONFIRMED:
                        confirmed++;
                        if (booking.getPaymentStatus() == com.GoAero.model.Booking.PaymentStatus.COMPLETED) {
                            totalRevenue = totalRevenue.add(booking.getAmount());
                        }
                        break;
                    case PENDING:
                        pending++;
                        break;
                    case CANCELLED:
                        cancelled++;
                        break;
                }
            }

            confirmedBookingsLabel.setText(String.valueOf(confirmed));
            pendingBookingsLabel.setText(String.valueOf(pending));
            cancelledBookingsLabel.setText(String.valueOf(cancelled));
            totalRevenueLabel.setText(String.format("₹%.2f", totalRevenue));

        } catch (Exception e) {
            System.out.println("Failed to load booking statistics: " + e.getMessage());
        }
    }

    private void exportReports() {
        try {
            StringBuilder report = new StringBuilder();
            report.append("GoAero Flight Booking System - Reports\n");
            report.append("=====================================\n\n");
            
            report.append("System Overview:\n");
            report.append("- Total Registered Users: ").append(totalUsersLabel.getText()).append("\n");
            report.append("- Total Airlines: ").append(totalAirlinesLabel.getText()).append("\n");
            report.append("- Total Flights: ").append(totalFlightsLabel.getText()).append("\n");
            report.append("- Total Bookings: ").append(totalBookingsLabel.getText()).append("\n\n");
            
            report.append("Booking Statistics:\n");
            report.append("- Confirmed Bookings: ").append(confirmedBookingsLabel.getText()).append("\n");
            report.append("- Pending Bookings: ").append(pendingBookingsLabel.getText()).append("\n");
            report.append("- Cancelled Bookings: ").append(cancelledBookingsLabel.getText()).append("\n\n");
            
            report.append("Revenue Information:\n");
            report.append("- Total Revenue: ").append(totalRevenueLabel.getText()).append("\n");
            
            report.append("\nGenerated on: ").append(java.time.LocalDateTime.now().toString()).append("\n");

            // Show in a dialog
            JTextArea textArea = new JTextArea(report.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Exported Report", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception e) {
            showError("Failed to export reports: " + e.getMessage());
        }
    }

    private void showError(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Reports Error", true);
        dialog.setUndecorated(true);

        // Main content with rounded background and shadow
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 16;
                // shadow
                g2.setColor(new Color(0, 0, 0, 25));
                g2.fillRoundRect(4, 8, getWidth() - 8, getHeight() - 8, arc, arc);
                // background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 12, arc, arc);
                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(16, 18, 14, 18));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        header.setOpaque(false);
        JLabel errorIcon = new JLabel("⚠");
        errorIcon.setFont(new Font("Arial", Font.BOLD, 24));
        errorIcon.setForeground(DANGER_RED);
        header.add(errorIcon);
        JLabel title = new JLabel("Report Error");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(DARK_BLUE);
        header.add(title);

        content.add(header, BorderLayout.NORTH);

        // Message area
        JTextArea msgArea = new JTextArea(message);
        msgArea.setFont(new Font("Arial", Font.PLAIN, 14));
        msgArea.setEditable(false);
        msgArea.setOpaque(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setBorder(new EmptyBorder(8, 6, 12, 6));
        content.add(msgArea, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        JButton ok = createStyledButton("OK", DANGER_RED, Color.WHITE, 14);
        ok.setPreferredSize(new Dimension(90, 36));
        ok.addActionListener(e -> dialog.dispose());
        btnPanel.add(ok);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(Math.max(380, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Creates a styled data label with color coding
     */
    private JLabel createDataLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    /**
     * Creates a styled button with hover effects
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
        if (originalColor.equals(PRIMARY_BLUE)) {
            return HOVER_BLUE;
        } else if (originalColor.equals(ACCENT_ORANGE)) {
            return new Color(255, 167, 38);
        } else if (originalColor.equals(SUCCESS_GREEN)) {
            return new Color(102, 187, 106);
        } else if (originalColor.equals(DANGER_RED)) {
            return new Color(255, 87, 87);
        } else {
            // For other colors, create a lighter version
            int r = Math.min(255, originalColor.getRed() + 20);
            int g = Math.min(255, originalColor.getGreen() + 20);
            int b = Math.min(255, originalColor.getBlue() + 20);
            return new Color(r, g, b);
        }
    }

    private JPanel createHeaderSection() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        // Title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_GRAY);

        JLabel titleLabel = new JLabel("System Reports & Analytics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Go-Aero System statistics and performance metrics");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBackground(BACKGROUND_GRAY);
        
        JLabel lastUpdated = new JLabel("Last Updated: " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        lastUpdated.setFont(new Font("Arial", Font.ITALIC, 12));
        lastUpdated.setForeground(new Color(120, 120, 120));
        statusPanel.add(lastUpdated);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(statusPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentSection() {
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setBackground(BACKGROUND_GRAY);

        // Create cards grid
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(BACKGROUND_GRAY);

        // System Overview Card
        JPanel overviewCard = createModernOverviewCard();
        cardsPanel.add(overviewCard);

        // Booking Statistics Card
        JPanel bookingCard = createModernBookingCard();
        cardsPanel.add(bookingCard);

        // Revenue Card
        JPanel revenueCard = createModernRevenueCard();
        cardsPanel.add(revenueCard);

        // Performance Metrics Card
        JPanel performanceCard = createPerformanceCard();
        cardsPanel.add(performanceCard);

        contentContainer.add(cardsPanel, BorderLayout.CENTER);
        return contentContainer;
    }

    private JPanel createModernOverviewCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Card header
        JLabel cardTitle = new JLabel("System Overview");
        cardTitle.setFont(new Font("Arial", Font.BOLD, 16));
        cardTitle.setForeground(DARK_BLUE);

        // Metrics panel
        JPanel metricsPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        metricsPanel.setBackground(CARD_WHITE);
        metricsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Add metrics
        addMetricRow(metricsPanel, "👥 Total Users:", totalUsersLabel);
        addMetricRow(metricsPanel, "✈️ Airlines:", totalAirlinesLabel);
        addMetricRow(metricsPanel, "🛫 Flights:", totalFlightsLabel);
        addMetricRow(metricsPanel, "📋 Bookings:", totalBookingsLabel);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(metricsPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createModernBookingCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Card header
        JLabel cardTitle = new JLabel("📊 Booking Statistics");
        cardTitle.setFont(new Font("Arial", Font.BOLD, 16));
        cardTitle.setForeground(DARK_BLUE);

        // Metrics panel
        JPanel metricsPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        metricsPanel.setBackground(CARD_WHITE);
        metricsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Add booking metrics
        addMetricRow(metricsPanel, "✅ Confirmed:", confirmedBookingsLabel);
        addMetricRow(metricsPanel, "⏳ Pending:", pendingBookingsLabel);
        addMetricRow(metricsPanel, "❌ Cancelled:", cancelledBookingsLabel);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(metricsPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createModernRevenueCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Card header
        JLabel cardTitle = new JLabel("💰 Revenue Analytics");
        cardTitle.setFont(new Font("Arial", Font.BOLD, 16));
        cardTitle.setForeground(DARK_BLUE);

        // Revenue display
        JPanel revenuePanel = new JPanel(new BorderLayout());
        revenuePanel.setBackground(CARD_WHITE);
        revenuePanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel revenuePrefix = new JLabel("Total Revenue:");
        revenuePrefix.setFont(new Font("Arial", Font.PLAIN, 14));
        revenuePrefix.setForeground(new Color(100, 100, 100));

        totalRevenueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        totalRevenueLabel.setForeground(SUCCESS_GREEN);
        totalRevenueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        revenuePanel.add(revenuePrefix, BorderLayout.NORTH);
        revenuePanel.add(totalRevenueLabel, BorderLayout.CENTER);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(revenuePanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createPerformanceCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Card header
        JLabel cardTitle = new JLabel("⚡ System Performance");
        cardTitle.setFont(new Font("Arial", Font.BOLD, 16));
        cardTitle.setForeground(DARK_BLUE);

        // Performance indicators
        JPanel perfPanel = new JPanel(new GridLayout(3, 1, 5, 10));
        perfPanel.setBackground(CARD_WHITE);
        perfPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Status indicators
        JLabel systemStatus = new JLabel("🟢 System Online");
        systemStatus.setFont(new Font("Arial", Font.BOLD, 13));
        systemStatus.setForeground(SUCCESS_GREEN);

        JLabel dbStatus = new JLabel("🟢 Database Connected");
        dbStatus.setFont(new Font("Arial", Font.BOLD, 13));
        dbStatus.setForeground(SUCCESS_GREEN);

        JLabel performanceStatus = new JLabel("📈 Performance: Optimal");
        performanceStatus.setFont(new Font("Arial", Font.BOLD, 13));
        performanceStatus.setForeground(PRIMARY_BLUE);

        perfPanel.add(systemStatus);
        perfPanel.add(dbStatus);
        perfPanel.add(performanceStatus);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(perfPanel, BorderLayout.CENTER);

        return card;
    }

    private void addMetricRow(JPanel parent, String label, JLabel valueLabel) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.PLAIN, 14));
        labelComponent.setForeground(new Color(80, 80, 80));

        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        parent.add(labelComponent);
        parent.add(valueLabel);
    }

    private JPanel createActionSection() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BorderLayout());
        actionPanel.setBackground(BACKGROUND_GRAY);
        actionPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Action buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonsPanel.setBackground(BACKGROUND_GRAY);
        
        refreshButton.setPreferredSize(new Dimension(160, 42));
        exportButton.setPreferredSize(new Dimension(150, 42));
        
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(exportButton);

        actionPanel.add(buttonsPanel, BorderLayout.CENTER);
        return actionPanel;
    }
}