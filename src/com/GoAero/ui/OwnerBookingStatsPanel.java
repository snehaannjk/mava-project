<<<<<<< HEAD
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
=======
package com.GoAero.ui;

import com.GoAero.dao.BookingDAO;
import com.GoAero.dao.FlightDAO;
import com.GoAero.model.Booking;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Panel for flight owners to view booking statistics for their flights with modern analytics UI
 */
public class OwnerBookingStatsPanel extends JPanel {
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
    private static final Color WARNING_ORANGE = new Color(255, 193, 7);
    private FlightOwner currentOwner;
    private FlightDAO flightDAO;
    private BookingDAO bookingDAO;
    
    private JTable flightStatsTable;
    private DefaultTableModel tableModel;
    private JLabel totalFlightsLabel, totalBookingsLabel, totalRevenueLabel;
    private JLabel confirmedBookingsLabel, pendingBookingsLabel, cancelledBookingsLabel;
    private JButton refreshButton;

    public OwnerBookingStatsPanel() {
        currentOwner = SessionManager.getInstance().getCurrentFlightOwner();
        if (currentOwner == null) {
            JLabel errorLabel = new JLabel("Access denied. Please login as a flight owner.");
            add(errorLabel);
            return;
        }
        
        flightDAO = new FlightDAO();
        bookingDAO = new BookingDAO();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadStatistics();
    }

