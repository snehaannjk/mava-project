<<<<<<< HEAD
package com.GoAero.ui;

import com.GoAero.dao.AirportDAO;
import com.GoAero.dao.FlightDAO;
import com.GoAero.model.Airport;
import com.GoAero.model.Flight;
import com.GoAero.model.FlightOwner;
import com.GoAero.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog for flight owners to add or edit their own flights with modern UI design
 */
public class OwnerFlightDialog extends JDialog {
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
    private Flight flight;
    private FlightDAO flightDAO;
    private AirportDAO airportDAO;
    private FlightOwner currentOwner;
    private boolean isEditMode;
    private boolean dataChanged = false;
    
    private JTextField flightCodeField, flightNameField, capacityField, priceField;
    private JTextField departureTimeField, destinationTimeField;
    private JComboBox<Airport> departureAirportComboBox, destinationAirportComboBox;
    private JButton saveButton, cancelButton;

    public OwnerFlightDialog(Frame parent, Flight flight, FlightDAO flightDAO, FlightOwner currentOwner) {
        super(parent, flight == null ? "Add Flight" : "Edit Flight", true);
        this.flight = flight;
        this.flightDAO = flightDAO;
        this.airportDAO = new AirportDAO();
        this.currentOwner = currentOwner;
        this.isEditMode = (flight != null);
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadComboBoxData();
        
        if (isEditMode) {
            loadFlightData();
        }
    }

    private void initializeComponents() {
        setSize(750, 700);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Create styled form fields with placeholders
        flightCodeField = createStyledTextField("Enter flight code (e.g., GA123)");
        flightNameField = createStyledTextField("Enter flight name or route description");
        capacityField = createStyledTextField("Enter passenger capacity");
        priceField = createStyledTextField("Enter ticket price in INR");
        departureTimeField = createStyledTextField("YYYY-MM-DD HH:MM");
        destinationTimeField = createStyledTextField("YYYY-MM-DD HH:MM");
        
        // Create styled combo boxes
        departureAirportComboBox = createStyledComboBox();
        destinationAirportComboBox = createStyledComboBox();
        
        // Create modern styled buttons
        String buttonText = isEditMode ? "✈ Update Flight" : "✈ Create Flight";
        saveButton = createStyledButton(buttonText, PRIMARY_BLUE, Color.WHITE, 14);
        cancelButton = createStyledButton("❌ Cancel", LIGHT_GRAY, DARK_BLUE, 14);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GRAY);

        // Main content panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_GRAY);
        mainPanel.setBorder(new EmptyBorder(25, 30, 30, 30));

        // Header section
        JPanel headerSection = createHeaderSection();
        mainPanel.add(headerSection, BorderLayout.NORTH);

