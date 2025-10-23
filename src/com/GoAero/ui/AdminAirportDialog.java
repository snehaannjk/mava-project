package com.GoAero.ui;

import com.GoAero.dao.AirportDAO;
import com.GoAero.model.Airport;
import com.GoAero.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Dialog for admin to add or edit airports with modern UI design
 */
public class AdminAirportDialog extends JDialog {
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
    private Airport airport;
    private AirportDAO airportDAO;
    private boolean isEditMode;
    private boolean dataChanged = false;
    
    private JTextField codeField, nameField, cityField, countryField;
    private JButton saveButton, cancelButton;

    public AdminAirportDialog(Frame parent, Airport airport, AirportDAO airportDAO) {
        super(parent, airport == null ? "Add Airport" : "Edit Airport", true);
        this.airport = airport;
        this.airportDAO = airportDAO;
        this.isEditMode = (airport != null);
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        if (isEditMode) {
            loadAirportData();
        }
    }

    private void initializeComponents() {
        setSize(750, 850);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Create enhanced styled form fields with improved placeholders
        codeField = createStyledTextFieldWithPlaceholder("Enter IATA/ICAO code (e.g., LAX, KLAX)");
        nameField = createStyledTextFieldWithPlaceholder("Enter full airport name (e.g., Los Angeles International)");
        cityField = createStyledTextFieldWithPlaceholder("Enter city name (e.g., Los Angeles)");
        countryField = createStyledTextFieldWithPlaceholder("Enter country name (e.g., United States)");
        
        // Create modern styled buttons with enhanced icons
        String buttonText = isEditMode ? "� Update Airport" : "🛫 Create Airport";
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

        // Create scrollable content area
        JPanel scrollableContent = new JPanel(new BorderLayout());
        scrollableContent.setBackground(BACKGROUND_GRAY);

        // Content section with form
        JPanel contentSection = createContentSection();
        scrollableContent.add(contentSection, BorderLayout.CENTER);

        // Button section
        JPanel buttonSection = createButtonSection();
        scrollableContent.add(buttonSection, BorderLayout.SOUTH);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(scrollableContent);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND_GRAY);
        scrollPane.getViewport().setBackground(BACKGROUND_GRAY);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        saveButton.addActionListener(e -> saveAirport());
        cancelButton.addActionListener(e -> dispose());
        
        // Enter key on country field
        countryField.addActionListener(e -> saveAirport());

