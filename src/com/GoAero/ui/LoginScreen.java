package com.GoAero.ui;

import com.GoAero.dao.AdminDAO;
import com.GoAero.dao.FlightOwnerDAO;
import com.GoAero.dao.UserDAO;
import com.GoAero.model.Admin;
import com.GoAero.model.FlightOwner;
import com.GoAero.model.SessionManager;
import com.GoAero.model.User;
import com.GoAero.util.PasswordUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Unified login screen for Users, Admins, and Flight Owners with modern UI design
 *
 * Tab indices:
 * - 0: Passenger Login (👤 Passenger)
 * - 1: Admin Login (⚙ Admin)
 * - 2: Airline Login (✈ Airline)
 */
public class LoginScreen extends JFrame {
    // Professional color scheme (consistent with other pages)
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private static final Color ACCENT_ORANGE = new Color(255, 152, 0);
    private static final Color DARK_BLUE = new Color(13, 71, 161);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color HOVER_BLUE = new Color(30, 136, 229);
    private static final Color SELECTED_TAB_BLUE = new Color(179, 229, 252); // light blue for selected tab
    private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    private static final Color BACKGROUND_GRAY = new Color(250, 250, 250);
    private static final Color CARD_WHITE = Color.WHITE;
    private JTabbedPane tabbedPane;
    private JTextField userEmailField, adminUsernameField, ownerCodeField;
    private JPasswordField userPasswordField, adminPasswordField, ownerPasswordField;
    private JButton userLoginButton, adminLoginButton, ownerLoginButton;
    private JButton userRegisterButton, ownerRegisterButton;
    
    private UserDAO userDAO;
    private AdminDAO adminDAO;
    private FlightOwnerDAO flightOwnerDAO;

    public LoginScreen() {
        this(0); // Default to passenger tab
    }

    public LoginScreen(int initialTabIndex) {
        initializeDAOs();
        initializeComponents();
        setupLayout();
        setupEventListeners();

        // Set the initial tab if valid
        if (initialTabIndex >= 0 && initialTabIndex < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(initialTabIndex);
        }
    }

    private void initializeDAOs() {
        userDAO = new UserDAO();
        adminDAO = new AdminDAO();
        flightOwnerDAO = new FlightOwnerDAO();
    }

