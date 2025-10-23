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
 * Flight Owner registration dialog for new airline companies with modern UI design
 */
public class FlightOwnerRegistrationDialog extends JDialog {
    // Professional color scheme (consistent with other GoAero pages)
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private static final Color ACCENT_ORANGE = new Color(255, 152, 0);
    private static final Color DARK_BLUE = new Color(13, 71, 161);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    private static final Color BACKGROUND_GRAY = new Color(250, 250, 250);
    private static final Color CARD_WHITE = Color.WHITE;
    
    private JTextField companyNameField, companyCodeField, contactInfoField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, cancelButton;
    private FlightOwnerDAO flightOwnerDAO;

    public FlightOwnerRegistrationDialog(Frame parent) {
        super(parent, "Airline Registration", true);
        flightOwnerDAO = new FlightOwnerDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        setSize(800, 750);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Create styled form fields with placeholders
        companyNameField = createStyledTextFieldWithPlaceholder("Enter company name");
        companyCodeField = createStyledTextFieldWithPlaceholder("Enter company code (2-5 characters)");
        contactInfoField = createStyledTextFieldWithPlaceholder("Enter contact information");
        passwordField = createStyledPasswordFieldWithPlaceholder("Enter a secure password");
        confirmPasswordField = createStyledPasswordFieldWithPlaceholder("Confirm your password");
        
        // Create styled buttons
        registerButton = createStyledButton("🏢 Register Company", SUCCESS_GREEN, Color.WHITE);
        cancelButton = createStyledButton("❌ Cancel", LIGHT_GRAY, DARK_BLUE);
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

        // Content section
        JPanel contentSection = createContentSection();
        mainPanel.add(contentSection, BorderLayout.CENTER);

        // Button section
        JPanel buttonSection = createButtonSection();
        mainPanel.add(buttonSection, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        registerButton.addActionListener(e -> handleRegistration());
        cancelButton.addActionListener(e -> dispose());

        // Enter key on confirm password field
        confirmPasswordField.addActionListener(e -> handleRegistration());

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

    private void handleRegistration() {
        if (!validateInput()) {
            return;
        }

        try {
            // Create new flight owner
            FlightOwner flightOwner = new FlightOwner();
            flightOwner.setCompanyName(companyNameField.getText().trim());
            flightOwner.setCompanyCode(ValidationUtil.formatCompanyCode(companyCodeField.getText().trim()));
            flightOwner.setContactInfo(contactInfoField.getText().trim());
            flightOwner.setPasswordHash(PasswordUtil.hashPassword(new String(passwordField.getPassword())));
            flightOwner.setFlightCount(0);

            // Save to database
            FlightOwner savedOwner = flightOwnerDAO.create(flightOwner);
            if (savedOwner != null) {
                showSuccess("Company registration successful! You can now login with your company code and password.");
                clearFields();
                dispose();
            } else {
                showError("Registration failed. Please try again.");
            }
        } catch (Exception e) {
            showError("Registration failed: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        // Company Name validation
        String companyName = companyNameField.getText().trim();
        if (companyName.equals("Enter company name") || !ValidationUtil.isNotEmpty(companyName)) {
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
        String companyCode = companyCodeField.getText().trim();
        if (!ValidationUtil.isValidCompanyCode(companyCode)) {
            showError(ValidationUtil.getCompanyCodeErrorMessage());
            companyCodeField.requestFocus();
            return false;
        }

        // Check if company code already exists
        if (flightOwnerDAO.codeExists(companyCode.toUpperCase())) {
            showError("A company with this code already exists. Please choose a different code.");
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
        if (!PasswordUtil.isValidPassword(password)) {
            showError(PasswordUtil.getPasswordRequirements());
            passwordField.requestFocus();
            return false;
        }

        // Confirm password validation
        String confirmPassword = new String(confirmPasswordField.getPassword());
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            confirmPasswordField.requestFocus();
            return false;
        }

        return true;
    }

    private void clearFields() {
        companyNameField.setText("Enter company name");
        companyNameField.setForeground(Color.GRAY);
        companyCodeField.setText("Enter company code (2-5 characters)");
        companyCodeField.setForeground(Color.GRAY);
        contactInfoField.setText("Enter contact information");
        contactInfoField.setForeground(Color.GRAY);
        passwordField.setText("Enter a secure password");
        passwordField.setForeground(Color.GRAY);
        passwordField.setEchoChar((char) 0);
        confirmPasswordField.setText("Confirm your password");
        confirmPasswordField.setForeground(Color.GRAY);
        confirmPasswordField.setEchoChar((char) 0);
    }

    private void showError(String message) {
        // Create a custom styled error dialog for airline registration
        JDialog dialog = new JDialog(this, "Registration Error", true);
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

        // Header (icon + title) with airline theme
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        header.setOpaque(false);
        JLabel errorIcon = new JLabel("⚠");
        errorIcon.setFont(new Font("Arial", Font.BOLD, 24));
        errorIcon.setForeground(new Color(244, 67, 54));
        header.add(errorIcon);
        JLabel title = new JLabel("Registration Issue");
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
        JButton ok = createStyledButton("Understood", new Color(244, 67, 54), Color.WHITE);
        ok.setPreferredSize(new Dimension(110, 36));
        ok.addActionListener(e -> dialog.dispose());
        btnPanel.add(ok);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(Math.max(400, dialog.getWidth()), dialog.getHeight());
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

    private void showSuccess(String message) {
        // Create a custom styled success dialog for airline registration
        JDialog dialog = new JDialog(this, "Registration Complete", true);
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
                // background with corporate gradient
                GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(248, 255, 248));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 12, arc, arc);
                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(20, 22, 18, 22));

        // Success header with airline celebration
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        header.setOpaque(false);
        JLabel successIcon = new JLabel("🎉");
        successIcon.setFont(new Font("Arial", Font.BOLD, 26));
        header.add(successIcon);
        JLabel title = new JLabel("Welcome to GoAero!");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(DARK_BLUE);
        header.add(title);

        content.add(header, BorderLayout.NORTH);

        // Message with airline-specific content
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        messagePanel.setBorder(new EmptyBorder(10, 8, 15, 8));
        
        JLabel mainMessage = new JLabel("Your airline has been successfully registered!");
        mainMessage.setFont(new Font("Arial", Font.PLAIN, 15));
        
        JLabel subMessage = new JLabel("You can now login and start managing your flights on GoAero.");
        subMessage.setFont(new Font("Arial", Font.ITALIC, 13));
        subMessage.setForeground(new Color(100, 100, 100));
        subMessage.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        JLabel actionMessage = new JLabel("✈️ Ready to add your first flight?");
        actionMessage.setFont(new Font("Arial", Font.BOLD, 12));
        actionMessage.setForeground(ACCENT_ORANGE);
        actionMessage.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        messagePanel.add(mainMessage, BorderLayout.NORTH);
        messagePanel.add(subMessage, BorderLayout.CENTER);
        messagePanel.add(actionMessage, BorderLayout.SOUTH);
        content.add(messagePanel, BorderLayout.CENTER);

        // Enhanced button panel with airline actions
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        JButton loginBtn = createStyledButton("🏢 Go to Dashboard", PRIMARY_BLUE, Color.WHITE);
        loginBtn.setPreferredSize(new Dimension(150, 38));
        loginBtn.addActionListener(e -> {
            dialog.dispose();
            // Close registration dialog and navigate to login
            dispose();
        });
        
        JButton okBtn = createStyledButton("Perfect!", SUCCESS_GREEN, Color.WHITE);
        okBtn.setPreferredSize(new Dimension(100, 38));
        okBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(loginBtn);
        btnPanel.add(okBtn);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(Math.max(480, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(this);

        // Smooth celebration animation
        try {
            dialog.setOpacity(0f);
            Timer fadeTimer = new Timer(15, null);
            final float[] alpha = {0f};
            fadeTimer.addActionListener(ev -> {
                alpha[0] += 0.06f;
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

    private JTextField createStyledTextFieldWithPlaceholder(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(320, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        // Set placeholder text
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        
        // Add focus effects with placeholder handling
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(LIGHT_GRAY, 2),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                    ));
                } else {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                    ));
                }
            }
        });
        
        return field;
    }

    private JPasswordField createStyledPasswordFieldWithPlaceholder(String placeholder) {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(320, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        // Set placeholder text
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.setEchoChar((char) 0); // Show placeholder text clearly
        
        // Add focus effects with placeholder handling
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('*'); // Enable password masking
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    field.setEchoChar((char) 0); // Show placeholder text clearly
                    field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(LIGHT_GRAY, 2),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                    ));
                } else {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                    ));
                }
            }
        });
        
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
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

    private Color createHoverColor(Color originalColor) {
        if (originalColor.equals(SUCCESS_GREEN)) {
            return new Color(102, 187, 106);
        } else if (originalColor.equals(LIGHT_GRAY)) {
            return new Color(220, 220, 220);
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
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        // Title
        JLabel titleLabel = new JLabel("Register Airline Company");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Join the GoAero network as a flight operator");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createContentSection() {
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(CARD_WHITE);
        contentWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Company Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel companyNameLabel = new JLabel("Company Name:");
        companyNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        companyNameLabel.setForeground(DARK_BLUE);
        formPanel.add(companyNameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(companyNameField, gbc);

        // Company Code
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel companyCodeLabel = new JLabel("Company Code:");
        companyCodeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        companyCodeLabel.setForeground(DARK_BLUE);
        formPanel.add(companyCodeLabel, gbc);
        gbc.gridx = 1;
        JPanel codePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        codePanel.setBackground(CARD_WHITE);
        codePanel.add(companyCodeField);
        JLabel codeHint = new JLabel(" (2-5 characters)");
        codeHint.setFont(new Font("Arial", Font.ITALIC, 12));
        codeHint.setForeground(Color.GRAY);
        codePanel.add(codeHint);
        formPanel.add(codePanel, gbc);

        // Contact Information
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel contactLabel = new JLabel("Contact Information:");
        contactLabel.setFont(new Font("Arial", Font.BOLD, 14));
        contactLabel.setForeground(DARK_BLUE);
        formPanel.add(contactLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(contactInfoField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(DARK_BLUE);
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmPasswordLabel.setForeground(DARK_BLUE);
        formPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // Password requirements
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JLabel requirementsLabel = new JLabel("<html><div style='font-size:11px; color:#666;'>" + 
            PasswordUtil.getPasswordRequirements() + "</div></html>");
        formPanel.add(requirementsLabel, gbc);

        contentWrapper.add(formPanel, BorderLayout.CENTER);
        return contentWrapper;
    }

    private JPanel createButtonSection() {
        JPanel buttonSection = new JPanel(new BorderLayout());
        buttonSection.setBackground(BACKGROUND_GRAY);
        buttonSection.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Left side - Cancel button
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(BACKGROUND_GRAY);
        cancelButton.setPreferredSize(new Dimension(120, 40));
        leftPanel.add(cancelButton);

        // Right side - Register button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(BACKGROUND_GRAY);
        registerButton.setPreferredSize(new Dimension(180, 40));
        rightPanel.add(registerButton);

        buttonSection.add(leftPanel, BorderLayout.WEST);
        buttonSection.add(rightPanel, BorderLayout.EAST);

        return buttonSection;
    }
}