        // Auto-format airport code as user types
        codeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String text = codeField.getText().toUpperCase();
                if (!text.equals(codeField.getText())) {
                    codeField.setText(text);
                }
            }
        });
    }

    private void loadAirportData() {
        codeField.setText(airport.getAirportCode());
        nameField.setText(airport.getAirportName());
        cityField.setText(airport.getCity());
        countryField.setText(airport.getCountry());
    }

    private void saveAirport() {
        if (!validateInput()) {
            return;
        }

        try {
            if (isEditMode) {
                updateAirport();
            } else {
                createAirport();
            }
        } catch (Exception e) {
            showError("Save failed: " + e.getMessage());
        }
    }

    private void createAirport() {
        Airport newAirport = new Airport();
        newAirport.setAirportCode(ValidationUtil.formatAirportCode(codeField.getText().trim()));
        newAirport.setAirportName(nameField.getText().trim());
        newAirport.setCity(cityField.getText().trim());
        newAirport.setCountry(countryField.getText().trim());

        Airport savedAirport = airportDAO.create(newAirport);
        if (savedAirport != null) {
            showSuccess("Airport created successfully!");
            dataChanged = true;
            dispose();
        } else {
            showError("Failed to create airport. Please try again.");
        }
    }

    private void updateAirport() {
        airport.setAirportCode(ValidationUtil.formatAirportCode(codeField.getText().trim()));
        airport.setAirportName(nameField.getText().trim());
        airport.setCity(cityField.getText().trim());
        airport.setCountry(countryField.getText().trim());

        boolean success = airportDAO.update(airport);
        if (success) {
            showSuccess("Airport updated successfully!");
            dataChanged = true;
            dispose();
        } else {
            showError("Failed to update airport. Please try again.");
        }
    }

    private boolean validateInput() {
        // Airport Code validation
        String code = codeField.getText().trim();
        if (!ValidationUtil.isValidAirportCode(code)) {
            showError(ValidationUtil.getAirportCodeErrorMessage());
            codeField.requestFocus();
            return false;
        }

        // Check if airport code already exists
        int excludeAirportId = isEditMode ? airport.getAirportId() : -1;
        if (airportDAO.codeExists(code.toUpperCase(), excludeAirportId)) {
            showError("An airport with this code already exists.");
            codeField.requestFocus();
            return false;
        }

        // Airport Name validation
        if (!ValidationUtil.isNotEmpty(nameField.getText())) {
            showError("Airport name is required.");
            nameField.requestFocus();
            return false;
        }

        if (!ValidationUtil.hasMinLength(nameField.getText(), 3)) {
            showError("Airport name must be at least 3 characters long.");
            nameField.requestFocus();
            return false;
        }

        // City validation
        if (!ValidationUtil.isNotEmpty(cityField.getText())) {
            showError("City is required.");
            cityField.requestFocus();
            return false;
        }

        if (!ValidationUtil.hasMinLength(cityField.getText(), 2)) {
            showError("City name must be at least 2 characters long.");
            cityField.requestFocus();
            return false;
        }

        // Country validation
        if (!ValidationUtil.isNotEmpty(countryField.getText())) {
            showError("Country is required.");
            countryField.requestFocus();
            return false;
        }

        if (!ValidationUtil.hasMinLength(countryField.getText(), 2)) {
            showError("Country name must be at least 2 characters long.");
            countryField.requestFocus();
            return false;
        }

        return true;
    }

    public boolean isDataChanged() {
        return dataChanged;
    }

    private void showError(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog(this, "Airport Validation Error", true);
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
        JDialog dialog = new JDialog(this, "Airport Operation Complete", true);
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
        JLabel successIcon = new JLabel("✅");
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
        JButton ok = createStyledButton("Perfect!", SUCCESS_GREEN, Color.WHITE, 14);
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
    private JTextField createStyledTextFieldWithPlaceholder(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(280, 40));
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

    private JPanel createHeaderSection() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        // Title section with airport icon
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_GRAY);

        JLabel titleLabel = new JLabel("🛫 " + (isEditMode ? "Edit Airport Information" : "Add New Airport Location"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(isEditMode ? 
            "Update airport information and location details" : 
            "Register a new airport location in the GoAero network");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);

        // Admin badge
        JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        adminPanel.setBackground(BACKGROUND_GRAY);
        
        JLabel adminBadge = new JLabel("👨‍💼 Airport Management");
        adminBadge.setFont(new Font("Arial", Font.BOLD, 12));
        adminBadge.setForeground(ACCENT_ORANGE);
        adminBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_ORANGE, 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        adminBadge.setBackground(new Color(255, 248, 225));
        adminBadge.setOpaque(true);
        adminPanel.add(adminBadge);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(adminPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createContentSection() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CARD_WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(30, 30, 30, 30)
        ));

        // Form panel with enhanced layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 15, 18, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Airport Information Section Header
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel sectionLabel = new JLabel("🛫 Airport Information");
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sectionLabel.setForeground(DARK_BLUE);
        sectionLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        formPanel.add(sectionLabel, gbc);

        // Airport Code with enhanced styling
        gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel codeLabel = new JLabel("Airport Code");
        codeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        codeLabel.setForeground(DARK_BLUE);
        formPanel.add(codeLabel, gbc);

        gbc.gridx = 1;
        JPanel codePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        codePanel.setBackground(CARD_WHITE);
        codePanel.add(codeField);
        JLabel codeHint = new JLabel("  (3-4 letters, e.g., LAX, JFK, KLAX)");
        codeHint.setFont(new Font("Arial", Font.ITALIC, 12));
        codeHint.setForeground(new Color(120, 120, 120));
        codePanel.add(codeHint);
        formPanel.add(codePanel, gbc);

        // Airport Name
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel nameLabel = new JLabel("Airport Name");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(DARK_BLUE);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Location Section Header
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel locationLabel = new JLabel("🌍 Location Details");
        locationLabel.setFont(new Font("Arial", Font.BOLD, 16));
        locationLabel.setForeground(DARK_BLUE);
        locationLabel.setBorder(new EmptyBorder(20, 0, 15, 0));
        formPanel.add(locationLabel, gbc);

        // City
        gbc.gridy = 4; gbc.gridwidth = 1;
        JLabel cityLabel = new JLabel("City");
        cityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cityLabel.setForeground(DARK_BLUE);
        formPanel.add(cityLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(cityField, gbc);

        // Country
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel countryLabel = new JLabel("Country");
        countryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        countryLabel.setForeground(DARK_BLUE);
        formPanel.add(countryLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(countryField, gbc);

        // Information note
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JLabel infoNote = new JLabel("<html><i>💡 Ensure airport codes follow IATA (3-letter) or ICAO (4-letter) standards</i></html>");
        infoNote.setFont(new Font("Arial", Font.ITALIC, 12));
        infoNote.setForeground(new Color(120, 120, 120));
        infoNote.setBorder(new EmptyBorder(20, 0, 0, 0));
        formPanel.add(infoNote, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        return contentPanel;
    }

    private JPanel createButtonSection() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(BACKGROUND_GRAY);
        buttonPanel.setBorder(new EmptyBorder(35, 0, 20, 0));

        // Enhanced button sizing
        saveButton.setPreferredSize(new Dimension(160, 45));
        cancelButton.setPreferredSize(new Dimension(120, 45));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }
}