    private void initializeComponents() {
        // Enhanced summary labels with color coding
        totalFlightsLabel = createDataLabel("0", PRIMARY_BLUE);
        totalBookingsLabel = createDataLabel("0", DARK_BLUE);
        totalRevenueLabel = createDataLabel("₹0.00", SUCCESS_GREEN);
        confirmedBookingsLabel = createDataLabel("0", SUCCESS_GREEN);
        pendingBookingsLabel = createDataLabel("0", WARNING_ORANGE);
        cancelledBookingsLabel = createDataLabel("0", DANGER_RED);
        
        // Modern styled refresh button
        refreshButton = createStyledButton("🔄 Refresh Analytics", PRIMARY_BLUE, Color.WHITE, 14);
        refreshButton.setPreferredSize(new Dimension(180, 40));
        refreshButton.setToolTipText("Refresh booking statistics and analytics");

        // Enhanced table setup for flight-wise statistics
        String[] columnNames = {"Flight Code", "Route", "Departure", "Capacity", "Bookings", "Available", "Occupancy %", "Revenue"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        flightStatsTable = new JTable(tableModel);
        flightStatsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightStatsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        flightStatsTable.setRowHeight(45);
        flightStatsTable.setGridColor(LIGHT_GRAY);
        flightStatsTable.setSelectionBackground(new Color(230, 240, 255));
        flightStatsTable.setSelectionForeground(DARK_BLUE);
        flightStatsTable.setShowVerticalLines(true);
        flightStatsTable.setShowHorizontalLines(true);

        // Style table header
        flightStatsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        flightStatsTable.getTableHeader().setBackground(DARK_BLUE);
        flightStatsTable.getTableHeader().setForeground(Color.WHITE);
        flightStatsTable.getTableHeader().setPreferredSize(new Dimension(0, 50));

        // Set optimized column widths
        flightStatsTable.getColumnModel().getColumn(0).setPreferredWidth(110); // Flight Code
        flightStatsTable.getColumnModel().getColumn(1).setPreferredWidth(220); // Route
        flightStatsTable.getColumnModel().getColumn(2).setPreferredWidth(130); // Departure
        flightStatsTable.getColumnModel().getColumn(3).setPreferredWidth(90);  // Capacity
        flightStatsTable.getColumnModel().getColumn(4).setPreferredWidth(90);  // Bookings
        flightStatsTable.getColumnModel().getColumn(5).setPreferredWidth(90);  // Available
        flightStatsTable.getColumnModel().getColumn(6).setPreferredWidth(110); // Occupancy
        flightStatsTable.getColumnModel().getColumn(7).setPreferredWidth(110); // Revenue

        // Add custom cell renderers
        flightStatsTable.getColumnModel().getColumn(6).setCellRenderer(new OccupancyCellRenderer());
        flightStatsTable.getColumnModel().getColumn(7).setCellRenderer(new RevenueCellRenderer());
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

        // Content section with analytics cards
        JPanel contentSection = createContentSection();
        mainPanel.add(contentSection, BorderLayout.CENTER);

        // Action section
        JPanel actionSection = createActionSection();
        mainPanel.add(actionSection, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        refreshButton.addActionListener(e -> loadStatistics());
    }

    private void loadStatistics() {
        try {
            // Load flights for this owner
            List<Flight> flights = flightDAO.findByCompanyId(currentOwner.getOwnerId());
            
            // Load all bookings
            List<Booking> allBookings = bookingDAO.findAll();
            
            // Filter bookings for this owner's flights
            List<Integer> flightIds = flights.stream()
                .map(Flight::getFlightId)
                .collect(Collectors.toList());
            
            List<Booking> ownerBookings = allBookings.stream()
                .filter(booking -> flightIds.contains(booking.getFlightId()))
                .collect(Collectors.toList());

            // Update summary statistics
            updateSummaryStatistics(flights, ownerBookings);
            
            // Update flight-wise table
            updateFlightStatsTable(flights, ownerBookings);
            
        } catch (Exception e) {
            System.out.println("Failed to load statistics: " + e.getMessage());
        }
    }

    private void updateSummaryStatistics(List<Flight> flights, List<Booking> bookings) {
        totalFlightsLabel.setText(String.valueOf(flights.size()));
        totalBookingsLabel.setText(String.valueOf(bookings.size()));

        int confirmed = 0, pending = 0, cancelled = 0;
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Booking booking : bookings) {
            switch (booking.getBookingStatus()) {
                case CONFIRMED:
                    confirmed++;
                    if (booking.getPaymentStatus() == Booking.PaymentStatus.COMPLETED) {
                        totalRevenue = totalRevenue.add(booking.getAmount());
                    }
                    break;
                case PENDING:
                    pending++;
                    break;
                case CANCELLED:
                    cancelled++;
                    break;
            }
        }

        confirmedBookingsLabel.setText(String.valueOf(confirmed));
        pendingBookingsLabel.setText(String.valueOf(pending));
        cancelledBookingsLabel.setText(String.valueOf(cancelled));
        totalRevenueLabel.setText(String.format("₹%.2f", totalRevenue));
    }

    private void updateFlightStatsTable(List<Flight> flights, List<Booking> allBookings) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        if (flights == null || allBookings == null) {
            showError("Failed to retrieve flight or booking data.");
            return;
        }

        // Group bookings by flight ID
        Map<Integer, List<Booking>> bookingsByFlight = allBookings.stream()
            .collect(Collectors.groupingBy(Booking::getFlightId));

        // Add flight statistics to table
        for (Flight flight : flights) {
            List<Booking> flightBookings = bookingsByFlight.getOrDefault(flight.getFlightId(), List.of());

            int confirmedBookings = (int) flightBookings.stream()
                .filter(b -> b.getBookingStatus() == Booking.BookingStatus.CONFIRMED)
                .count();

            int totalCapacity = flight.getCapacity();
            int availableSeats = totalCapacity - confirmedBookings;
            double occupancyPercent = totalCapacity > 0 ? (confirmedBookings * 100.0 / totalCapacity) : 0.0;

            // Calculate revenue from confirmed bookings with completed payments
            BigDecimal flightRevenue = flightBookings.stream()
                .filter(b -> b.getBookingStatus() == Booking.BookingStatus.CONFIRMED && 
                           b.getPaymentStatus() == Booking.PaymentStatus.COMPLETED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Format route display (using available Flight model methods)
            String route = flight.getRoute() != null ? flight.getRoute() : "Route TBD";
            
            // Format departure time
            String departureTime = flight.getDepartureTime() != null ? 
                flight.getDepartureTime().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")) : "TBD";

            Object[] row = {
                flight.getFlightCode(),
                route,
                departureTime,
                totalCapacity,
                confirmedBookings,
                availableSeats,
                String.format("%.1f%%", occupancyPercent),
                String.format("₹%.2f", flightRevenue)
            };
            tableModel.addRow(row);
        }
    }

    private void showError(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Analytics Error", true);
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
        JLabel errorIcon = new JLabel("📊⚠");
        errorIcon.setFont(new Font("Arial", Font.BOLD, 24));
        errorIcon.setForeground(DANGER_RED);
        header.add(errorIcon);
        JLabel title = new JLabel("Analytics Error");
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

    /**
     * Creates a styled data label with color coding
     */
    private JLabel createDataLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    /**
     * Creates a styled button with hover effects
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

    private JPanel createHeaderSection() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        // Title section with analytics icon
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_GRAY);

        JLabel titleLabel = new JLabel("📊 Booking Analytics & Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Real-time performance metrics for " + currentOwner.getCompanyName());
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        // Status panel with last update time
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBackground(BACKGROUND_GRAY);
        
        JLabel lastUpdated = new JLabel("Last Updated: " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        lastUpdated.setFont(new Font("Arial", Font.ITALIC, 12));
        lastUpdated.setForeground(new Color(120, 120, 120));
        statusPanel.add(lastUpdated);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(statusPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentSection() {
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setBackground(BACKGROUND_GRAY);

        // Analytics cards panel
        JPanel analyticsSection = createAnalyticsCardsSection();
        contentContainer.add(analyticsSection, BorderLayout.NORTH);

        // Flight statistics table section
        JPanel tableSection = createTableSection();
        contentContainer.add(tableSection, BorderLayout.CENTER);

        return contentContainer;
    }

    private JPanel createAnalyticsCardsSection() {
        JPanel cardsContainer = new JPanel(new BorderLayout());
        cardsContainer.setBackground(BACKGROUND_GRAY);
        cardsContainer.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Create 2x3 grid of analytics cards
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 20, 15));
        cardsPanel.setBackground(BACKGROUND_GRAY);

        // Overview cards
        JPanel flightsCard = createAnalyticsCard("✈️ Total Flights", totalFlightsLabel, "Active flight schedules", PRIMARY_BLUE);
        JPanel bookingsCard = createAnalyticsCard("📋 Total Bookings", totalBookingsLabel, "All booking requests", DARK_BLUE);
        JPanel revenueCard = createAnalyticsCard("💰 Total Revenue", totalRevenueLabel, "Confirmed payments", SUCCESS_GREEN);

        // Status cards
        JPanel confirmedCard = createAnalyticsCard("✅ Confirmed", confirmedBookingsLabel, "Successful bookings", SUCCESS_GREEN);
        JPanel pendingCard = createAnalyticsCard("⏳ Pending", pendingBookingsLabel, "Awaiting confirmation", WARNING_ORANGE);
        JPanel cancelledCard = createAnalyticsCard("❌ Cancelled", cancelledBookingsLabel, "Cancelled bookings", DANGER_RED);

        cardsPanel.add(flightsCard);
        cardsPanel.add(bookingsCard);
        cardsPanel.add(revenueCard);
        cardsPanel.add(confirmedCard);
        cardsPanel.add(pendingCard);
        cardsPanel.add(cancelledCard);

        cardsContainer.add(cardsPanel, BorderLayout.CENTER);
        return cardsContainer;
    }

    private JPanel createAnalyticsCard(String title, JLabel valueLabel, String description, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Card header with colored accent
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(DARK_BLUE);

        // Accent bar
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(4, 20));

        headerPanel.add(accentBar, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Value display
        JPanel valuePanel = new JPanel(new BorderLayout());
        valuePanel.setBackground(CARD_WHITE);
        valuePanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valuePanel.add(valueLabel, BorderLayout.CENTER);

        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        descLabel.setForeground(new Color(120, 120, 120));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createTableSection() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(CARD_WHITE);
        tableContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Table title
        JLabel tableTitle = new JLabel("🛫 Flight Performance Details");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setForeground(DARK_BLUE);
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(flightStatsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        scrollPane.setPreferredSize(new Dimension(0, 350));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableContainer.add(tableTitle, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        return tableContainer;
    }

    private JPanel createActionSection() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionPanel.setBackground(BACKGROUND_GRAY);
        actionPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        refreshButton.setPreferredSize(new Dimension(180, 42));
        actionPanel.add(refreshButton);

        return actionPanel;
    }

    /**
     * Custom cell renderer for occupancy percentage column
     */
    private static class OccupancyCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                String occupancyStr = value.toString().replace("%", "");
                try {
                    double occupancy = Double.parseDouble(occupancyStr);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Arial", Font.BOLD, 12));

                    if (!isSelected) {
                        if (occupancy >= 80.0) {
                            setBackground(new Color(232, 245, 233));
                            setForeground(new Color(27, 94, 32));
                        } else if (occupancy >= 60.0) {
                            setBackground(new Color(255, 248, 225));
                            setForeground(new Color(230, 81, 0));
                        } else if (occupancy >= 30.0) {
                            setBackground(new Color(255, 243, 224));
                            setForeground(new Color(191, 54, 12));
                        } else {
                            setBackground(new Color(255, 235, 238));
                            setForeground(new Color(183, 28, 28));
                        }
                    }
                } catch (NumberFormatException e) {
                    // Handle invalid number format
                }
            }

            return this;
        }
    }

    /**
     * Custom cell renderer for revenue column
     */
    private static class RevenueCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                setHorizontalAlignment(SwingConstants.RIGHT);
                setFont(new Font("Arial", Font.BOLD, 12));
                
                String revenueStr = value.toString().replace("₹", "").replace(",", "");
                try {
                    double revenue = Double.parseDouble(revenueStr);
                    
                    if (!isSelected) {
                        if (revenue > 10000) {
                            setForeground(new Color(27, 94, 32));
                            setBackground(new Color(232, 245, 233));
                        } else if (revenue > 5000) {
                            setForeground(new Color(56, 142, 60));
                            setBackground(new Color(237, 247, 237));
                        } else if (revenue > 1000) {
                            setForeground(new Color(230, 81, 0));
                            setBackground(new Color(255, 248, 225));
                        } else {
                            setForeground(new Color(120, 120, 120));
                            setBackground(new Color(245, 245, 245));
                        }
                    }
                } catch (NumberFormatException e) {
                    // Handle invalid number format
                }
            }

            return this;
        }
    }
}
>>>>>>> 3f5f91f7cfd6522e16720d383393a51fecc12eb0