    private void initializeComponents() {
        setTitle("GoAero - Login");
        setSize(600, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Modern styled tabbed pane with enhanced styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 16));
        tabbedPane.setBackground(CARD_WHITE);
        tabbedPane.setForeground(DARK_BLUE);
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        
        // Enhanced tab styling
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                tabAreaInsets = new Insets(5, 10, 0, 10);
                contentBorderInsets = new Insets(0, 0, 0, 0);
                tabInsets = new Insets(12, 20, 12, 20);
            }
            
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, 
                                           int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isSelected) {
                    g2d.setColor(SELECTED_TAB_BLUE);
                    g2d.fillRoundRect(x, y, w, h + 5, 10, 10);
                    // subtle blue underline for the active tab
                    g2d.setColor(PRIMARY_BLUE);
                    g2d.fillRoundRect(x + 10, y + h + 2, w - 20, 4, 4, 4);
                } else {
                    g2d.setColor(LIGHT_GRAY);
                    g2d.fillRoundRect(x, y, w, h, 10, 10);
                }
                g2d.dispose();
            }
            
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // Custom content border
                g.setColor(PRIMARY_BLUE);
                g.fillRect(0, 0, tabPane.getWidth(), 3);
            }
        });

        // User login components with modern styling and placeholders
        userEmailField = createStyledTextFieldWithPlaceholder(20, "📧 Enter your email address");
        userPasswordField = createStyledPasswordFieldWithPlaceholder(20, "🔒 Enter your password");
        userLoginButton = createStyledButton("🔑 Login", PRIMARY_BLUE, Color.WHITE, 14);
        userRegisterButton = createStyledButton("📝 Register", SUCCESS_GREEN, Color.WHITE, 14);

        // Admin login components with modern styling and placeholders
        adminUsernameField = createStyledTextFieldWithPlaceholder(20, "👤 Enter admin username");
        adminPasswordField = createStyledPasswordFieldWithPlaceholder(20, "🔒 Enter admin password");
        adminLoginButton = createStyledButton("🔑 Admin Login", DARK_BLUE, Color.WHITE, 14);

        // Flight Owner login components with modern styling and placeholders
        ownerCodeField = createStyledTextFieldWithPlaceholder(20, "🏢 Enter company code");
        ownerPasswordField = createStyledPasswordFieldWithPlaceholder(20, "🔒 Enter company password");
        ownerLoginButton = createStyledButton("🔑 Airline Login", ACCENT_ORANGE, Color.WHITE, 14);
        ownerRegisterButton = createStyledButton("🏢 Register Company", SUCCESS_GREEN, Color.WHITE, 14);
    }

    @SuppressWarnings("unused")
    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 45));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        // Add focus effects
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                Color borderColor = field.getText().trim().isEmpty() ? LIGHT_GRAY : new Color(200, 200, 200);
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 2),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
            }
        });
        
        return field;
    }


    private JTextField createStyledTextFieldWithPlaceholder(int columns, String placeholder) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 45));
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

    private JPasswordField createStyledPasswordFieldWithPlaceholder(int columns, String placeholder) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 45));
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

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GRAY);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_GRAY);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header section
        JPanel headerSection = createHeaderSection();
        mainPanel.add(headerSection, BorderLayout.NORTH);

        // Login tabs section
        JPanel tabsSection = createTabsSection();
        mainPanel.add(tabsSection, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderSection() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 25, 0));

        // Title
        JLabel titleLabel = new JLabel("Welcome to GoAero");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Please sign in to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createTabsSection() {
        JPanel tabsPanel = new JPanel(new BorderLayout());
        tabsPanel.setBackground(CARD_WHITE);
        tabsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // User login panel
        JPanel userPanel = createUserLoginPanel();
        tabbedPane.addTab("👤 Passenger", userPanel);

        // Admin login panel
        JPanel adminPanel = createAdminLoginPanel();
        tabbedPane.addTab("⚙ Admin", adminPanel);

        // Flight Owner login panel
        JPanel ownerPanel = createFlightOwnerLoginPanel();
        tabbedPane.addTab("✈ Airline", ownerPanel);

        // Style individual tabs
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setBackgroundAt(i, LIGHT_GRAY);
            tabbedPane.setForegroundAt(i, DARK_BLUE);
        }

        tabsPanel.add(tabbedPane, BorderLayout.CENTER);
        return tabsPanel;
    }

    private JPanel createUserLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_WHITE);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title
        JLabel titleLabel = new JLabel("Passenger Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Email field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setForeground(DARK_BLUE);
        formPanel.add(emailLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(userEmailField, gbc);

        // Password field
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(DARK_BLUE);
        formPanel.add(passwordLabel, gbc);
        gbc.gridy = 3;
        formPanel.add(userPasswordField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(CARD_WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        userLoginButton.setPreferredSize(new Dimension(120, 40));
        userRegisterButton.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(userLoginButton);
        buttonPanel.add(userRegisterButton);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(formPanel);
        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createAdminLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_WHITE);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title
        JLabel titleLabel = new JLabel("Administrator Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(DARK_BLUE);
        formPanel.add(usernameLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(adminUsernameField, gbc);

        // Password field
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(DARK_BLUE);
        formPanel.add(passwordLabel, gbc);
        gbc.gridy = 3;
        formPanel.add(adminPasswordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(CARD_WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        adminLoginButton.setPreferredSize(new Dimension(140, 40));
        buttonPanel.add(adminLoginButton);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(formPanel);
        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createFlightOwnerLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_WHITE);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title
        JLabel titleLabel = new JLabel("Airline Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Company Code field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel codeLabel = new JLabel("Company Code:");
        codeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        codeLabel.setForeground(DARK_BLUE);
        formPanel.add(codeLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(ownerCodeField, gbc);

        // Password field
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(DARK_BLUE);
        formPanel.add(passwordLabel, gbc);
        gbc.gridy = 3;
        formPanel.add(ownerPasswordField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(CARD_WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        ownerLoginButton.setPreferredSize(new Dimension(160, 44));
        ownerRegisterButton.setPreferredSize(new Dimension(180, 44));
        buttonPanel.add(ownerLoginButton);
        buttonPanel.add(ownerRegisterButton);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(formPanel);
        panel.add(buttonPanel);

        return panel;
    }

    private void setupEventListeners() {
        // User login
        userLoginButton.addActionListener(e -> handleUserLogin());
        userRegisterButton.addActionListener(e -> openUserRegistration());

        // Admin login
        adminLoginButton.addActionListener(e -> handleAdminLogin());

        // Flight Owner login
        ownerLoginButton.addActionListener(e -> handleFlightOwnerLogin());
        ownerRegisterButton.addActionListener(e -> openFlightOwnerRegistration());

        // Enter key listeners
        userPasswordField.addActionListener(e -> handleUserLogin());
        adminPasswordField.addActionListener(e -> handleAdminLogin());
        ownerPasswordField.addActionListener(e -> handleFlightOwnerLogin());
    }

    private void handleUserLogin() {
        String email = userEmailField.getText().trim();
        String password = new String(userPasswordField.getPassword());

        // Check if fields contain placeholder text
        if (email.equals("📧 Enter your email address") || email.isEmpty() || 
            String.valueOf(userPasswordField.getPassword()).equals("🔒 Enter your password") || password.isEmpty()) {
            showError("Please enter both email and password.");
            return;
        }

        try {
            User user = userDAO.findByEmail(email);
            if (user != null && PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                SessionManager.getInstance().loginUser(user);
                showSuccess("Login successful! Welcome, " + user.getFullName());
                openUserDashboard();
                dispose();
            } else {
                showError("Invalid email or password.");
            }
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
        }
    }

    private void handleAdminLogin() {
        String username = adminUsernameField.getText().trim();
        String password = new String(adminPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        try {
            Admin admin = adminDAO.findByUsername(username);
            if (admin != null && PasswordUtil.verifyPassword(password, admin.getPasswordHash())) {
                SessionManager.getInstance().loginAdmin(admin);
                showSuccess("Admin login successful! Welcome, " + admin.getUsername());
                openAdminDashboard();
                dispose();
            } else {
                showError("Invalid username or password.");
            }
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
        }
    }

    private void handleFlightOwnerLogin() {
        String companyCode = ownerCodeField.getText().trim().toUpperCase();
        String password = new String(ownerPasswordField.getPassword());

        if (companyCode.isEmpty() || password.isEmpty()) {
            showError("Please enter both company code and password.");
            return;
        }

        try {
            FlightOwner owner = flightOwnerDAO.findByCode(companyCode);
            if (owner != null && PasswordUtil.verifyPassword(password, owner.getPasswordHash())) {
                SessionManager.getInstance().loginFlightOwner(owner);
                showSuccess("Login successful! Welcome, " + owner.getCompanyName());
                openFlightOwnerDashboard();
                dispose();
            } else {
                showError("Invalid company code or password.");
            }
        } catch (Exception e) {
            showError("Login failed: " + e.getMessage());
        }
    }

    private void openUserRegistration() {
        new UserRegistrationDialog(this).setVisible(true);
    }

    private void openFlightOwnerRegistration() {
        new FlightOwnerRegistrationDialog(this).setVisible(true);
    }

    private void openUserDashboard() {
        SwingUtilities.invokeLater(() -> new UserDashboard().setVisible(true));
    }

    private void openAdminDashboard() {
        SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
    }

    private void openFlightOwnerDashboard() {
        SwingUtilities.invokeLater(() -> new FlightOwnerDashboard().setVisible(true));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        // Create a custom undecorated dialog for a nicer success UX
        JDialog dialog = new JDialog(this, "Success", true);
        dialog.setUndecorated(true);

        // Main content with rounded background and subtle shadow
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
        content.setBorder(new EmptyBorder(14, 16, 12, 16));

        // Header (icon + title)
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        header.setOpaque(false);
        JLabel statusIcon = new JLabel("✔");
        statusIcon.setFont(new Font("Arial", Font.BOLD, 22));
        statusIcon.setForeground(SUCCESS_GREEN);
        header.add(statusIcon);
        JLabel title = new JLabel("Success");
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
        msgArea.setBorder(new EmptyBorder(8, 6, 8, 6));
        content.add(msgArea, BorderLayout.CENTER);

        // Buttons panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        JButton ok = createStyledButton("OK", PRIMARY_BLUE, Color.WHITE, 14);
        ok.setPreferredSize(new Dimension(90, 36));
        ok.addActionListener(e -> dialog.dispose());
        btnPanel.add(ok);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(Math.max(360, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(this);

        // Fade-in effect (if supported)
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
            // setOpacity may not be supported on all platforms; ignore silently
        }

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
        } else if (originalColor.equals(DARK_BLUE)) {
            return new Color(33, 91, 181);
        } else {
            // For other colors, create a lighter version
            int r = Math.min(255, originalColor.getRed() + 20);
            int g = Math.min(255, originalColor.getGreen() + 20);
            int b = Math.min(255, originalColor.getBlue() + 20);
            return new Color(r, g, b);
        }
    }

    public void clearFields() {
        userEmailField.setText("📧 Enter your email address");
        userEmailField.setForeground(Color.GRAY);
        userPasswordField.setText("🔒 Enter your password");
        userPasswordField.setForeground(Color.GRAY);
        userPasswordField.setEchoChar((char) 0);
        
        adminUsernameField.setText("👤 Enter admin username");
        adminUsernameField.setForeground(Color.GRAY);
        adminPasswordField.setText("🔒 Enter admin password");
        adminPasswordField.setForeground(Color.GRAY);
        adminPasswordField.setEchoChar((char) 0);
        
        ownerCodeField.setText("🏢 Enter company code");
        ownerCodeField.setForeground(Color.GRAY);
        ownerPasswordField.setText("🔒 Enter company password");
        ownerPasswordField.setForeground(Color.GRAY);
        ownerPasswordField.setEchoChar((char) 0);
    }
}
