package com.GoAero.ui;

import com.GoAero.dao.UserDAO;
import com.GoAero.model.User;
import com.GoAero.util.PasswordUtil;
import com.GoAero.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * User registration dialog for new passenger accounts with modern UI design
 */
public class UserRegistrationDialog extends JDialog {
    // Professional color scheme (consistent with other GoAero pages)
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    @SuppressWarnings("unused")
    private static final Color ACCENT_ORANGE = new Color(255, 152, 0);
    private static final Color DARK_BLUE = new Color(13, 71, 161);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    @SuppressWarnings("unused")
    private static final Color HOVER_BLUE = new Color(30, 136, 229);
    private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    private static final Color BACKGROUND_GRAY = new Color(250, 250, 250);
    private static final Color CARD_WHITE = Color.WHITE;
    
    private JTextField firstNameField, lastNameField, emailField, phoneField, dobField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton registerButton, cancelButton;
    private UserDAO userDAO;

    public UserRegistrationDialog(Frame parent) {
        super(parent, "Passenger Registration", true);
        userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        setSize(750, 800);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Create styled form fields with placeholders
        firstNameField = createStyledTextFieldWithPlaceholder("Enter your first name");
        lastNameField = createStyledTextFieldWithPlaceholder("Enter your last name");
        emailField = createStyledTextFieldWithPlaceholder("Enter your email address");
        phoneField = createStyledTextFieldWithPlaceholder("Enter your phone number");
        dobField = createStyledTextFieldWithPlaceholder("YYYY-MM-DD");
        passwordField = createStyledPasswordFieldWithPlaceholder("Enter a secure password");
        confirmPasswordField = createStyledPasswordFieldWithPlaceholder("Confirm your password");
        
        // Create styled buttons
        registerButton = createStyledButton("📝 Register", SUCCESS_GREEN, Color.WHITE);
        cancelButton = createStyledButton("❌ Cancel", LIGHT_GRAY, DARK_BLUE);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GRAY);

        // Main content panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_GRAY);
        mainPanel.setBorder(new EmptyBorder(25, 50, 25, 50));

        // Header section
        JPanel headerSection = createHeaderSection();
        mainPanel.add(headerSection, BorderLayout.NORTH);

