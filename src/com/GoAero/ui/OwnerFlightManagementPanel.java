package com.GoAero.ui;

import com.GoAero.dao.FlightDAO;
import com.GoAero.model.Flight;
import com.GoAero.model.FlightOwner;
import com.GoAero.model.SessionManager;

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
 * Panel for flight owners to manage their own flights with modern UI design
 */
public class OwnerFlightManagementPanel extends JPanel {
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
    private JTable flightsTable;
    private DefaultTableModel tableModel;
    private JButton addFlightButton, editFlightButton, deleteFlightButton, refreshButton;
    private JTextField searchField;
    private JButton searchButton;
    private FlightDAO flightDAO;
    private FlightOwner currentOwner;
    private List<Flight> flights;

    public OwnerFlightManagementPanel() {
        currentOwner = SessionManager.getInstance().getCurrentFlightOwner();
        if (currentOwner == null) {
            JLabel errorLabel = new JLabel("Access denied. Please login as a flight owner.");
            add(errorLabel);
            return;
        }
        
        flightDAO = new FlightDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadFlights();
    }

    private void initializeComponents() {
        // Modern table setup
        String[] columnNames = {"Flight Code", "Flight Name", "Route", "Departure", "Arrival", "Price", "Capacity", "Available"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        flightsTable = new JTable(tableModel);
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        flightsTable.setRowHeight(45);
        flightsTable.setGridColor(LIGHT_GRAY);
        flightsTable.setSelectionBackground(new Color(230, 240, 255));
        flightsTable.setSelectionForeground(DARK_BLUE);
        flightsTable.setShowVerticalLines(true);
        flightsTable.setShowHorizontalLines(true);

        // Style table header
        flightsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        flightsTable.getTableHeader().setBackground(DARK_BLUE);
        flightsTable.getTableHeader().setForeground(Color.WHITE);
        flightsTable.getTableHeader().setPreferredSize(new Dimension(0, 50));

        flightsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Set column widths (removed ID column)
        flightsTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Flight Code
        flightsTable.getColumnModel().getColumn(1).setPreferredWidth(140); // Flight Name
        flightsTable.getColumnModel().getColumn(2).setPreferredWidth(220); // Route
        flightsTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Departure
        flightsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Arrival
        flightsTable.getColumnModel().getColumn(5).setPreferredWidth(90);  // Price
        flightsTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Capacity
        flightsTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Available

        // Add custom cell renderer for availability
        flightsTable.getColumnModel().getColumn(7).setCellRenderer(new AvailabilityCellRenderer());

        // Modern styled buttons with icons
        addFlightButton = createStyledButton("✈ Add Flight", PRIMARY_BLUE, Color.WHITE, 14);
        editFlightButton = createStyledButton("✏ Edit Flight", ACCENT_ORANGE, Color.WHITE, 14);
        deleteFlightButton = createStyledButton("🗑 Delete Flight", DANGER_RED, Color.WHITE, 14);
        refreshButton = createStyledButton("🔄 Refresh", SUCCESS_GREEN, Color.WHITE, 14);

        // Modern search components
        searchField = createStyledTextField("Search flights...");
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
        addFlightButton.addActionListener(e -> addFlight());
        editFlightButton.addActionListener(e -> editFlight());
        deleteFlightButton.addActionListener(e -> deleteFlight());
        refreshButton.addActionListener(e -> loadFlights());
        searchButton.addActionListener(e -> searchFlights());
        
        // Enter key on search field
        searchField.addActionListener(e -> searchFlights());

        // Double-click to edit
        flightsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editFlight();
                }
            }
        });
    }

    private void loadFlights() {
        try {
            flights = flightDAO.findByCompanyId(currentOwner.getOwnerId());
            displayFlights(flights);
            updateInfoPanel();
        } catch (Exception e) {
            System.out.println("Failed to load flights: " + e.getMessage());
        }
    }

    private void displayFlights(List<Flight> flightList) {
        // Clear existing data
        tableModel.setRowCount(0);

        // Add flights to table (removed ID column)
        for (Flight flight : flightList) {
            Object[] row = {
                flight.getFlightCode(),
                flight.getFlightName() != null ? flight.getFlightName() : "",
                flight.getRoute(),
                flight.getDepartureTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
                flight.getDestinationTime().format(DateTimeFormatter.ofPattern("MM-dd HH:mm")),
                String.format("₹%.2f", flight.getPrice()),
                flight.getCapacity(),
                flight.getAvailableSeats()
            };
            tableModel.addRow(row);
        }

        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean hasSelection = flightsTable.getSelectedRow() != -1;
        editFlightButton.setEnabled(hasSelection);
        deleteFlightButton.setEnabled(hasSelection);
    }

    private void updateInfoPanel() {
        // Update the info label in the south panel
        Component[] components = ((JPanel) getComponent(2)).getComponents();
        if (components.length > 0 && components[0] instanceof JLabel) {
            ((JLabel) components[0]).setText("Total Flights: " + (flights != null ? flights.size() : 0));
        }
    }

    private void addFlight() {
        OwnerFlightDialog dialog = new OwnerFlightDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            null, 
            flightDAO,
            currentOwner
        );
        dialog.setVisible(true);
        
        if (dialog.isDataChanged()) {
            loadFlights();
        }
    }

    private void editFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a flight to edit.");
            return;
        }

        Flight selectedFlight = flights.get(selectedRow);
        OwnerFlightDialog dialog = new OwnerFlightDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            selectedFlight, 
            flightDAO,
            currentOwner
        );
        dialog.setVisible(true);
        
        if (dialog.isDataChanged()) {
            loadFlights();
        }
    }

    private void deleteFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a flight to delete.");
            return;
        }

        Flight selectedFlight = flights.get(selectedRow);
        
        // Create custom styled confirmation dialog
        boolean confirmed = showDeleteConfirmationDialog(selectedFlight);
        
        if (confirmed) {
            try {
                boolean success = flightDAO.delete(selectedFlight.getFlightId());
                if (success) {
                    showSuccess("Flight deleted successfully.");
                    loadFlights();
                } else {
                    showError("Failed to delete flight. It may have associated bookings.");
                }
            } catch (Exception e) {
                showError("Deletion failed: " + e.getMessage());
            }
        }
    }

    private void searchFlights() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            displayFlights(flights);
            return;
        }

        // Filter flights based on search term
        List<Flight> filteredFlights = flights.stream()
            .filter(flight ->
                flight.getFlightCode().toLowerCase().contains(searchTerm.toLowerCase()) ||
                (flight.getFlightName() != null && flight.getFlightName().toLowerCase().contains(searchTerm.toLowerCase())) ||
                flight.getRoute().toLowerCase().contains(searchTerm.toLowerCase())
            )
            .collect(java.util.stream.Collectors.toList());

        displayFlights(filteredFlights);
    }

    private void showError(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Flight Management Error", true);
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
        JLabel successIcon = new JLabel("✅");
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
        JTextField field = new JTextField(15);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 35));
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

        JLabel titleLabel = new JLabel("Flight Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Manage your airline's flight schedules and operations");
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

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.getViewport().setBackground(Color.WHITE);

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
        
        addFlightButton.setPreferredSize(new Dimension(130, 40));
        editFlightButton.setPreferredSize(new Dimension(130, 40));
        deleteFlightButton.setPreferredSize(new Dimension(140, 40));
        refreshButton.setPreferredSize(new Dimension(110, 40));
        
        buttonsPanel.add(addFlightButton);
        buttonsPanel.add(editFlightButton);
        buttonsPanel.add(deleteFlightButton);
        buttonsPanel.add(refreshButton);

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setBackground(BACKGROUND_GRAY);
        JLabel infoLabel = new JLabel("Total Flights: " + (flights != null ? flights.size() : 0));
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
     * Custom cell renderer for flight availability column
     */
    private static class AvailabilityCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                int available = Integer.parseInt(value.toString());
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 12));

                if (!isSelected) {
                    if (available > 20) {
                        setBackground(new Color(232, 245, 233));
                        setForeground(new Color(27, 94, 32));
                    } else if (available > 5) {
                        setBackground(new Color(255, 248, 225));
                        setForeground(new Color(230, 81, 0));
                    } else {
                        setBackground(new Color(255, 235, 238));
                        setForeground(new Color(183, 28, 28));
                    }
                }
            }

            return this;
        }
    }

    /**
     * Creates a modern styled delete confirmation dialog for flight owners
     */
    private boolean showDeleteConfirmationDialog(Flight flight) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Confirm Flight Deletion", true);
        dialog.setUndecorated(true);
        final boolean[] confirmed = {false};

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
                // background with warning gradient
                GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(255, 248, 240));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 12, arc, arc);
                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(20, 25, 18, 25));

        // Warning header with airline context
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        header.setOpaque(false);
        JLabel warningIcon = new JLabel("✈⚠");
        warningIcon.setFont(new Font("Arial", Font.BOLD, 24));
        warningIcon.setForeground(DANGER_RED);
        header.add(warningIcon);
        JLabel title = new JLabel("Delete Your Flight");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(DARK_BLUE);
        header.add(title);

        content.add(header, BorderLayout.NORTH);

        // Flight details and airline owner context section
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setOpaque(false);
        messagePanel.setBorder(new EmptyBorder(10, 0, 15, 0));

        // Flight and airline identification
        JPanel flightInfoPanel = new JPanel(new BorderLayout());
        flightInfoPanel.setOpaque(false);
        flightInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        flightInfoPanel.setBackground(new Color(248, 250, 255));
        flightInfoPanel.setOpaque(true);
        
        JLabel flightCodeLabel = new JLabel(flight.getFlightCode());
        flightCodeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        flightCodeLabel.setForeground(PRIMARY_BLUE);
        
        JLabel routeLabel = new JLabel(flight.getRoute() != null ? flight.getRoute() : "Route TBD");
        routeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        routeLabel.setForeground(DARK_BLUE);
        
        JLabel scheduleLabel = new JLabel(flight.getDepartureTime() != null ? 
            "Departure: " + flight.getDepartureTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) : 
            "Departure: TBD");
        scheduleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        scheduleLabel.setForeground(new Color(120, 120, 120));

        JLabel airlineLabel = new JLabel("🏢 " + currentOwner.getCompanyName());
        airlineLabel.setFont(new Font("Arial", Font.BOLD, 13));
        airlineLabel.setForeground(ACCENT_ORANGE);

        flightInfoPanel.add(flightCodeLabel, BorderLayout.NORTH);
        flightInfoPanel.add(routeLabel, BorderLayout.CENTER);
        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.add(scheduleLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(airlineLabel);
        flightInfoPanel.add(detailsPanel, BorderLayout.SOUTH);

        JLabel mainMessage = new JLabel("Are you sure you want to delete this flight from your schedule?");
        mainMessage.setFont(new Font("Arial", Font.BOLD, 15));
        mainMessage.setForeground(DARK_BLUE);
        mainMessage.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel warningMessage = new JLabel("This action cannot be undone and will permanently:");
        warningMessage.setFont(new Font("Arial", Font.PLAIN, 13));
        warningMessage.setForeground(new Color(120, 120, 120));
        warningMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
        warningMessage.setBorder(new EmptyBorder(8, 0, 5, 0));

        JLabel consequence1 = new JLabel("• Remove flight from your airline's schedule");
        consequence1.setFont(new Font("Arial", Font.PLAIN, 13));
        consequence1.setForeground(new Color(120, 120, 120));
        consequence1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel consequence2 = new JLabel("• Cancel all existing passenger bookings");
        consequence2.setFont(new Font("Arial", Font.PLAIN, 13));
        consequence2.setForeground(DANGER_RED);
        consequence2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel consequence3 = new JLabel("• Notify affected passengers automatically");
        consequence3.setFont(new Font("Arial", Font.PLAIN, 13));
        consequence3.setForeground(new Color(120, 120, 120));
        consequence3.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel consequence4 = new JLabel("• Update your airline's operational statistics");
        consequence4.setFont(new Font("Arial", Font.PLAIN, 13));
        consequence4.setForeground(new Color(120, 120, 120));
        consequence4.setAlignmentX(Component.LEFT_ALIGNMENT);

        messagePanel.add(flightInfoPanel);
        messagePanel.add(Box.createVerticalStrut(15));
        messagePanel.add(mainMessage);
        messagePanel.add(warningMessage);
        messagePanel.add(consequence1);
        messagePanel.add(consequence2);
        messagePanel.add(consequence3);
        messagePanel.add(consequence4);

        content.add(messagePanel, BorderLayout.CENTER);

        // Button panel with owner-specific styling
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        
        JButton deleteBtn = createStyledButton("🗑 Delete Flight", DANGER_RED, Color.WHITE, 14);
        deleteBtn.setPreferredSize(new Dimension(140, 40));
        deleteBtn.addActionListener(e -> {
            confirmed[0] = true;
            dialog.dispose();
        });
        
        JButton cancelBtn = createStyledButton("❌ Keep Flight", SUCCESS_GREEN, Color.WHITE, 14);
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(deleteBtn);
        btnPanel.add(cancelBtn);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(Math.max(480, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        return confirmed[0];
    }
}
