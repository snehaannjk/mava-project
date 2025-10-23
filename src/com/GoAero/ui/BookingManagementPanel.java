package com.GoAero.ui;

import com.GoAero.dao.BookingDAO;
import com.GoAero.model.Booking;

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
 * Panel for managing bookings in the admin dashboard with modern UI design
 */
public class BookingManagementPanel extends JPanel {
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
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JButton viewDetailsButton, updateStatusButton, updatePaymentButton, refreshButton;
    private JTextField searchField;
    private JButton searchButton;
    private BookingDAO bookingDAO;
    private List<Booking> bookings;

    public BookingManagementPanel() {
        bookingDAO = new BookingDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadBookings();
    }

    private void initializeComponents() {
        // Modern table setup (removed ID column for cleaner look)
        String[] columnNames = {"PNR", "Passenger", "Flight", "Route", "Date", "Amount", "Payment", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        bookingsTable = new JTable(tableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        bookingsTable.setRowHeight(45);
        bookingsTable.setGridColor(LIGHT_GRAY);
        bookingsTable.setSelectionBackground(new Color(230, 240, 255));
        bookingsTable.setSelectionForeground(DARK_BLUE);
        bookingsTable.setShowVerticalLines(true);
        bookingsTable.setShowHorizontalLines(true);

        // Style table header
        bookingsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        bookingsTable.getTableHeader().setBackground(DARK_BLUE);
        bookingsTable.getTableHeader().setForeground(Color.WHITE);
        bookingsTable.getTableHeader().setPreferredSize(new Dimension(0, 50));

        bookingsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Set optimized column widths (removed ID column)
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(100); // PNR
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(160); // Passenger
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(110); // Flight
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Route
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Date
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Amount
        bookingsTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Payment
        bookingsTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Status

        // Add custom cell renderers
        bookingsTable.getColumnModel().getColumn(0).setCellRenderer(new PNRCellRenderer());
        bookingsTable.getColumnModel().getColumn(5).setCellRenderer(new AmountCellRenderer());
        bookingsTable.getColumnModel().getColumn(6).setCellRenderer(new PaymentStatusCellRenderer());
        bookingsTable.getColumnModel().getColumn(7).setCellRenderer(new BookingStatusCellRenderer());

        // Modern styled buttons with icons
        viewDetailsButton = createStyledButton("📋 View Details", PRIMARY_BLUE, Color.WHITE, 14);
        updateStatusButton = createStyledButton("📝 Update Status", ACCENT_ORANGE, Color.WHITE, 14);
        updatePaymentButton = createStyledButton("💳 Update Payment", SUCCESS_GREEN, Color.WHITE, 14);
        refreshButton = createStyledButton("🔄 Refresh", DARK_BLUE, Color.WHITE, 14);

        // Modern search components
        searchField = createStyledTextField("Search by PNR, passenger name, or flight code...");
        searchButton = createStyledButton("🔍 Search", DARK_BLUE, Color.WHITE, 12);

        updateButtonStates();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_GRAY);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_GRAY);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Header section
        JPanel headerSection = createHeaderSection();
        mainPanel.add(headerSection, BorderLayout.NORTH);

        // Content section with table
        JPanel contentSection = createContentSection();
        mainPanel.add(contentSection, BorderLayout.CENTER);

        // Action buttons section
        JPanel actionSection = createActionSection();
        mainPanel.add(actionSection, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        viewDetailsButton.addActionListener(e -> viewBookingDetails());
        updateStatusButton.addActionListener(e -> updateBookingStatus());
        updatePaymentButton.addActionListener(e -> updatePaymentStatus());
        refreshButton.addActionListener(e -> loadBookings());
        searchButton.addActionListener(e -> searchBookings());
        
        // Enter key on search field
        searchField.addActionListener(e -> searchBookings());

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
            bookings = bookingDAO.findAll();
            displayBookings(bookings);
            updateInfoPanel();
        } catch (Exception e) {
            System.out.println("Failed to load bookings: " + e.getMessage());
        }
    }

    private void displayBookings(List<Booking> bookingList) {
        // Clear existing data
        tableModel.setRowCount(0);

        // Add bookings to table (removed ID column)
        for (Booking booking : bookingList) {
            Object[] row = {
                booking.getPnr(),
                booking.getUserFullName() != null ? booking.getUserFullName() : "N/A",
                booking.getFlightCode() != null ? booking.getFlightCode() : "N/A",
                booking.getFullRoute() != null ? booking.getFullRoute() : "Route TBD",
                booking.getDateOfDeparture() != null ? 
                    booking.getDateOfDeparture().format(DateTimeFormatter.ofPattern("MM-dd")) : "TBD",
                String.format("₹%.2f", booking.getAmount()),
                booking.getPaymentStatus().getDisplayName(),
                booking.getBookingStatus().getDisplayName()
            };
            tableModel.addRow(row);
        }

        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean hasSelection = bookingsTable.getSelectedRow() != -1;
        viewDetailsButton.setEnabled(hasSelection);
        updateStatusButton.setEnabled(hasSelection);
        updatePaymentButton.setEnabled(hasSelection);
    }

