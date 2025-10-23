package com.GoAero.ui;

import com.GoAero.dao.FlightOwnerDAO;
import com.GoAero.model.FlightOwner;
import com.GoAero.model.SessionManager;
import com.GoAero.util.PasswordUtil;
import com.GoAero.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel for flight owners to view and edit their company profile with modern UI design
 */
public class OwnerProfilePanel extends JPanel {
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
    private FlightOwner currentOwner;
    private FlightOwnerDAO flightOwnerDAO;
    
    private JTextField companyNameField, companyCodeField, contactInfoField;
    private JPasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    private JButton saveButton, changePasswordButton;
    private JPanel passwordPanel;
    private boolean isPasswordChangeMode = false;

    public OwnerProfilePanel() {
        currentOwner = SessionManager.getInstance().getCurrentFlightOwner();
        if (currentOwner == null) {
            JLabel errorLabel = new JLabel("Access denied. Please login as a flight owner.");
            add(errorLabel);
            return;
        }
        
        flightOwnerDAO = new FlightOwnerDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadOwnerData();
    }

    private void initializeComponents() {
        // Create styled form fields
        companyNameField = createStyledTextField("Company Name", 30);
        companyCodeField = createStyledTextField("Company Code", 30);
        contactInfoField = createStyledTextField("Contact Information", 30);
        
        currentPasswordField = createStyledPasswordField("Current Password", 30);
        newPasswordField = createStyledPasswordField("New Password", 30);
        confirmPasswordField = createStyledPasswordField("Confirm New Password", 30);
        
        // Create modern styled buttons
        saveButton = createStyledButton("💾 Save Changes", PRIMARY_BLUE, Color.WHITE, 14);
        changePasswordButton = createStyledButton("🔐 Change Password", ACCENT_ORANGE, Color.WHITE, 14);
        
        // Company code field should be read-only for existing owners
        companyCodeField.setEditable(false);
        companyCodeField.setBackground(new Color(240, 240, 240));
        companyCodeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_GRAY);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_GRAY);
        mainPanel.setBorder(new EmptyBorder(25, 30, 30, 30));

        // Header section
        JPanel headerSection = createHeaderSection();
        mainPanel.add(headerSection, BorderLayout.NORTH);

        // Content section with cards
        JPanel contentSection = createContentSection();
        mainPanel.add(contentSection, BorderLayout.CENTER);

        // Button section
        JPanel buttonSection = createButtonSection();
        mainPanel.add(buttonSection, BorderLayout.SOUTH);

        // Wrap main panel in a scroll pane to handle content overflow
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBackground(BACKGROUND_GRAY);
        scrollPane.getViewport().setBackground(BACKGROUND_GRAY);
        scrollPane.setBorder(null); // Remove default border
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Configure scroll speed for better user experience
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(64);

        // Style the scroll bar to match the modern theme
        scrollPane.getVerticalScrollBar().setBackground(LIGHT_GRAY);
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = PRIMARY_BLUE;
                this.trackColor = LIGHT_GRAY;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = super.createDecreaseButton(orientation);
                button.setBackground(LIGHT_GRAY);
                button.setBorder(null);
                return button;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = super.createIncreaseButton(orientation);
                button.setBackground(LIGHT_GRAY);
                button.setBorder(null);
                return button;
            }
        });

        add(scrollPane, BorderLayout.CENTER);
    }



    private void setupEventListeners() {
        saveButton.addActionListener(e -> saveProfile());
        changePasswordButton.addActionListener(e -> togglePasswordChangeMode());
    }

    private void loadOwnerData() {
        companyNameField.setText(currentOwner.getCompanyName());
        companyCodeField.setText(currentOwner.getCompanyCode());
        contactInfoField.setText(currentOwner.getContactInfo());
    }

    private void togglePasswordChangeMode() {
        isPasswordChangeMode = !isPasswordChangeMode;
        passwordPanel.setVisible(isPasswordChangeMode);
        
        if (isPasswordChangeMode) {
            changePasswordButton.setText("Cancel Password Change");
        } else {
            changePasswordButton.setText("Change Password");
            clearPasswordFields();
        }
        
        revalidate();
        repaint();
    }

    private void clearPasswordFields() {
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    private void saveProfile() {
        if (!validateInput()) {
            return;
        }

        try {
            // Update owner object
            currentOwner.setCompanyName(companyNameField.getText().trim());
            currentOwner.setContactInfo(contactInfoField.getText().trim());

            // Handle password change if in password change mode
            if (isPasswordChangeMode) {
                String newPassword = new String(newPasswordField.getPassword());
                if (!newPassword.isEmpty()) {
                    currentOwner.setPasswordHash(PasswordUtil.hashPassword(newPassword));
                }
            }

            // Save to database
            boolean success = flightOwnerDAO.update(currentOwner);
            
            if (success) {
                // Update session
                SessionManager.getInstance().loginFlightOwner(currentOwner);
                showSuccess("Profile updated successfully!");
                
                if (isPasswordChangeMode) {
                    togglePasswordChangeMode();
                }
            } else {
                showError("Failed to update profile. Please try again.");
            }
            
        } catch (Exception e) {
            showError("Update failed: " + e.getMessage());
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

        // Contact Info validation
        if (!ValidationUtil.isNotEmpty(contactInfoField.getText())) {
            showError("Contact information is required.");
            contactInfoField.requestFocus();
            return false;
        }

        // Password validation if in password change mode
        if (isPasswordChangeMode) {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (currentPassword.isEmpty()) {
                showError("Please enter your current password.");
                currentPasswordField.requestFocus();
                return false;
            }

            if (!PasswordUtil.verifyPassword(currentPassword, currentOwner.getPasswordHash())) {
                showError("Current password is incorrect.");
                currentPasswordField.requestFocus();
                return false;
            }

            if (newPassword.isEmpty()) {
                showError("Please enter a new password.");
                newPasswordField.requestFocus();
                return false;
            }

            if (!PasswordUtil.isValidPassword(newPassword)) {
                showError(PasswordUtil.getPasswordRequirements());
                newPasswordField.requestFocus();
                return false;
            }

            if (!newPassword.equals(confirmPassword)) {
                showError("New passwords do not match.");
                confirmPasswordField.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void showError(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Profile Error", true);
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
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Profile Updated", true);
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
        JLabel title = new JLabel("Profile Updated");
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
        } else {
            // For other colors, create a lighter version
            int r = Math.min(255, originalColor.getRed() + 20);
            int g = Math.min(255, originalColor.getGreen() + 20);
            int b = Math.min(255, originalColor.getBlue() + 20);
            return new Color(r, g, b);
        }
    }

    /**
     * Creates a styled text field with modern design
     */
    private JTextField createStyledTextField(String label, int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Add focus effects
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

    /**
     * Creates a styled password field with modern design
     */
    private JPasswordField createStyledPasswordField(String label, int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Add focus effects
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

        JLabel titleLabel = new JLabel("🏢 Company Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Manage your airline company information and settings");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        // Company status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBackground(BACKGROUND_GRAY);
        
        JLabel statusLabel = new JLabel("🟢 Active Account");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(SUCCESS_GREEN);
        statusPanel.add(statusLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(statusPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentSection() {
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setBackground(BACKGROUND_GRAY);

        // Create two-column layout for cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        cardsPanel.setBackground(BACKGROUND_GRAY);

        // Company Information Card
        JPanel companyCard = createModernCompanyCard();
        cardsPanel.add(companyCard);

        // Security Settings Card
        JPanel securityCard = createModernSecurityCard();
        cardsPanel.add(securityCard);

        contentContainer.add(cardsPanel, BorderLayout.CENTER);
        return contentContainer;
    }

    private JPanel createModernCompanyCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        // Card header with proper spacing
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel cardTitle = new JLabel("📋 Company Information");
        cardTitle.setFont(new Font("Arial", Font.BOLD, 16));
        cardTitle.setForeground(DARK_BLUE);
        headerPanel.add(cardTitle, BorderLayout.WEST);

        // Form panel with improved spacing
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10); // Increased horizontal spacing
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Allow horizontal expansion

        // Company Name section
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel nameLabel = new JLabel("Company Name");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(DARK_BLUE);
        formPanel.add(nameLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 20, 10); // Larger bottom spacing after field
        formPanel.add(companyNameField, gbc);

        // Company Code section
        gbc.gridy = 2;
        gbc.insets = new Insets(12, 10, 12, 10); // Reset to normal spacing
        JLabel codeLabel = new JLabel("Company Code");
        codeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        codeLabel.setForeground(DARK_BLUE);
        formPanel.add(codeLabel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 10, 20, 10); // Larger bottom spacing after field
        JPanel codePanel = new JPanel(new BorderLayout());
        codePanel.setBackground(CARD_WHITE);
        codePanel.add(companyCodeField, BorderLayout.CENTER);

        JLabel codeHint = new JLabel("(Cannot be changed)");
        codeHint.setFont(new Font("Arial", Font.ITALIC, 12));
        codeHint.setForeground(new Color(120, 120, 120));
        codeHint.setBorder(new EmptyBorder(5, 0, 0, 0));
        codePanel.add(codeHint, BorderLayout.SOUTH);
        formPanel.add(codePanel, gbc);

        // Contact Information section
        gbc.gridy = 4;
        gbc.insets = new Insets(12, 10, 12, 10); // Reset to normal spacing
        JLabel contactLabel = new JLabel("Contact Information");
        contactLabel.setFont(new Font("Arial", Font.BOLD, 14));
        contactLabel.setForeground(DARK_BLUE);
        formPanel.add(contactLabel, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(5, 10, 10, 10); // Final field spacing
        formPanel.add(contactInfoField, gbc);

        // Add vertical glue to push content to top
        gbc.gridy = 6;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(Box.createVerticalGlue(), gbc);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(formPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createModernSecurityCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        // Card header with proper spacing
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel cardTitle = new JLabel("🔐 Security Settings");
        cardTitle.setFont(new Font("Arial", Font.BOLD, 16));
        cardTitle.setForeground(DARK_BLUE);

        headerPanel.add(cardTitle, BorderLayout.WEST);
        headerPanel.add(changePasswordButton, BorderLayout.EAST);

        // Main content panel that will switch between security info and password form
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CARD_WHITE);

        // Password form panel (initially hidden)
        passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.setBackground(CARD_WHITE);
        passwordPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10); // Improved horizontal spacing
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Allow horizontal expansion

        // Current Password section
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel currentPwdLabel = new JLabel("Current Password");
        currentPwdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        currentPwdLabel.setForeground(DARK_BLUE);
        passwordPanel.add(currentPwdLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 20, 10); // Larger bottom spacing after field
        passwordPanel.add(currentPasswordField, gbc);

        // New Password section
        gbc.gridy = 2;
        gbc.insets = new Insets(12, 10, 12, 10); // Reset to normal spacing
        JLabel newPwdLabel = new JLabel("New Password");
        newPwdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        newPwdLabel.setForeground(DARK_BLUE);
        passwordPanel.add(newPwdLabel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 10, 20, 10); // Larger bottom spacing after field
        passwordPanel.add(newPasswordField, gbc);

        // Confirm New Password section
        gbc.gridy = 4;
        gbc.insets = new Insets(12, 10, 12, 10); // Reset to normal spacing
        JLabel confirmPwdLabel = new JLabel("Confirm New Password");
        confirmPwdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmPwdLabel.setForeground(DARK_BLUE);
        passwordPanel.add(confirmPwdLabel, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(5, 10, 20, 10); // Larger bottom spacing after field
        passwordPanel.add(confirmPasswordField, gbc);

        // Password requirements section
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel requirementsLabel = new JLabel("<html><small>" + PasswordUtil.getPasswordRequirements() + "</small></html>");
        requirementsLabel.setForeground(new Color(120, 120, 120));
        passwordPanel.add(requirementsLabel, gbc);

        // Add vertical glue to push content to top
        gbc.gridy = 7;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        passwordPanel.add(Box.createVerticalGlue(), gbc);

        // Security info panel (shown when password panel is hidden)
        JPanel securityInfo = new JPanel(new BorderLayout());
        securityInfo.setBackground(CARD_WHITE);
        securityInfo.setBorder(new EmptyBorder(40, 20, 40, 20));

        JLabel securityText = new JLabel("<html><div style='text-align: center;'>" +
                "🛡️<br><br>" +
                "Your account security is protected.<br>" +
                "Click 'Change Password' to update<br>" +
                "your login credentials." +
                "</div></html>");
        securityText.setFont(new Font("Arial", Font.PLAIN, 14));
        securityText.setForeground(new Color(100, 100, 100));
        securityText.setHorizontalAlignment(SwingConstants.CENTER);
        securityInfo.add(securityText, BorderLayout.CENTER);

        // Add both panels to content panel
        contentPanel.add(passwordPanel, BorderLayout.CENTER);
        contentPanel.add(securityInfo, BorderLayout.SOUTH);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createButtonSection() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(BACKGROUND_GRAY);
        buttonPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        // Enhanced button sizing
        saveButton.setPreferredSize(new Dimension(160, 45));

        buttonPanel.add(saveButton);

        return buttonPanel;
    }
}