        // Form section
        JPanel formSection = createFormSection();
        mainPanel.add(formSection, BorderLayout.CENTER);

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
    }

    private void handleRegistration() {
        if (!validateInput()) {
            return;
        }

        try {
            // Create new user
            User user = new User();
            user.setFirstName(firstNameField.getText().trim());
            user.setLastName(lastNameField.getText().trim());
            user.setEmail(emailField.getText().trim().toLowerCase());
            user.setPhone(ValidationUtil.cleanPhoneNumber(phoneField.getText().trim()));
            user.setDateOfBirth(LocalDate.parse(dobField.getText().trim()));
            user.setPasswordHash(PasswordUtil.hashPassword(new String(passwordField.getPassword())));

            // Save to database
            User savedUser = userDAO.create(user);
            if (savedUser != null) {
                showSuccess("Registration successful! You can now login with your email and password.");
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
        // First Name validation
        String firstName = firstNameField.getText().trim();
        if (firstName.equals("Enter your first name") || !ValidationUtil.isNotEmpty(firstName)) {
            showError("First name is required.");
            firstNameField.requestFocus();
            return false;
        }

        // Last Name validation
        String lastName = lastNameField.getText().trim();
        if (lastName.equals("Enter your last name") || !ValidationUtil.isNotEmpty(lastName)) {
            showError("Last name is required.");
            lastNameField.requestFocus();
            return false;
        }

        // Email validation
        String email = emailField.getText().trim();
        if (email.equals("Enter your email address") || !ValidationUtil.isValidEmail(email)) {
            showError(ValidationUtil.getEmailErrorMessage());
            emailField.requestFocus();
            return false;
        }

        // Check if email already exists
        if (userDAO.emailExists(email)) {
            showError("An account with this email already exists.");
            emailField.requestFocus();
            return false;
        }

        // Phone validation
        String phone = phoneField.getText().trim();
        if (phone.equals("Enter your phone number") || !ValidationUtil.isNotEmpty(phone)) {
            showError("Phone number is required.");
            phoneField.requestFocus();
            return false;
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            showError(ValidationUtil.getPhoneErrorMessage());
            phoneField.requestFocus();
            return false;
        }

        // Date of Birth validation
        String dobText = dobField.getText().trim();
        if (dobText.equals("YYYY-MM-DD") || !ValidationUtil.isNotEmpty(dobText)) {
            showError("Date of birth is required.");
            dobField.requestFocus();
            return false;
        }

        LocalDate dob;
        try {
            dob = LocalDate.parse(dobText);
        } catch (DateTimeParseException e) {
            showError("Please enter date of birth in YYYY-MM-DD format.");
            dobField.requestFocus();
            return false;
        }

        if (!ValidationUtil.isValidDateOfBirth(dob)) {
            showError("Please enter a valid date of birth. You must be at least 12 years old.");
            dobField.requestFocus();
            return false;
        }

        // Password validation
        String password = new String(passwordField.getPassword());
        if (password.equals("Enter a secure password") || !PasswordUtil.isValidPassword(password)) {
            showError(PasswordUtil.getPasswordRequirements());
            passwordField.requestFocus();
            return false;
        }

        // Confirm password validation
        String confirmPassword = new String(confirmPasswordField.getPassword());
        if (confirmPassword.equals("Confirm your password") || !password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            confirmPasswordField.requestFocus();
            return false;
        }

        return true;
    }

    private void clearFields() {
        firstNameField.setText("Enter your first name");
        firstNameField.setForeground(Color.GRAY);
        lastNameField.setText("Enter your last name");
        lastNameField.setForeground(Color.GRAY);
        emailField.setText("Enter your email address");
        emailField.setForeground(Color.GRAY);
        phoneField.setText("Enter your phone number");
        phoneField.setForeground(Color.GRAY);
        dobField.setText("YYYY-MM-DD");
        dobField.setForeground(Color.GRAY);
        passwordField.setText("Enter a secure password");
        passwordField.setForeground(Color.GRAY);
        passwordField.setEchoChar((char) 0);
        confirmPasswordField.setText("Confirm your password");
        confirmPasswordField.setForeground(Color.GRAY);
        confirmPasswordField.setEchoChar((char) 0);
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
        errorIcon.setForeground(new Color(244, 67, 54));
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
        JButton ok = createStyledButton("OK", new Color(244, 67, 54), Color.WHITE);
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
        successIcon.setForeground(SUCCESS_GREEN);
        header.add(successIcon);
        JLabel title = new JLabel("Registration Successful");
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
        JButton ok = createStyledButton("Great!", SUCCESS_GREEN, Color.WHITE);
        ok.setPreferredSize(new Dimension(100, 36));
        ok.addActionListener(e -> dialog.dispose());
        btnPanel.add(ok);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(Math.max(420, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(this);

        // Fade-in effect with slight delay for celebration
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

    @SuppressWarnings("unused")
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(320, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return field;
    }

    private JTextField createStyledTextFieldWithPlaceholder(String placeholder) {
        JTextField field = new JTextField(25);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(350, 42));
        field.setMinimumSize(new Dimension(350, 42));
        field.setEnabled(true);
        field.setEditable(true);
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

    @SuppressWarnings("unused")
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(320, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordFieldWithPlaceholder(String placeholder) {
        JPasswordField field = new JPasswordField(25);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(350, 42));
        field.setMinimumSize(new Dimension(350, 42));
        field.setEnabled(true);
        field.setEditable(true);
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
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Join GoAero and start your journey");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createFormSection() {
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(CARD_WHITE);
        formWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // First Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        firstNameLabel.setForeground(DARK_BLUE);
        formPanel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        lastNameLabel.setForeground(DARK_BLUE);
        formPanel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setForeground(DARK_BLUE);
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        phoneLabel.setForeground(DARK_BLUE);
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // Date of Birth
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel dobLabel = new JLabel("Date of Birth:");
        dobLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dobLabel.setForeground(DARK_BLUE);
        formPanel.add(dobLabel, gbc);
        gbc.gridx = 1;
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dobPanel.setBackground(CARD_WHITE);
        dobPanel.add(dobField);
        JLabel dobHint = new JLabel(" (YYYY-MM-DD)");
        dobHint.setFont(new Font("Arial", Font.ITALIC, 12));
        dobHint.setForeground(Color.GRAY);
        dobPanel.add(dobHint);
        formPanel.add(dobPanel, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(DARK_BLUE);
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        confirmPasswordLabel.setForeground(DARK_BLUE);
        formPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(confirmPasswordField, gbc);

        // Password requirements
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        JLabel requirementsLabel = new JLabel("<html><div style='font-size:11px; color:#666;'>" + 
            PasswordUtil.getPasswordRequirements() + "</div></html>");
        formPanel.add(requirementsLabel, gbc);

        formWrapper.add(formPanel, BorderLayout.CENTER);
        return formWrapper;
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
        registerButton.setPreferredSize(new Dimension(150, 40));
        rightPanel.add(registerButton);

        buttonSection.add(leftPanel, BorderLayout.WEST);
        buttonSection.add(rightPanel, BorderLayout.EAST);

        return buttonSection;
    }
}