    private void updateInfoPanel() {
        // Update the info label in the south panel
        Component[] components = ((JPanel) getComponent(2)).getComponents();
        if (components.length > 0 && components[0] instanceof JLabel) {
            ((JLabel) components[0]).setText("Total Bookings: " + (bookings != null ? bookings.size() : 0));
        }
    }

    private void viewBookingDetails() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a booking to view details.");
            return;
        }

        Booking selectedBooking = bookings.get(selectedRow);
        new BookingDetailsDialog((Frame) SwingUtilities.getWindowAncestor(this), selectedBooking).setVisible(true);
    }

    private void updateBookingStatus() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a booking to update status.");
            return;
        }

        Booking selectedBooking = bookings.get(selectedRow);
        
        Booking.BookingStatus[] statuses = Booking.BookingStatus.values();
        String[] statusNames = new String[statuses.length];
        for (int i = 0; i < statuses.length; i++) {
            statusNames[i] = statuses[i].getDisplayName();
        }

        String selectedStatus = (String) JOptionPane.showInputDialog(
            this,
            "Select new booking status for PNR: " + selectedBooking.getPnr(),
            "Update Booking Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusNames,
            selectedBooking.getBookingStatus().getDisplayName()
        );

        if (selectedStatus != null) {
            // Find the corresponding enum value
            Booking.BookingStatus newStatus = null;
            for (Booking.BookingStatus status : statuses) {
                if (status.getDisplayName().equals(selectedStatus)) {
                    newStatus = status;
                    break;
                }
            }

            if (newStatus != null) {
                try {
                    boolean success = bookingDAO.updateBookingStatus(selectedBooking.getBookingId(), newStatus);
                    if (success) {
                        showSuccess("Booking status updated successfully.");
                        loadBookings();
                    } else {
                        showError("Failed to update booking status.");
                    }
                } catch (Exception e) {
                    showError("Update failed: " + e.getMessage());
                }
            }
        }
    }

    private void updatePaymentStatus() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a booking to update payment status.");
            return;
        }

        Booking selectedBooking = bookings.get(selectedRow);
        
        Booking.PaymentStatus[] statuses = Booking.PaymentStatus.values();
        String[] statusNames = new String[statuses.length];
        for (int i = 0; i < statuses.length; i++) {
            statusNames[i] = statuses[i].getDisplayName();
        }

        String selectedStatus = (String) JOptionPane.showInputDialog(
            this,
            "Select new payment status for PNR: " + selectedBooking.getPnr(),
            "Update Payment Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusNames,
            selectedBooking.getPaymentStatus().getDisplayName()
        );

        if (selectedStatus != null) {
            // Find the corresponding enum value
            Booking.PaymentStatus newStatus = null;
            for (Booking.PaymentStatus status : statuses) {
                if (status.getDisplayName().equals(selectedStatus)) {
                    newStatus = status;
                    break;
                }
            }

            if (newStatus != null) {
                try {
                    boolean success = bookingDAO.updatePaymentStatus(selectedBooking.getBookingId(), newStatus);
                    if (success) {
                        showSuccess("Payment status updated successfully.");
                        loadBookings();
                    } else {
                        showError("Failed to update payment status.");
                    }
                } catch (Exception e) {
                    showError("Update failed: " + e.getMessage());
                }
            }
        }
    }

    private void searchBookings() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty() || searchTerm.equals("Search by PNR, passenger name, or flight code...")) {
            displayBookings(bookings);
            return;
        }

        // Filter bookings based on search term
        List<Booking> filteredBookings = bookings.stream()
            .filter(booking ->
                booking.getPnr().toLowerCase().contains(searchTerm.toLowerCase()) ||
                (booking.getUserFullName() != null && booking.getUserFullName().toLowerCase().contains(searchTerm.toLowerCase())) ||
                (booking.getFlightCode() != null && booking.getFlightCode().toLowerCase().contains(searchTerm.toLowerCase()))
            )
            .collect(java.util.stream.Collectors.toList());

        displayBookings(filteredBookings);
    }

    private void showError(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Booking Management Error", true);
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

    private void showSuccess(String message) {
        // Create a custom styled success dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Success", true);
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

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        header.setOpaque(false);
        JLabel successIcon = new JLabel("📋✅");
        successIcon.setFont(new Font("Arial", Font.BOLD, 22));
        header.add(successIcon);
        JLabel title = new JLabel("Operation Successful");
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
        dialog.setSize(Math.max(380, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
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
     * Creates a styled text field with placeholder support
     */
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(30);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(350, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Set placeholder text
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        
        // Add focus effects
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_GRAY, 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
            }
        });
        
        return field;
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

        JLabel titleLabel = new JLabel("📋 Booking Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Manage passenger bookings, payments, and reservation status");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(BACKGROUND_GRAY);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 13));
        searchLabel.setForeground(DARK_BLUE);
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentSection() {
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setBackground(CARD_WHITE);
        contentContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Table title
        JLabel tableTitle = new JLabel("Passenger Reservations");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setForeground(DARK_BLUE);
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        scrollPane.setPreferredSize(new Dimension(0, 450));
        scrollPane.getViewport().setBackground(Color.WHITE);

        contentContainer.add(tableTitle, BorderLayout.NORTH);
        contentContainer.add(scrollPane, BorderLayout.CENTER);
        
        return contentContainer;
    }

    private JPanel createActionSection() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BorderLayout());
        actionPanel.setBackground(BACKGROUND_GRAY);
        actionPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Action buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonsPanel.setBackground(BACKGROUND_GRAY);
        
        viewDetailsButton.setPreferredSize(new Dimension(140, 40));
        updateStatusButton.setPreferredSize(new Dimension(150, 40));
        updatePaymentButton.setPreferredSize(new Dimension(170, 40));
        refreshButton.setPreferredSize(new Dimension(110, 40));
        
        buttonsPanel.add(viewDetailsButton);
        buttonsPanel.add(updateStatusButton);
        buttonsPanel.add(updatePaymentButton);
        buttonsPanel.add(refreshButton);

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setBackground(BACKGROUND_GRAY);
        JLabel infoLabel = new JLabel("Total Bookings: " + (bookings != null ? bookings.size() : 0));
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setForeground(DARK_BLUE);
        infoLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        infoLabel.setBackground(CARD_WHITE);
        infoLabel.setOpaque(true);
        infoPanel.add(infoLabel);

        actionPanel.add(buttonsPanel, BorderLayout.WEST);
        actionPanel.add(infoPanel, BorderLayout.EAST);

        return actionPanel;
    }

    /**
     * Custom cell renderer for PNR column
     */
    private static class PNRCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 12));

                if (!isSelected) {
                    setBackground(new Color(232, 245, 255));
                    setForeground(new Color(13, 71, 161));
                }
            }

            return this;
        }
    }

    /**
     * Custom cell renderer for amount column
     */
    private static class AmountCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                setHorizontalAlignment(SwingConstants.RIGHT);
                setFont(new Font("Arial", Font.BOLD, 12));

                String amountText = value.toString().replace(" ₹", "");
                try {
                    double amount = Double.parseDouble(amountText);
                    
                    if (!isSelected) {
                        if (amount >= 1000) {
                            setForeground(new Color(27, 94, 32));
                            setBackground(new Color(232, 245, 233));
                        } else if (amount >= 500) {
                            setForeground(new Color(56, 142, 60));
                            setBackground(new Color(237, 247, 237));
                        } else if (amount >= 200) {
                            setForeground(new Color(230, 81, 0));
                            setBackground(new Color(255, 248, 225));
                        } else {
                            setForeground(new Color(183, 28, 28));
                            setBackground(new Color(255, 235, 238));
                        }
                    }
                } catch (NumberFormatException e) {
                    // Handle invalid amount format
                }
            }

            return this;
        }
    }

    /**
     * Custom cell renderer for payment status column
     */
    private static class PaymentStatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 11));
                
                String status = value.toString().toUpperCase();
                
                if (!isSelected) {
                    switch (status) {
                        case "COMPLETED":
                            setBackground(new Color(232, 245, 233));
                            setForeground(new Color(27, 94, 32));
                            break;
                        case "PENDING":
                            setBackground(new Color(255, 248, 225));
                            setForeground(new Color(230, 81, 0));
                            break;
                        case "FAILED":
                            setBackground(new Color(255, 235, 238));
                            setForeground(new Color(183, 28, 28));
                            break;
                        default:
                            setBackground(new Color(245, 245, 245));
                            setForeground(new Color(120, 120, 120));
                    }
                }
            }

            return this;
        }
    }

    /**
     * Custom cell renderer for booking status column
     */
    private static class BookingStatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 11));
                
                String status = value.toString().toUpperCase();
                
                if (!isSelected) {
                    switch (status) {
                        case "CONFIRMED":
                            setBackground(new Color(232, 245, 233));
                            setForeground(new Color(27, 94, 32));
                            break;
                        case "PENDING":
                            setBackground(new Color(255, 248, 225));
                            setForeground(new Color(230, 81, 0));
                            break;
                        case "CANCELLED":
                            setBackground(new Color(255, 235, 238));
                            setForeground(new Color(183, 28, 28));
                            break;
                        default:
                            setBackground(new Color(245, 245, 245));
                            setForeground(new Color(120, 120, 120));
                    }
                }
            }

            return this;
        }
    }
}