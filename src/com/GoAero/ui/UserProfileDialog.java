package com.GoAero.ui;

import com.GoAero.dao.UserDAO;
import com.GoAero.model.SessionManager;
import com.GoAero.model.User;
import com.GoAero.util.PasswordUtil;
import com.GoAero.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Dialog for users to view and edit their profile information with modern UI design
 */
public class UserProfileDialog extends JDialog {
    // Professional color scheme (consistent with other pages)
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private static final Color DARK_BLUE = new Color(13, 71, 161);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color HOVER_BLUE = new Color(30, 136, 229);
    private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    private static final Color BACKGROUND_GRAY = new Color(250, 250, 250);
    private static final Color CARD_WHITE = Color.WHITE;
    private User currentUser;
    private UserDAO userDAO;
    
    private JTextField firstNameField, lastNameField, emailField, phoneField, dobField;
    private JPasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    private JButton saveButton, cancelButton, changePasswordButton;
    private JPanel passwordPanel;
    private boolean isPasswordChangeMode = false;

    public UserProfileDialog(Frame parent) {
        super(parent, "My Profile", true);
        currentUser = SessionManager.getInstance().getCurrentUser();
        userDAO = new UserDAO();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadUserData();
    }

    private void initializeComponents() {
        setSize(850, 800);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Modern styled text fields
        firstNameField = createStyledTextField(20);
        lastNameField = createStyledTextField(20);
        emailField = createStyledTextField(20);
        phoneField = createStyledTextField(20);
        dobField = createStyledTextField(20);

        // Modern styled password fields
        currentPasswordField = createStyledPasswordField(20);
        newPasswordField = createStyledPasswordField(20);
        confirmPasswordField = createStyledPasswordField(20);

        // Modern styled buttons
        saveButton = createStyledButton("💾 Save Changes", SUCCESS_GREEN, Color.WHITE, 14);
        saveButton.setPreferredSize(new Dimension(150, 40));

        cancelButton = createStyledButton("❌ Cancel", new Color(108, 117, 125), Color.WHITE, 14);
        cancelButton.setPreferredSize(new Dimension(120, 40));

        changePasswordButton = createStyledButton("🔒 Change Password", PRIMARY_BLUE, Color.WHITE, 14);
        changePasswordButton.setPreferredSize(new Dimension(180, 40));
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GRAY);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_GRAY);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

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

    private JPanel createHeaderSection() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Title
        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Manage your account information");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createContentSection() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 25, 20, 25)
        ));

        // Profile information panel
        JPanel profilePanel = createProfilePanel();
        contentPanel.add(profilePanel);

        // Password change panel (initially hidden)
        passwordPanel = createPasswordPanel();
        passwordPanel.setVisible(false);
        contentPanel.add(passwordPanel);

        return contentPanel;
    }

    private JPanel createButtonSection() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setBackground(BACKGROUND_GRAY);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Change password button (left)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(BACKGROUND_GRAY);
        leftPanel.add(changePasswordButton);

        // Save and cancel buttons (right)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(BACKGROUND_GRAY);
        rightPanel.add(cancelButton);
        rightPanel.add(saveButton);

        buttonPanel.add(leftPanel, BorderLayout.WEST);
        buttonPanel.add(rightPanel, BorderLayout.EAST);

        return buttonPanel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Profile Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // First Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Phone
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        // Date of Birth
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dobPanel.add(dobField);
        dobPanel.add(new JLabel(" (YYYY-MM-DD)"));
        panel.add(dobPanel, gbc);

        return panel;
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Change Password"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Current Password
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        panel.add(currentPasswordField, gbc);

        // New Password
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        panel.add(newPasswordField, gbc);

        // Confirm New Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Confirm New Password:"), gbc);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        // Password requirements
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel requirementsLabel = new JLabel("<html><small>" + PasswordUtil.getPasswordRequirements() + "</small></html>");
        requirementsLabel.setForeground(Color.GRAY);
        panel.add(requirementsLabel, gbc);

        return panel;
    }

    private void setupEventListeners() {
        saveButton.addActionListener(e -> saveProfile());
        cancelButton.addActionListener(e -> dispose());
        changePasswordButton.addActionListener(e -> togglePasswordChangeMode());
    }

    private void loadUserData() {
        firstNameField.setText(currentUser.getFirstName());
        lastNameField.setText(currentUser.getLastName());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
        dobField.setText(currentUser.getDateOfBirth() != null ? 
            currentUser.getDateOfBirth().format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
    }

    private void togglePasswordChangeMode() {
        isPasswordChangeMode = !isPasswordChangeMode;
        passwordPanel.setVisible(isPasswordChangeMode);
        
        if (isPasswordChangeMode) {
            changePasswordButton.setText("🔒 Cancel Password Change");
        } else {
            changePasswordButton.setText("🔒 Change Password");
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
            // Update user object
            currentUser.setFirstName(firstNameField.getText().trim());
            currentUser.setLastName(lastNameField.getText().trim());
            currentUser.setEmail(emailField.getText().trim().toLowerCase());
            currentUser.setPhone(ValidationUtil.cleanPhoneNumber(phoneField.getText().trim()));
            
            String dobText = dobField.getText().trim();
            if (!dobText.isEmpty()) {
                currentUser.setDateOfBirth(LocalDate.parse(dobText));
            }

            // Handle password change if in password change mode
            if (isPasswordChangeMode) {
                String newPassword = new String(newPasswordField.getPassword());
                if (!newPassword.isEmpty()) {
                    currentUser.setPasswordHash(PasswordUtil.hashPassword(newPassword));
                }
            }

            // Save to database
            boolean success = userDAO.update(currentUser);
            
            if (success) {
                // Update session
                SessionManager.getInstance().loginUser(currentUser);
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
        // First Name validation
        if (!ValidationUtil.isNotEmpty(firstNameField.getText())) {
            showError("First name is required.");
            firstNameField.requestFocus();
            return false;
        }

        // Last Name validation
        if (!ValidationUtil.isNotEmpty(lastNameField.getText())) {
            showError("Last name is required.");
            lastNameField.requestFocus();
            return false;
        }

        // Email validation
        String email = emailField.getText().trim();
        if (!ValidationUtil.isValidEmail(email)) {
            showError(ValidationUtil.getEmailErrorMessage());
            emailField.requestFocus();
            return false;
        }

        // Check if email already exists for another user
        if (userDAO.emailExists(email, currentUser.getUserId())) {
            showError("An account with this email already exists.");
            emailField.requestFocus();
            return false;
        }

        // Phone validation (optional)
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !ValidationUtil.isValidPhone(phone)) {
            showError(ValidationUtil.getPhoneErrorMessage());
            phoneField.requestFocus();
            return false;
        }

        // Date of Birth validation (optional)
        String dobText = dobField.getText().trim();
        if (!dobText.isEmpty()) {
            try {
                LocalDate dob = LocalDate.parse(dobText);
                if (!ValidationUtil.isValidDateOfBirth(dob)) {
                    showError("Please enter a valid date of birth. You must be at least 12 years old.");
                    dobField.requestFocus();
                    return false;
                }
            } catch (DateTimeParseException e) {
                showError("Please enter date of birth in YYYY-MM-DD format.");
                dobField.requestFocus();
                return false;
            }
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

            if (!PasswordUtil.verifyPassword(currentPassword, currentUser.getPasswordHash())) {
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
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
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
}