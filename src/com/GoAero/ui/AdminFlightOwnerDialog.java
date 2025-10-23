package com.GoAero.ui;

import com.GoAero.dao.FlightOwnerDAO;
import com.GoAero.model.FlightOwner;
import com.GoAero.util.PasswordUtil;
import com.GoAero.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Dialog for admin to add or edit flight owners (airline companies) with modern UI design
 */
public class AdminFlightOwnerDialog extends JDialog {
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
    private FlightOwner flightOwner;
    private FlightOwnerDAO flightOwnerDAO;
    private boolean isEditMode;
    private boolean dataChanged = false;
    
    private JTextField companyNameField, companyCodeField, contactInfoField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton saveButton, cancelButton, generatePasswordButton;
    private JLabel passwordLabel, confirmPasswordLabel;

    public AdminFlightOwnerDialog(Frame parent, FlightOwner flightOwner, FlightOwnerDAO flightOwnerDAO) {
        super(parent, flightOwner == null ? "Add Airline" : "Edit Airline", true);
        this.flightOwner = flightOwner;
        this.flightOwnerDAO = flightOwnerDAO;
        this.isEditMode = (flightOwner != null);
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        if (isEditMode) {
            loadFlightOwnerData();
        }
    }

    private void initializeComponents() {
        setSize(850, 750);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Create styled form fields with placeholders
        companyNameField = createStyledTextField("Enter airline company name");
        companyCodeField = createStyledTextField("2-3 letters (e.g., AA, DL)");
        contactInfoField = createStyledTextField("Enter contact email or phone");
        passwordField = createStyledPasswordField("Enter secure password");
        confirmPasswordField = createStyledPasswordField("Confirm password");
        
        // Create modern styled buttons
        String buttonText = isEditMode ? "🏢 Update Airline" : "🏢 Create Airline";
        saveButton = createStyledButton(buttonText, PRIMARY_BLUE, Color.WHITE, 14);
        cancelButton = createStyledButton("❌ Cancel", LIGHT_GRAY, DARK_BLUE, 14);
        generatePasswordButton = createStyledButton("🔑 Generate", SUCCESS_GREEN, Color.WHITE, 12);
        
        passwordLabel = new JLabel("Password:");
        confirmPasswordLabel = new JLabel("Confirm Password:");
        
        // In edit mode, password fields are optional
        if (isEditMode) {
            passwordLabel.setText("New Password (optional):");
            confirmPasswordLabel.setText("Confirm New Password:");
        }
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
        saveButton.addActionListener(e -> saveFlightOwner());
        cancelButton.addActionListener(e -> dispose());
        generatePasswordButton.addActionListener(e -> generatePassword());
        
        // Enter key on confirm password field
        confirmPasswordField.addActionListener(e -> saveFlightOwner());

        // Auto-format company code as user types
        companyCodeField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String text = companyCodeField.getText().toUpperCase();
                if (!text.equals(companyCodeField.getText())) {
                    companyCodeField.setText(text);
                }
            }
        });
    }

    private void loadFlightOwnerData() {
        companyNameField.setText(flightOwner.getCompanyName());
        companyCodeField.setText(flightOwner.getCompanyCode());
        contactInfoField.setText(flightOwner.getContactInfo());
    }

    private void generatePassword() {
        String generatedPassword = PasswordUtil.generateRandomPassword(8);
        passwordField.setText(generatedPassword);
        confirmPasswordField.setText(generatedPassword);
        
        JOptionPane.showMessageDialog(this, 
            "Generated password: " + generatedPassword + "\n\nPlease save this password and share it with the airline company.",
            "Password Generated", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveFlightOwner() {
        if (!validateInput()) {
            return;
        }

        try {
            if (isEditMode) {
                updateFlightOwner();
            } else {
                createFlightOwner();
            }
        } catch (Exception e) {
            showError("Save failed: " + e.getMessage());
        }
    }

    private void createFlightOwner() {
        FlightOwner newOwner = new FlightOwner();
        newOwner.setCompanyName(companyNameField.getText().trim());
        newOwner.setCompanyCode(ValidationUtil.formatCompanyCode(companyCodeField.getText().trim()));
        newOwner.setContactInfo(contactInfoField.getText().trim());
        newOwner.setPasswordHash(PasswordUtil.hashPassword(new String(passwordField.getPassword())));

        FlightOwner savedOwner = flightOwnerDAO.create(newOwner);
        if (savedOwner != null) {
            showSuccess("Airline created successfully!");
            dataChanged = true;
            dispose();
        } else {
            showError("Failed to create airline. Please try again.");
        }
    }

    private void updateFlightOwner() {
        flightOwner.setCompanyName(companyNameField.getText().trim());
        flightOwner.setCompanyCode(ValidationUtil.formatCompanyCode(companyCodeField.getText().trim()));
        flightOwner.setContactInfo(contactInfoField.getText().trim());

        // Update password only if new password is provided
        String newPassword = new String(passwordField.getPassword());
        if (!newPassword.isEmpty()) {
            flightOwner.setPasswordHash(PasswordUtil.hashPassword(newPassword));
        }

        boolean success = flightOwnerDAO.update(flightOwner);
        if (success) {
            showSuccess("Airline updated successfully!");
            dataChanged = true;
            dispose();
        } else {
            showError("Failed to update airline. Please try again.");
        }
    }

    private boolean validateInput() {
        // Company Name validation
        if (!ValidationUtil.isNotEmpty(companyNameField.getText())) {
            showError("Company name is required.");
            companyNameField.requestFocus();
            return false;
        }

        if (!ValidationUtil.hasMinLength(companyNameField.getText(), 2)) {
            showError("Company name must be at least 2 characters long.");
            companyNameField.requestFocus();
            return false;
        }

        // Company Code validation
        String code = companyCodeField.getText().trim();
        if (!ValidationUtil.isValidCompanyCode(code)) {
            showError(ValidationUtil.getCompanyCodeErrorMessage());
            companyCodeField.requestFocus();
            return false;
        }

        // Check if company code already exists
        int excludeOwnerId = isEditMode ? flightOwner.getOwnerId() : -1;
        if (flightOwnerDAO.codeExists(code.toUpperCase(), excludeOwnerId)) {
            showError("An airline with this company code already exists.");
            companyCodeField.requestFocus();
            return false;
        }

        // Contact Info validation
        if (!ValidationUtil.isNotEmpty(contactInfoField.getText())) {
            showError("Contact information is required.");
            contactInfoField.requestFocus();
            return false;
        }

        // Password validation
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!isEditMode || !password.isEmpty()) {
            if (password.isEmpty()) {
                showError("Password is required.");
                passwordField.requestFocus();
                return false;
            }

            if (!PasswordUtil.isValidPassword(password)) {
                showError(PasswordUtil.getPasswordRequirements());
                passwordField.requestFocus();
                return false;
            }

            if (!password.equals(confirmPassword)) {
                showError("Passwords do not match.");
                confirmPasswordField.requestFocus();
                return false;
            }
        }

        return true;
    }

    public boolean isDataChanged() {
        return dataChanged;
    }

    private void showError(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog(this, "Airline Management Error", true);
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
        JLabel errorIcon = new JLabel("🏢⚠");
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
        JDialog dialog = new JDialog(this, "Airline Operation Complete", true);
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
        JLabel successIcon = new JLabel("🏢✅");
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
     * Creates a styled password field with placeholder support
     */
    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(25);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(320, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Add focus effects (password fields don't have placeholder text)
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
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

        // Title section with airline icon
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_GRAY);

        JLabel titleLabel = new JLabel("🏢 " + (isEditMode ? "Edit Airline Company" : "Add New Airline Company"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(isEditMode ? 
            "Update airline company information and access credentials" : 
            "Register a new airline company with the GoAero platform");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);

        // Admin badge
        JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        adminPanel.setBackground(BACKGROUND_GRAY);
        
        JLabel adminBadge = new JLabel("👨‍💼 Admin Access");
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

        // Company Information Section
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel companyInfoLabel = new JLabel("🏢 Company Information");
        companyInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        companyInfoLabel.setForeground(DARK_BLUE);
        companyInfoLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        formPanel.add(companyInfoLabel, gbc);

        // Company Name
        gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel nameLabel = new JLabel("Company Name");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(DARK_BLUE);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(companyNameField, gbc);

        // Company Code
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel codeLabel = new JLabel("Company Code");
        codeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        codeLabel.setForeground(DARK_BLUE);
        formPanel.add(codeLabel, gbc);

        gbc.gridx = 1;
        JPanel codePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        codePanel.setBackground(CARD_WHITE);
        codePanel.add(companyCodeField);
        JLabel codeHint = new JLabel("  (2-3 letters, e.g., AA, DL, UA)");
        codeHint.setFont(new Font("Arial", Font.ITALIC, 12));
        codeHint.setForeground(new Color(120, 120, 120));
        codePanel.add(codeHint);
        formPanel.add(codePanel, gbc);

        // Contact Information
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel contactLabel = new JLabel("Contact Information");
        contactLabel.setFont(new Font("Arial", Font.BOLD, 14));
        contactLabel.setForeground(DARK_BLUE);
        formPanel.add(contactLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(contactInfoField, gbc);

        // Security Section
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JLabel securityLabel = new JLabel("🔒 Security Credentials");
        securityLabel.setFont(new Font("Arial", Font.BOLD, 16));
        securityLabel.setForeground(DARK_BLUE);
        securityLabel.setBorder(new EmptyBorder(20, 0, 10, 0));
        formPanel.add(securityLabel, gbc);

        // Password
        gbc.gridy = 5; gbc.gridwidth = 1;
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(DARK_BLUE);
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passwordPanel.setBackground(CARD_WHITE);
        passwordPanel.add(passwordField);
        passwordPanel.add(Box.createHorizontalStrut(10));
        generatePasswordButton.setPreferredSize(new Dimension(100, 40));
        passwordPanel.add(generatePasswordButton);
        formPanel.add(passwordPanel, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 6;
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmPasswordLabel.setForeground(DARK_BLUE);
        formPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // Password requirements
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        String requirementText = isEditMode ? 
            "💡 Leave password fields empty to keep current password" : 
            "💡 " + PasswordUtil.getPasswordRequirements();
        JLabel requirementsLabel = new JLabel("<html><small>" + requirementText + "</small></html>");
        requirementsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        requirementsLabel.setForeground(new Color(120, 120, 120));
        requirementsLabel.setBorder(new EmptyBorder(8, 6, 0, 6));
        formPanel.add(requirementsLabel, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        return contentPanel;
    }

    private JPanel createButtonSection() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(BACKGROUND_GRAY);
        buttonPanel.setBorder(new EmptyBorder(35, 0, 20, 0));

        // Enhanced button sizing
        saveButton.setPreferredSize(new Dimension(170, 45));
        cancelButton.setPreferredSize(new Dimension(120, 45));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }
}