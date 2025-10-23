package com.GoAero.ui;

import com.GoAero.dao.AirportDAO;
import com.GoAero.dao.FlightDAO;
import com.GoAero.model.Airport;
import com.GoAero.model.Flight;
import com.GoAero.model.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Flight search interface for passengers with modern UI design
 */
public class SearchFlights extends JFrame {
    // Professional color scheme (consistent with other pages)
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private static final Color ACCENT_ORANGE = new Color(255, 152, 0);
    private static final Color DARK_BLUE = new Color(13, 71, 161);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color HOVER_BLUE = new Color(30, 136, 229);
    private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    private static final Color BACKGROUND_GRAY = new Color(250, 250, 250);
    private static final Color CARD_WHITE = Color.WHITE;
    private JComboBox<Airport> departureComboBox, destinationComboBox;
    private JTextField departureDateField;
    private JButton searchButton, backButton, bookButton;
    private JTable flightsTable;
    private DefaultTableModel tableModel;
    
    private AirportDAO airportDAO;
    private FlightDAO flightDAO;
    private List<Flight> searchResults;

    public SearchFlights() {
        airportDAO = new AirportDAO();
        flightDAO = new FlightDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadAirports();
    }

    private void initializeComponents() {
        setTitle("GoAero - Search Flights");
        setSize(1650, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Modern styled combo boxes
        departureComboBox = new JComboBox<>();
        departureComboBox.setPreferredSize(new Dimension(220, 40));
        departureComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        departureComboBox.setBackground(CARD_WHITE);
        departureComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        destinationComboBox = new JComboBox<>();
        destinationComboBox.setPreferredSize(new Dimension(220, 40));
        destinationComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        destinationComboBox.setBackground(CARD_WHITE);
        destinationComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Modern styled date field
        departureDateField = new JTextField(12);
        departureDateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        departureDateField.setPreferredSize(new Dimension(150, 40));
        departureDateField.setFont(new Font("Arial", Font.PLAIN, 14));
        departureDateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Modern styled buttons
        searchButton = createStyledButton("🔍 Search Flights", PRIMARY_BLUE, Color.WHITE, 16);
        searchButton.setPreferredSize(new Dimension(160, 45));

        backButton = createStyledButton("← Back", new Color(108, 117, 125), Color.WHITE, 14);
        backButton.setPreferredSize(new Dimension(100, 40));

        bookButton = createStyledButton("✈ Book Flight", SUCCESS_GREEN, Color.WHITE, 16);
        bookButton.setPreferredSize(new Dimension(150, 45));
        bookButton.setEnabled(false);

        // Modern table setup
        String[] columnNames = {"Flight Code", "Airline", "Route", "Departure", "Arrival", "Price (₹)", "Available Seats"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        flightsTable = new JTable(tableModel);
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        flightsTable.setRowHeight(35);
        flightsTable.setGridColor(LIGHT_GRAY);
        flightsTable.setSelectionBackground(new Color(230, 240, 255));
        flightsTable.setSelectionForeground(DARK_BLUE);
        flightsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        flightsTable.getTableHeader().setBackground(DARK_BLUE);
        flightsTable.getTableHeader().setForeground(Color.WHITE);
        flightsTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        flightsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                bookButton.setEnabled(flightsTable.getSelectedRow() != -1);
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GRAY);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_GRAY);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Header section
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Search form section
        JPanel searchFormPanel = createSearchFormPanel();
        mainPanel.add(searchFormPanel, BorderLayout.CENTER);

        // Results section
        JPanel resultsPanel = createResultsPanel();
        mainPanel.add(resultsPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Title
        JLabel titleLabel = new JLabel("Search Flights");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Find the perfect flight for your journey");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Back button in top right
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backPanel.setBackground(BACKGROUND_GRAY);
        backPanel.add(backButton);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(backPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSearchFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BorderLayout());
        formPanel.setBackground(CARD_WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(25, 30, 25, 30)
        ));

        // Form title
        JLabel formTitle = new JLabel("Flight Search");
        formTitle.setFont(new Font("Arial", Font.BOLD, 18));
        formTitle.setForeground(DARK_BLUE);
        formTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Form fields
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(CARD_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // From field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Arial", Font.BOLD, 14));
        fromLabel.setForeground(DARK_BLUE);
        fieldsPanel.add(fromLabel, gbc);
        gbc.gridy = 1;
        fieldsPanel.add(departureComboBox, gbc);

        // To field
        gbc.gridx = 1; gbc.gridy = 0;
        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Arial", Font.BOLD, 14));
        toLabel.setForeground(DARK_BLUE);
        fieldsPanel.add(toLabel, gbc);
        gbc.gridy = 1;
        fieldsPanel.add(destinationComboBox, gbc);

        // Date field
        gbc.gridx = 2; gbc.gridy = 0;
        JLabel dateLabel = new JLabel("Departure Date:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateLabel.setForeground(DARK_BLUE);
        fieldsPanel.add(dateLabel, gbc);
        gbc.gridy = 1;
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        datePanel.setBackground(CARD_WHITE);
        datePanel.add(departureDateField);
        JLabel dateHint = new JLabel(" (YYYY-MM-DD)");
        dateHint.setFont(new Font("Arial", Font.ITALIC, 12));
        dateHint.setForeground(Color.GRAY);
        datePanel.add(dateHint);
        fieldsPanel.add(datePanel, gbc);

        // Search button
        gbc.gridx = 3; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        fieldsPanel.add(searchButton, gbc);

        formPanel.add(formTitle, BorderLayout.NORTH);
        formPanel.add(fieldsPanel, BorderLayout.CENTER);

        return formPanel;
    }

    private JPanel createResultsPanel() {
        JPanel resultsContainer = new JPanel(new BorderLayout());
        resultsContainer.setBackground(BACKGROUND_GRAY);
        resultsContainer.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(CARD_WHITE);
        resultsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Results title
        JLabel resultsTitle = new JLabel("Search Results");
        resultsTitle.setFont(new Font("Arial", Font.BOLD, 18));
        resultsTitle.setForeground(DARK_BLUE);
        resultsTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        scrollPane.setPreferredSize(new Dimension(0, 300));

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionPanel.setBackground(CARD_WHITE);
        actionPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        actionPanel.add(bookButton);

        resultsPanel.add(resultsTitle, BorderLayout.NORTH);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        resultsPanel.add(actionPanel, BorderLayout.SOUTH);

        resultsContainer.add(resultsPanel, BorderLayout.CENTER);
        return resultsContainer;
    }

    private void setupEventListeners() {
        searchButton.addActionListener(e -> performSearch());
        bookButton.addActionListener(e -> bookSelectedFlight());
        backButton.addActionListener(e -> goBackToDashboard());
        
        // Enter key on date field
        departureDateField.addActionListener(e -> performSearch());
    }

    private void loadAirports() {
        try {
            List<Airport> airports = airportDAO.findAll();
            
            // Add default option
            departureComboBox.addItem(null);
            destinationComboBox.addItem(null);
            
            for (Airport airport : airports) {
                departureComboBox.addItem(airport);
                destinationComboBox.addItem(airport);
            }
            
            // Custom renderer to show airport display name
            departureComboBox.setRenderer(new AirportComboBoxRenderer());
            destinationComboBox.setRenderer(new AirportComboBoxRenderer());
            
        } catch (Exception e) {
            System.out.println("Failed to load airports: " + e.getMessage());
        }
    }

    private void performSearch() {
        if (!validateSearchInput()) {
            return;
        }

        try {
            Airport departure = (Airport) departureComboBox.getSelectedItem();
            Airport destination = (Airport) destinationComboBox.getSelectedItem();
            LocalDate departureDate = LocalDate.parse(departureDateField.getText().trim());

            searchResults = flightDAO.searchFlights(departure.getAirportId(), destination.getAirportId(), departureDate);
            
            // Update available seats for each flight
            for (Flight flight : searchResults) {
                int availableSeats = flightDAO.getAvailableSeats(flight.getFlightId());
                flight.setAvailableSeats(availableSeats);
            }
            
            displaySearchResults();
            
        } catch (Exception e) {
            showError("Search failed: " + e.getMessage());
        }
    }

    private boolean validateSearchInput() {
        if (departureComboBox.getSelectedItem() == null) {
            showError("Please select a departure airport.");
            return false;
        }

        if (destinationComboBox.getSelectedItem() == null) {
            showError("Please select a destination airport.");
            return false;
        }

        if (departureComboBox.getSelectedItem().equals(destinationComboBox.getSelectedItem())) {
            showError("Departure and destination airports cannot be the same.");
            return false;
        }

        try {
            LocalDate departureDate = LocalDate.parse(departureDateField.getText().trim());
            if (departureDate.isBefore(LocalDate.now())) {
                showError("Departure date cannot be in the past.");
                return false;
            }
        } catch (DateTimeParseException e) {
            showError("Please enter departure date in YYYY-MM-DD format.");
            return false;
        }

        return true;
    }

    private void displaySearchResults() {
        // Clear existing data
        tableModel.setRowCount(0);

        if (searchResults.isEmpty()) {
            showInfo("No flights found for the selected criteria.");
            return;
        }

        // Add flights to table
        for (Flight flight : searchResults) {
            Object[] row = {
                flight.getFlightCode(),
                flight.getCompanyName(),
                flight.getRoute(),
                flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                flight.getDestinationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                "₹" + flight.getPrice(),
                flight.getAvailableSeats()
            };
            tableModel.addRow(row);
        }
    }

    private void bookSelectedFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a flight to book.");
            return;
        }

        if (!SessionManager.getInstance().isUserLoggedIn()) {
            showError("Please login to book a flight.");
            return;
        }

        Flight selectedFlight = searchResults.get(selectedRow);
        
        if (selectedFlight.getAvailableSeats() <= 0) {
            showError("This flight is fully booked.");
            return;
        }

        // Open booking dialog
        SwingUtilities.invokeLater(() -> {
            new FlightBookingDialog(this, selectedFlight).setVisible(true);
        });
    }

    private void goBackToDashboard() {
        if (SessionManager.getInstance().isUserLoggedIn()) {
            SwingUtilities.invokeLater(() -> {
                new UserDashboard().setVisible(true);
                dispose();
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                new LandingPage().setVisible(true);
                dispose();
            });
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
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
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
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

    // Custom renderer for airport combo box
    private static class AirportComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value == null) {
                setText("Select Airport");
            } else if (value instanceof Airport) {
                Airport airport = (Airport) value;
                setText(airport.getDisplayName());
            }
            
            return this;
        }
    }
}