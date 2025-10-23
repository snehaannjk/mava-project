package com.GoAero.ui;

import com.GoAero.dao.BookingDAO;
import com.GoAero.model.Booking;
import com.GoAero.model.SessionManager;
import com.GoAero.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Screen to display user's booking history with modern UI design
 */
public class BookingHistory extends JFrame {
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
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JButton viewDetailsButton, cancelBookingButton, refreshButton, closeButton;
    private BookingDAO bookingDAO;
    private User currentUser;
    private List<Booking> userBookings;

    public BookingHistory() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "Please login first.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        bookingDAO = new BookingDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadBookings();
    }

    private void initializeComponents() {
        setTitle("GoAero - My Bookings");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Modern table setup
        String[] columnNames = {"PNR", "Flight Code", "Route", "Departure Date", "Status", "Payment", "Amount (₹)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(tableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        bookingsTable.setRowHeight(40);
        bookingsTable.setGridColor(LIGHT_GRAY);
        bookingsTable.setSelectionBackground(new Color(230, 240, 255));
        bookingsTable.setSelectionForeground(DARK_BLUE);
        bookingsTable.setShowVerticalLines(true);
        bookingsTable.setShowHorizontalLines(true);

        // Style table header
        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        bookingsTable.getTableHeader().setBackground(DARK_BLUE);
        bookingsTable.getTableHeader().setForeground(Color.WHITE);
        bookingsTable.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Set column widths
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(100);  // PNR
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Flight Code
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(250); // Route
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(140); // Date
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Status
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Payment
        bookingsTable.getColumnModel().getColumn(6).setPreferredWidth(100);  // Amount

        // Add custom cell renderer for status column
        bookingsTable.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());

        bookingsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Modern styled buttons
        viewDetailsButton = createStyledButton("📋 View Details", PRIMARY_BLUE, Color.WHITE, 14);
        viewDetailsButton.setPreferredSize(new Dimension(140, 40));

        cancelBookingButton = createStyledButton("❌ Cancel Booking", DANGER_RED, Color.WHITE, 14);
        cancelBookingButton.setPreferredSize(new Dimension(160, 40));

        refreshButton = createStyledButton("🔄 Refresh", ACCENT_ORANGE, Color.WHITE, 14);
        refreshButton.setPreferredSize(new Dimension(120, 40));

        closeButton = createStyledButton("← Close", new Color(108, 117, 125), Color.WHITE, 14);
        closeButton.setPreferredSize(new Dimension(100, 40));

        updateButtonStates();
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

        // Table section
        JPanel tableSection = createTableSection();
        mainPanel.add(tableSection, BorderLayout.CENTER);

        // Action buttons section
        JPanel actionSection = createActionSection();
        mainPanel.add(actionSection, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
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

        JLabel titleLabel = new JLabel("My Booking History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Manage and track your flight bookings");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);

        // User info panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(BACKGROUND_GRAY);

        JLabel userLabel = new JLabel("Passenger: " + currentUser.getFullName());
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(DARK_BLUE);
        userLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        userLabel.setBackground(CARD_WHITE);
        userLabel.setOpaque(true);
        userPanel.add(userLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTableSection() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(CARD_WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Table title
        JLabel tableTitle = new JLabel("Your Bookings");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setForeground(DARK_BLUE);
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        scrollPane.setPreferredSize(new Dimension(0, 350));

        tableContainer.add(tableTitle, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        return tableContainer;
    }

    private JPanel createActionSection() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BorderLayout());
        actionPanel.setBackground(BACKGROUND_GRAY);
        actionPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Action buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonsPanel.setBackground(BACKGROUND_GRAY);
        buttonsPanel.add(viewDetailsButton);
        buttonsPanel.add(cancelBookingButton);
        buttonsPanel.add(refreshButton);

        // Close button (separate)
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.setBackground(BACKGROUND_GRAY);
        closePanel.add(closeButton);

        actionPanel.add(buttonsPanel, BorderLayout.CENTER);
        actionPanel.add(closePanel, BorderLayout.EAST);

        return actionPanel;
    }

    private void setupEventListeners() {
        viewDetailsButton.addActionListener(e -> viewBookingDetails());
        cancelBookingButton.addActionListener(e -> cancelSelectedBooking());
        refreshButton.addActionListener(e -> loadBookings());
        closeButton.addActionListener(e -> dispose());

        // Double-click to view details
        bookingsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewBookingDetails();
                }
            }
        });
    }

    private void loadBookings() {
        try {
            userBookings = bookingDAO.findByUserId(currentUser.getUserId());
            displayBookings();
        } catch (Exception e) {
            System.out.println("Failed to load bookings: " + e.getMessage());
        }
    }

    private void displayBookings() {
        // Clear existing data
        tableModel.setRowCount(0);

        if (userBookings.isEmpty()) {
            showInfo("No bookings found. Book your first flight to see it here!");
            return;
        }

        // Add bookings to table
        for (Booking booking : userBookings) {
            Object[] row = {
                booking.getPnr(),
                booking.getFlightCode(),
                booking.getFullRoute(),
                booking.getDateOfDeparture().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                booking.getBookingStatus().getDisplayName(),
                booking.getPaymentStatus().getDisplayName(),
                String.format("₹%.2f", booking.getAmount())
            };
            tableModel.addRow(row);
        }

        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean hasSelection = bookingsTable.getSelectedRow() != -1;
        viewDetailsButton.setEnabled(hasSelection);
        
        // Enable cancel button only for cancellable bookings
        boolean canCancel = false;
        if (hasSelection && userBookings != null) {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow < userBookings.size()) {
                Booking selectedBooking = userBookings.get(selectedRow);
                canCancel = selectedBooking.isCancellable();
            }
        }
        cancelBookingButton.setEnabled(canCancel);
    }

    private void viewBookingDetails() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a booking to view details.");
            return;
        }

        Booking selectedBooking = userBookings.get(selectedRow);
        new BookingDetailsDialog(this, selectedBooking).setVisible(true);
    }

    private void cancelSelectedBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a booking to cancel.");
            return;
        }

        Booking selectedBooking = userBookings.get(selectedRow);
        
        if (!selectedBooking.isCancellable()) {
            showError("This booking cannot be cancelled.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
            this,
            String.format("Are you sure you want to cancel booking %s?\n\nFlight: %s\nRoute: %s\nDeparture: %s",
                selectedBooking.getPnr(),
                selectedBooking.getFlightCode(),
                selectedBooking.getFullRoute(),
                selectedBooking.getDateOfDeparture().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            ),
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = bookingDAO.updateBookingStatus(
                    selectedBooking.getBookingId(), 
                    Booking.BookingStatus.CANCELLED
                );
                
                if (success) {
                    showSuccess("Booking cancelled successfully.");
                    loadBookings(); // Refresh the list
                } else {
                    showError("Failed to cancel booking. Please try again.");
                }
            } catch (Exception e) {
                showError("Cancellation failed: " + e.getMessage());
            }
        }
    }

    private void showError(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog(this, "Error", true);
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

        // Header (icon + title)
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        header.setOpaque(false);
        JLabel errorIcon = new JLabel("⚠");
        errorIcon.setFont(new Font("Arial", Font.BOLD, 24));
        errorIcon.setForeground(DANGER_RED);
        header.add(errorIcon);
        JLabel title = new JLabel("Error");
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

        // Fade-in effect
        try {
            dialog.setOpacity(0f);
            Timer fadeTimer = new Timer(25, null);
            final float[] alpha = {0f};
            fadeTimer.addActionListener(ev -> {
                alpha[0] += 0.1f;
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

    private void showInfo(String message) {
        // Create a custom styled info dialog with modern design
        JDialog dialog = new JDialog(this, "Information", true);
        dialog.setUndecorated(true);

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
                GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(248, 250, 252));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 12, arc, arc);
                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(20, 22, 18, 22));

        // Header (icon + title)
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        header.setOpaque(false);
        JLabel infoIcon = new JLabel("💡");
        infoIcon.setFont(new Font("Arial", Font.BOLD, 28));
        header.add(infoIcon);
        JLabel title = new JLabel("Getting Started");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(DARK_BLUE);
        header.add(title);

        content.add(header, BorderLayout.NORTH);

        // Message area with enhanced styling
        JTextArea msgArea = new JTextArea(message);
        msgArea.setFont(new Font("Arial", Font.PLAIN, 15));
        msgArea.setEditable(false);
        msgArea.setOpaque(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        msgArea.setForeground(new Color(60, 60, 60));
        msgArea.setBorder(new EmptyBorder(10, 8, 15, 8));
        content.add(msgArea, BorderLayout.CENTER);

        // Action suggestion panel
        JPanel suggestionPanel = new JPanel(new BorderLayout());
        suggestionPanel.setOpaque(false);
        suggestionPanel.setBorder(new EmptyBorder(5, 8, 10, 8));
        
        JLabel suggestion = new JLabel("💼 Ready to book your first flight?");
        suggestion.setFont(new Font("Arial", Font.ITALIC, 13));
        suggestion.setForeground(new Color(100, 100, 100));
        suggestionPanel.add(suggestion, BorderLayout.WEST);
        
        content.add(suggestionPanel, BorderLayout.CENTER);

        // Enhanced button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        JButton bookFlightBtn = createStyledButton("✈ Book Flight", PRIMARY_BLUE, Color.WHITE, 14);
        bookFlightBtn.setPreferredSize(new Dimension(120, 38));
        bookFlightBtn.addActionListener(e -> {
            dialog.dispose();
            // Close booking history and return to main dashboard
            dispose();
            // Note: In a full implementation, this would navigate to the flight booking interface
        });
        
        JButton okBtn = createStyledButton("Got it!", SUCCESS_GREEN, Color.WHITE, 14);
        okBtn.setPreferredSize(new Dimension(90, 38));
        okBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(bookFlightBtn);
        btnPanel.add(okBtn);
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

    private void showSuccess(String message) {
        // Create a custom styled success dialog
        JDialog dialog = new JDialog(this, "Success", true);
        dialog.setUndecorated(true);

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
        content.setBorder(new EmptyBorder(16, 18, 14, 18));

        // Header (icon + title)
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        header.setOpaque(false);
        JLabel successIcon = new JLabel("✅");
        successIcon.setFont(new Font("Arial", Font.BOLD, 22));
        header.add(successIcon);
        JLabel title = new JLabel("Success");
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
        JButton ok = createStyledButton("Great!", SUCCESS_GREEN, Color.WHITE, 14);
        ok.setPreferredSize(new Dimension(100, 36));
        ok.addActionListener(e -> dialog.dispose());
        btnPanel.add(ok);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(Math.max(420, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(this);

        // Fade-in effect
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

    /**
     * Custom cell renderer for booking status column
     */
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String status = value.toString().toUpperCase();
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 12));

                if (!isSelected) {
                    switch (status) {
                        case "CONFIRMED":
                            setBackground(new Color(232, 245, 233));
                            setForeground(new Color(27, 94, 32));
                            break;
                        case "CANCELLED":
                            setBackground(new Color(255, 235, 238));
                            setForeground(new Color(183, 28, 28));
                            break;
                        case "PENDING":
                            setBackground(new Color(255, 248, 225));
                            setForeground(new Color(230, 81, 0));
                            break;
                        default:
                            setBackground(Color.WHITE);
                            setForeground(Color.BLACK);
                    }
                }
            }

            return this;
        }
    }
}