        // Content section with enhanced form
        JPanel contentSection = createContentSection();
        mainPanel.add(contentSection, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonSection();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        saveButton.addActionListener(e -> saveFlight());
        cancelButton.addActionListener(e -> dispose());
        
        // Auto-format flight code as user types
        flightCodeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String text = flightCodeField.getText().toUpperCase();
                if (!text.equals(flightCodeField.getText())) {
                    flightCodeField.setText(text);
                }
            }
        });
    }

    private void loadComboBoxData() {
        try {
            // Load airports
            List<Airport> airports = airportDAO.findAll();
            for (Airport airport : airports) {
                departureAirportComboBox.addItem(airport);
                destinationAirportComboBox.addItem(airport);
            }
        } catch (Exception e) {
            System.out.println("Failed to load airports: " + e.getMessage());
        }
    }

    private void loadFlightData() {
        flightCodeField.setText(flight.getFlightCode());
        flightNameField.setText(flight.getFlightName());
        capacityField.setText(String.valueOf(flight.getCapacity()));
        priceField.setText(flight.getPrice().toString());
        
        departureTimeField.setText(flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        destinationTimeField.setText(flight.getDestinationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        // Set selected items in combo boxes
        for (int i = 0; i < departureAirportComboBox.getItemCount(); i++) {
            Airport airport = departureAirportComboBox.getItemAt(i);
            if (airport.getAirportId() == flight.getDepartureAirportId()) {
                departureAirportComboBox.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < destinationAirportComboBox.getItemCount(); i++) {
            Airport airport = destinationAirportComboBox.getItemAt(i);
            if (airport.getAirportId() == flight.getDestinationAirportId()) {
                destinationAirportComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void saveFlight() {
        if (!validateInput()) {
            return;
        }

        try {
            if (isEditMode) {
                updateFlight();
            } else {
                createFlight();
            }
        } catch (Exception e) {
            showError("Save failed: " + e.getMessage());
        }
    }

    private void createFlight() {
        Flight newFlight = new Flight();
        populateFlightFromForm(newFlight);

        Flight savedFlight = flightDAO.create(newFlight);
        if (savedFlight != null) {
            showSuccess("Flight created successfully!");
            dataChanged = true;
            dispose();
        } else {
            showError("Failed to create flight. Please try again.");
        }
    }

    private void updateFlight() {
        populateFlightFromForm(flight);

        boolean success = flightDAO.update(flight);
        if (success) {
            showSuccess("Flight updated successfully!");
            dataChanged = true;
            dispose();
        } else {
            showError("Failed to update flight. Please try again.");
        }
    }

    private void populateFlightFromForm(Flight flight) {
        flight.setFlightCode(ValidationUtil.formatFlightCode(flightCodeField.getText().trim()));
        flight.setFlightName(flightNameField.getText().trim());
        flight.setCompanyId(currentOwner.getOwnerId()); // Use current owner's ID
        flight.setDepartureAirportId(((Airport) departureAirportComboBox.getSelectedItem()).getAirportId());
        flight.setDestinationAirportId(((Airport) destinationAirportComboBox.getSelectedItem()).getAirportId());
        flight.setDepartureTime(LocalDateTime.parse(departureTimeField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        flight.setDestinationTime(LocalDateTime.parse(destinationTimeField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        flight.setCapacity(Integer.parseInt(capacityField.getText().trim()));
        flight.setPrice(new BigDecimal(priceField.getText().trim()));
    }

    private boolean validateInput() {
        // Flight Code validation
        String code = flightCodeField.getText().trim();
        if (!ValidationUtil.isValidFlightCode(code)) {
            showError(ValidationUtil.getFlightCodeErrorMessage());
            flightCodeField.requestFocus();
            return false;
        }

        // Check if flight code already exists for this owner
        try {
            List<Flight> ownerFlights = flightDAO.findByCompanyId(currentOwner.getOwnerId());
            for (Flight existingFlight : ownerFlights) {
                if (existingFlight.getFlightCode().equalsIgnoreCase(code) && 
                    (!isEditMode || existingFlight.getFlightId() != flight.getFlightId())) {
                    showError("You already have a flight with this code.");
                    flightCodeField.requestFocus();
                    return false;
                }
            }
        } catch (Exception e) {
            // Continue with validation
        }

        // Flight Name validation
        if (!ValidationUtil.isNotEmpty(flightNameField.getText())) {
            showError("Flight name is required.");
            flightNameField.requestFocus();
            return false;
        }

        // Airport validation
        if (departureAirportComboBox.getSelectedItem() == null) {
            showError("Please select a departure airport.");
            departureAirportComboBox.requestFocus();
            return false;
        }

        if (destinationAirportComboBox.getSelectedItem() == null) {
            showError("Please select a destination airport.");
            destinationAirportComboBox.requestFocus();
            return false;
        }

        if (departureAirportComboBox.getSelectedItem() == destinationAirportComboBox.getSelectedItem()) {
            showError("Departure and destination airports cannot be the same.");
            destinationAirportComboBox.requestFocus();
            return false;
        }

        // Time validation
        try {
            LocalDateTime depTime = LocalDateTime.parse(departureTimeField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime destTime = LocalDateTime.parse(destinationTimeField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            if (depTime.isAfter(destTime)) {
                showError("Departure time cannot be after arrival time.");
                destinationTimeField.requestFocus();
                return false;
            }
            
            if (depTime.isBefore(LocalDateTime.now())) {
                showError("Departure time cannot be in the past.");
                departureTimeField.requestFocus();
                return false;
            }
        } catch (DateTimeParseException e) {
            showError("Please enter valid date and time in YYYY-MM-DD HH:MM format.");
            return false;
        }

        // Capacity validation
        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());
            if (capacity <= 0) {
                showError("Capacity must be a positive number.");
                capacityField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid capacity number.");
            capacityField.requestFocus();
            return false;
        }

        // Price validation
        try {
            BigDecimal price = new BigDecimal(priceField.getText().trim());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Price must be greater than 0.");
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid price.");
            priceField.requestFocus();
            return false;
        }

        return true;
    }

    public boolean isDataChanged() {
        return dataChanged;
    }

    private void showError(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog(this, "Flight Management Error", true);
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
        JLabel errorIcon = new JLabel("✈⚠");
        errorIcon.setFont(new Font("Arial", Font.BOLD, 24));
        errorIcon.setForeground(DANGER_RED);
        header.add(errorIcon);
        JLabel title = new JLabel("Validation Error");
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
        JButton ok = createStyledButton("Understood", DANGER_RED, Color.WHITE, 14);
        ok.setPreferredSize(new Dimension(110, 36));
        ok.addActionListener(e -> dialog.dispose());
        btnPanel.add(ok);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(Math.max(400, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showSuccess(String message) {
        // Create a custom styled success dialog
        JDialog dialog = new JDialog(this, "Flight Operation Complete", true);
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
                // background with subtle gradient
                GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(248, 255, 248));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 12, arc, arc);
                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(18, 20, 16, 20));

        // Success header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        header.setOpaque(false);
        JLabel successIcon = new JLabel("✈✅");
        successIcon.setFont(new Font("Arial", Font.BOLD, 22));
        header.add(successIcon);
        JLabel title = new JLabel("Operation Successful");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(DARK_BLUE);
        header.add(title);

        content.add(header, BorderLayout.NORTH);

        // Message
        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        msgLabel.setBorder(new EmptyBorder(8, 6, 12, 6));
        content.add(msgLabel, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        JButton ok = createStyledButton("Excellent!", SUCCESS_GREEN, Color.WHITE, 14);
        ok.setPreferredSize(new Dimension(110, 36));
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
        } else if (originalColor.equals(LIGHT_GRAY)) {
            return new Color(224, 224, 224);
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
        JTextField field = new JTextField(25);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(320, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
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
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
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
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }

    /**
     * Creates a styled combo box with modern design
     */
    private JComboBox<Airport> createStyledComboBox() {
        JComboBox<Airport> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(320, 40));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(2, 8, 2, 2)
        ));
        comboBox.setBackground(Color.WHITE);
        
        // Custom renderer for airport display
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value instanceof Airport) {
                    Airport airport = (Airport) value;
                    setText(airport.getAirportCode() + " - " + airport.getAirportName() + 
                           " (" + airport.getCity() + ", " + airport.getCountry() + ")");
                }
                
                if (isSelected) {
                    setBackground(PRIMARY_BLUE);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                
                return this;
            }
        });
        
        return comboBox;
    }

    private JPanel createHeaderSection() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        // Title section with flight icon
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_GRAY);

        JLabel titleLabel = new JLabel("✈ " + (isEditMode ? "Edit Flight Schedule" : "Create New Flight"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(isEditMode ? 
            "Update flight information and schedule details" : 
            "Schedule a new flight for your airline");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);

        // Company info panel
        JPanel companyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        companyPanel.setBackground(BACKGROUND_GRAY);
        
        JLabel companyLabel = new JLabel("🏢 " + currentOwner.getCompanyName());
        companyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        companyLabel.setForeground(ACCENT_ORANGE);
        companyLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_ORANGE, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        companyLabel.setBackground(new Color(255, 248, 225));
        companyLabel.setOpaque(true);
        companyPanel.add(companyLabel);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(companyPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createContentSection() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CARD_WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        // Form panel with enhanced layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Flight Details Section
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel flightDetailsLabel = new JLabel("✈ Flight Details");
        flightDetailsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        flightDetailsLabel.setForeground(DARK_BLUE);
        flightDetailsLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        formPanel.add(flightDetailsLabel, gbc);

        // Flight Code
        gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel codeLabel = new JLabel("Flight Code");
        codeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        codeLabel.setForeground(DARK_BLUE);
        formPanel.add(codeLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(flightCodeField, gbc);

        // Flight Name
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel nameLabel = new JLabel("Flight Name");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(DARK_BLUE);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(flightNameField, gbc);

        // Route Information Section
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel routeLabel = new JLabel("🗺 Route Information");
        routeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        routeLabel.setForeground(DARK_BLUE);
        routeLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        formPanel.add(routeLabel, gbc);

        // Departure Airport
        gbc.gridy = 4; gbc.gridwidth = 1;
        JLabel depAirportLabel = new JLabel("Departure Airport");
        depAirportLabel.setFont(new Font("Arial", Font.BOLD, 14));
        depAirportLabel.setForeground(DARK_BLUE);
        formPanel.add(depAirportLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(departureAirportComboBox, gbc);

        // Destination Airport
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel destAirportLabel = new JLabel("Destination Airport");
        destAirportLabel.setFont(new Font("Arial", Font.BOLD, 14));
        destAirportLabel.setForeground(DARK_BLUE);
        formPanel.add(destAirportLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(destinationAirportComboBox, gbc);

        // Schedule Section
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JLabel scheduleLabel = new JLabel("⏰ Schedule");
        scheduleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scheduleLabel.setForeground(DARK_BLUE);
        scheduleLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        formPanel.add(scheduleLabel, gbc);

        // Departure Time
        gbc.gridy = 7; gbc.gridwidth = 1;
        JLabel depTimeLabel = new JLabel("Departure Time");
        depTimeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        depTimeLabel.setForeground(DARK_BLUE);
        formPanel.add(depTimeLabel, gbc);

        gbc.gridx = 1;
        JPanel depTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        depTimePanel.setBackground(CARD_WHITE);
        depTimePanel.add(departureTimeField);
        JLabel depTimeHint = new JLabel("  (YYYY-MM-DD HH:MM)");
        depTimeHint.setFont(new Font("Arial", Font.ITALIC, 12));
        depTimeHint.setForeground(new Color(120, 120, 120));
        depTimePanel.add(depTimeHint);
        formPanel.add(depTimePanel, gbc);

        // Arrival Time
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel arrTimeLabel = new JLabel("Arrival Time");
        arrTimeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        arrTimeLabel.setForeground(DARK_BLUE);
        formPanel.add(arrTimeLabel, gbc);

        gbc.gridx = 1;
        JPanel arrTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        arrTimePanel.setBackground(CARD_WHITE);
        arrTimePanel.add(destinationTimeField);
        JLabel arrTimeHint = new JLabel("  (YYYY-MM-DD HH:MM)");
        arrTimeHint.setFont(new Font("Arial", Font.ITALIC, 12));
        arrTimeHint.setForeground(new Color(120, 120, 120));
        arrTimePanel.add(arrTimeHint);
        formPanel.add(arrTimePanel, gbc);

        // Pricing & Capacity Section
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        JLabel pricingLabel = new JLabel("💰 Pricing & Capacity");
        pricingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pricingLabel.setForeground(DARK_BLUE);
        pricingLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        formPanel.add(pricingLabel, gbc);

        // Capacity
        gbc.gridy = 10; gbc.gridwidth = 1;
        JLabel capacityLabel = new JLabel("Passenger Capacity");
        capacityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        capacityLabel.setForeground(DARK_BLUE);
        formPanel.add(capacityLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(capacityField, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 11;
        JLabel priceLabel = new JLabel("Ticket Price (INR)");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(DARK_BLUE);
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(priceField, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        return contentPanel;
    }

    private JPanel createButtonSection() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_GRAY);
        buttonPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        // Enhanced button sizing
        saveButton.setPreferredSize(new Dimension(160, 45));
        cancelButton.setPreferredSize(new Dimension(120, 45));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }
}
=======
package com.GoAero.ui;

import com.GoAero.dao.AirportDAO;
import com.GoAero.dao.FlightDAO;
import com.GoAero.model.Airport;
import com.GoAero.model.Flight;
import com.GoAero.model.FlightOwner;
import com.GoAero.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog for flight owners to add or edit their own flights with modern UI design
 */
public class OwnerFlightDialog extends JDialog {
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
    private Flight flight;
    private FlightDAO flightDAO;
    private AirportDAO airportDAO;
    private FlightOwner currentOwner;
    private boolean isEditMode;
    private boolean dataChanged = false;
    
    private JTextField flightCodeField, flightNameField, capacityField, priceField;
    private JTextField departureTimeField, destinationTimeField;
    private JComboBox<Airport> departureAirportComboBox, destinationAirportComboBox;
    private JButton saveButton, cancelButton;

    public OwnerFlightDialog(Frame parent, Flight flight, FlightDAO flightDAO, FlightOwner currentOwner) {
        super(parent, flight == null ? "Add Flight" : "Edit Flight", true);
        this.flight = flight;
        this.flightDAO = flightDAO;
        this.airportDAO = new AirportDAO();
        this.currentOwner = currentOwner;
        this.isEditMode = (flight != null);
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadComboBoxData();
        
        if (isEditMode) {
            loadFlightData();
        }
    }

    private void initializeComponents() {
        setSize(750, 700);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Create styled form fields with placeholders
        flightCodeField = createStyledTextField("Enter flight code (e.g., GA123)");
        flightNameField = createStyledTextField("Enter flight name or route description");
        capacityField = createStyledTextField("Enter passenger capacity");
        priceField = createStyledTextField("Enter ticket price in INR");
        departureTimeField = createStyledTextField("YYYY-MM-DD HH:MM");
        destinationTimeField = createStyledTextField("YYYY-MM-DD HH:MM");
        
        // Create styled combo boxes
        departureAirportComboBox = createStyledComboBox();
        destinationAirportComboBox = createStyledComboBox();
        
        // Create modern styled buttons
        String buttonText = isEditMode ? "✈ Update Flight" : "✈ Create Flight";
        saveButton = createStyledButton(buttonText, PRIMARY_BLUE, Color.WHITE, 14);
        cancelButton = createStyledButton("❌ Cancel", LIGHT_GRAY, DARK_BLUE, 14);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GRAY);

        // Main content panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_GRAY);
        mainPanel.setBorder(new EmptyBorder(25, 30, 30, 30));

        // Header section
        JPanel headerSection = createHeaderSection();
        mainPanel.add(headerSection, BorderLayout.NORTH);

        // Content section with enhanced form
        JPanel contentSection = createContentSection();
        mainPanel.add(contentSection, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonSection();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        saveButton.addActionListener(e -> saveFlight());
        cancelButton.addActionListener(e -> dispose());
        
        // Auto-format flight code as user types
        flightCodeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String text = flightCodeField.getText().toUpperCase();
                if (!text.equals(flightCodeField.getText())) {
                    flightCodeField.setText(text);
                }
            }
        });
    }

    private void loadComboBoxData() {
        try {
            // Load airports
            List<Airport> airports = airportDAO.findAll();
            for (Airport airport : airports) {
                departureAirportComboBox.addItem(airport);
                destinationAirportComboBox.addItem(airport);
            }
        } catch (Exception e) {
            System.out.println("Failed to load airports: " + e.getMessage());
        }
    }

    private void loadFlightData() {
        flightCodeField.setText(flight.getFlightCode());
        flightNameField.setText(flight.getFlightName());
        capacityField.setText(String.valueOf(flight.getCapacity()));
        priceField.setText(flight.getPrice().toString());
        
        departureTimeField.setText(flight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        destinationTimeField.setText(flight.getDestinationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        // Set selected items in combo boxes
        for (int i = 0; i < departureAirportComboBox.getItemCount(); i++) {
            Airport airport = departureAirportComboBox.getItemAt(i);
            if (airport.getAirportId() == flight.getDepartureAirportId()) {
                departureAirportComboBox.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < destinationAirportComboBox.getItemCount(); i++) {
            Airport airport = destinationAirportComboBox.getItemAt(i);
            if (airport.getAirportId() == flight.getDestinationAirportId()) {
                destinationAirportComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void saveFlight() {
        if (!validateInput()) {
            return;
        }

        try {
            if (isEditMode) {
                updateFlight();
            } else {
                createFlight();
            }
        } catch (Exception e) {
            showError("Save failed: " + e.getMessage());
        }
    }

    private void createFlight() {
        Flight newFlight = new Flight();
        populateFlightFromForm(newFlight);

        Flight savedFlight = flightDAO.create(newFlight);
        if (savedFlight != null) {
            showSuccess("Flight created successfully!");
            dataChanged = true;
            dispose();
        } else {
            showError("Failed to create flight. Please try again.");
        }
    }

    private void updateFlight() {
        populateFlightFromForm(flight);

        boolean success = flightDAO.update(flight);
        if (success) {
            showSuccess("Flight updated successfully!");
            dataChanged = true;
            dispose();
        } else {
            showError("Failed to update flight. Please try again.");
        }
    }

    private void populateFlightFromForm(Flight flight) {
        flight.setFlightCode(ValidationUtil.formatFlightCode(flightCodeField.getText().trim()));
        flight.setFlightName(flightNameField.getText().trim());
        flight.setCompanyId(currentOwner.getOwnerId()); // Use current owner's ID
        flight.setDepartureAirportId(((Airport) departureAirportComboBox.getSelectedItem()).getAirportId());
        flight.setDestinationAirportId(((Airport) destinationAirportComboBox.getSelectedItem()).getAirportId());
        flight.setDepartureTime(LocalDateTime.parse(departureTimeField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        flight.setDestinationTime(LocalDateTime.parse(destinationTimeField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        flight.setCapacity(Integer.parseInt(capacityField.getText().trim()));
        flight.setPrice(new BigDecimal(priceField.getText().trim()));
    }

    private boolean validateInput() {
        // Flight Code validation
        String code = flightCodeField.getText().trim();
        if (!ValidationUtil.isValidFlightCode(code)) {
            showError(ValidationUtil.getFlightCodeErrorMessage());
            flightCodeField.requestFocus();
            return false;
        }

        // Check if flight code already exists for this owner
        try {
            List<Flight> ownerFlights = flightDAO.findByCompanyId(currentOwner.getOwnerId());
            for (Flight existingFlight : ownerFlights) {
                if (existingFlight.getFlightCode().equalsIgnoreCase(code) && 
                    (!isEditMode || existingFlight.getFlightId() != flight.getFlightId())) {
                    showError("You already have a flight with this code.");
                    flightCodeField.requestFocus();
                    return false;
                }
            }
        } catch (Exception e) {
            // Continue with validation
        }

        // Flight Name validation
        if (!ValidationUtil.isNotEmpty(flightNameField.getText())) {
            showError("Flight name is required.");
            flightNameField.requestFocus();
            return false;
        }

        // Airport validation
        if (departureAirportComboBox.getSelectedItem() == null) {
            showError("Please select a departure airport.");
            departureAirportComboBox.requestFocus();
            return false;
        }

        if (destinationAirportComboBox.getSelectedItem() == null) {
            showError("Please select a destination airport.");
            destinationAirportComboBox.requestFocus();
            return false;
        }

        if (departureAirportComboBox.getSelectedItem() == destinationAirportComboBox.getSelectedItem()) {
            showError("Departure and destination airports cannot be the same.");
            destinationAirportComboBox.requestFocus();
            return false;
        }

        // Time validation
        try {
            LocalDateTime depTime = LocalDateTime.parse(departureTimeField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime destTime = LocalDateTime.parse(destinationTimeField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            if (depTime.isAfter(destTime)) {
                showError("Departure time cannot be after arrival time.");
                destinationTimeField.requestFocus();
                return false;
            }
            
            if (depTime.isBefore(LocalDateTime.now())) {
                showError("Departure time cannot be in the past.");
                departureTimeField.requestFocus();
                return false;
            }
        } catch (DateTimeParseException e) {
            showError("Please enter valid date and time in YYYY-MM-DD HH:MM format.");
            return false;
        }

        // Capacity validation
        try {
            int capacity = Integer.parseInt(capacityField.getText().trim());
            if (capacity <= 0) {
                showError("Capacity must be a positive number.");
                capacityField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid capacity number.");
            capacityField.requestFocus();
            return false;
        }

        // Price validation
        try {
            BigDecimal price = new BigDecimal(priceField.getText().trim());
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Price must be greater than 0.");
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid price.");
            priceField.requestFocus();
            return false;
        }

        return true;
    }

    public boolean isDataChanged() {
        return dataChanged;
    }

    private void showError(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog(this, "Flight Management Error", true);
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
        JLabel errorIcon = new JLabel("✈⚠");
        errorIcon.setFont(new Font("Arial", Font.BOLD, 24));
        errorIcon.setForeground(DANGER_RED);
        header.add(errorIcon);
        JLabel title = new JLabel("Validation Error");
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
        JButton ok = createStyledButton("Understood", DANGER_RED, Color.WHITE, 14);
        ok.setPreferredSize(new Dimension(110, 36));
        ok.addActionListener(e -> dialog.dispose());
        btnPanel.add(ok);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(Math.max(400, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showSuccess(String message) {
        // Create a custom styled success dialog
        JDialog dialog = new JDialog(this, "Flight Operation Complete", true);
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
                // background with subtle gradient
                GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(248, 255, 248));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 12, arc, arc);
                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(18, 20, 16, 20));

        // Success header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        header.setOpaque(false);
        JLabel successIcon = new JLabel("✈✅");
        successIcon.setFont(new Font("Arial", Font.BOLD, 22));
        header.add(successIcon);
        JLabel title = new JLabel("Operation Successful");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(DARK_BLUE);
        header.add(title);

        content.add(header, BorderLayout.NORTH);

        // Message
        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        msgLabel.setBorder(new EmptyBorder(8, 6, 12, 6));
        content.add(msgLabel, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        JButton ok = createStyledButton("Excellent!", SUCCESS_GREEN, Color.WHITE, 14);
        ok.setPreferredSize(new Dimension(110, 36));
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
        } else if (originalColor.equals(LIGHT_GRAY)) {
            return new Color(224, 224, 224);
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
        JTextField field = new JTextField(25);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(320, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
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
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
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
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        return field;
    }

    /**
     * Creates a styled combo box with modern design
     */
    private JComboBox<Airport> createStyledComboBox() {
        JComboBox<Airport> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(320, 40));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(2, 8, 2, 2)
        ));
        comboBox.setBackground(Color.WHITE);
        
        // Custom renderer for airport display
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value instanceof Airport) {
                    Airport airport = (Airport) value;
                    setText(airport.getAirportCode() + " - " + airport.getAirportName() + 
                           " (" + airport.getCity() + ", " + airport.getCountry() + ")");
                }
                
                if (isSelected) {
                    setBackground(PRIMARY_BLUE);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                
                return this;
            }
        });
        
        return comboBox;
    }

    private JPanel createHeaderSection() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        // Title section with flight icon
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_GRAY);

        JLabel titleLabel = new JLabel("✈ " + (isEditMode ? "Edit Flight Schedule" : "Create New Flight"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(isEditMode ? 
            "Update flight information and schedule details" : 
            "Schedule a new flight for your airline");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);

        // Company info panel
        JPanel companyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        companyPanel.setBackground(BACKGROUND_GRAY);
        
        JLabel companyLabel = new JLabel("🏢 " + currentOwner.getCompanyName());
        companyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        companyLabel.setForeground(ACCENT_ORANGE);
        companyLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_ORANGE, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        companyLabel.setBackground(new Color(255, 248, 225));
        companyLabel.setOpaque(true);
        companyPanel.add(companyLabel);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(companyPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createContentSection() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CARD_WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        // Form panel with enhanced layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Flight Details Section
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel flightDetailsLabel = new JLabel("✈ Flight Details");
        flightDetailsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        flightDetailsLabel.setForeground(DARK_BLUE);
        flightDetailsLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        formPanel.add(flightDetailsLabel, gbc);

        // Flight Code
        gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel codeLabel = new JLabel("Flight Code");
        codeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        codeLabel.setForeground(DARK_BLUE);
        formPanel.add(codeLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(flightCodeField, gbc);

        // Flight Name
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel nameLabel = new JLabel("Flight Name");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(DARK_BLUE);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(flightNameField, gbc);

        // Route Information Section
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel routeLabel = new JLabel("🗺 Route Information");
        routeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        routeLabel.setForeground(DARK_BLUE);
        routeLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        formPanel.add(routeLabel, gbc);

        // Departure Airport
        gbc.gridy = 4; gbc.gridwidth = 1;
        JLabel depAirportLabel = new JLabel("Departure Airport");
        depAirportLabel.setFont(new Font("Arial", Font.BOLD, 14));
        depAirportLabel.setForeground(DARK_BLUE);
        formPanel.add(depAirportLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(departureAirportComboBox, gbc);

        // Destination Airport
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel destAirportLabel = new JLabel("Destination Airport");
        destAirportLabel.setFont(new Font("Arial", Font.BOLD, 14));
        destAirportLabel.setForeground(DARK_BLUE);
        formPanel.add(destAirportLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(destinationAirportComboBox, gbc);

        // Schedule Section
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JLabel scheduleLabel = new JLabel("⏰ Schedule");
        scheduleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scheduleLabel.setForeground(DARK_BLUE);
        scheduleLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        formPanel.add(scheduleLabel, gbc);

        // Departure Time
        gbc.gridy = 7; gbc.gridwidth = 1;
        JLabel depTimeLabel = new JLabel("Departure Time");
        depTimeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        depTimeLabel.setForeground(DARK_BLUE);
        formPanel.add(depTimeLabel, gbc);

        gbc.gridx = 1;
        JPanel depTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        depTimePanel.setBackground(CARD_WHITE);
        depTimePanel.add(departureTimeField);
        JLabel depTimeHint = new JLabel("  (YYYY-MM-DD HH:MM)");
        depTimeHint.setFont(new Font("Arial", Font.ITALIC, 12));
        depTimeHint.setForeground(new Color(120, 120, 120));
        depTimePanel.add(depTimeHint);
        formPanel.add(depTimePanel, gbc);

        // Arrival Time
        gbc.gridx = 0; gbc.gridy = 8;
        JLabel arrTimeLabel = new JLabel("Arrival Time");
        arrTimeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        arrTimeLabel.setForeground(DARK_BLUE);
        formPanel.add(arrTimeLabel, gbc);

        gbc.gridx = 1;
        JPanel arrTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        arrTimePanel.setBackground(CARD_WHITE);
        arrTimePanel.add(destinationTimeField);
        JLabel arrTimeHint = new JLabel("  (YYYY-MM-DD HH:MM)");
        arrTimeHint.setFont(new Font("Arial", Font.ITALIC, 12));
        arrTimeHint.setForeground(new Color(120, 120, 120));
        arrTimePanel.add(arrTimeHint);
        formPanel.add(arrTimePanel, gbc);

        // Pricing & Capacity Section
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        JLabel pricingLabel = new JLabel("💰 Pricing & Capacity");
        pricingLabel.setFont(new Font("Arial", Font.BOLD, 16));
        pricingLabel.setForeground(DARK_BLUE);
        pricingLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        formPanel.add(pricingLabel, gbc);

        // Capacity
        gbc.gridy = 10; gbc.gridwidth = 1;
        JLabel capacityLabel = new JLabel("Passenger Capacity");
        capacityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        capacityLabel.setForeground(DARK_BLUE);
        formPanel.add(capacityLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(capacityField, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 11;
        JLabel priceLabel = new JLabel("Ticket Price (INR)");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(DARK_BLUE);
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(priceField, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        return contentPanel;
    }

    private JPanel createButtonSection() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_GRAY);
        buttonPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        // Enhanced button sizing
        saveButton.setPreferredSize(new Dimension(160, 45));
        cancelButton.setPreferredSize(new Dimension(120, 45));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }
}
>>>>>>> 6106559f052df2a1a38f022b0aff140098b7b020
