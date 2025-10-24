package com.GoAero.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LandingPage extends JFrame {

    // Professional color scheme
    private static final Color PRIMARY_BLUE = new Color(25, 118, 210);
    private static final Color ACCENT_ORANGE = new Color(255, 152, 0);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color HOVER_BLUE = new Color(30, 136, 229);

    // UI Components
    private JButton bookTicketButton;
    private JButton airlineLoginButton;
    private JLabel adminLoginLabel;

    public LandingPage() {
        // Frame setup
        setTitle("GoAero - Flight Booking System");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setResizable(false);

        // Use a background panel with overlay
        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new BorderLayout());
        setContentPane(mainPanel);

        // Create main content panel with semi-transparent overlay
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();

        // Header section
        JPanel headerPanel = createHeaderPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 40, 0);
        contentPanel.add(headerPanel, gbc);

        // Main action buttons section
        JPanel buttonPanel = createButtonPanel();
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 60, 0);
        contentPanel.add(buttonPanel, gbc);

        // Footer section with login options
        JPanel footerPanel = createFooterPanel();
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.PAGE_END;
        gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(footerPanel, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // --- Action Listeners ---

        // Action for the Book Ticket button - opens LoginScreen with Passenger tab (index 0)
        bookTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the Login screen with Passenger tab selected
                new LoginScreen(0).setVisible(true);
                // Hide the landing page
                LandingPage.this.setVisible(false);
            }
        });

        // Action for the Airline Login button - opens LoginScreen with Airline tab (index 2)
        airlineLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the Login screen with Airline tab selected
                LoginScreen loginScreen = new LoginScreen(2);
                loginScreen.setVisible(true);
                // Hide the landing page
                LandingPage.this.setVisible(false);
            }
        });

        // Mouse listener for the Admin Login label - opens LoginScreen with Admin tab (index 1)
        adminLoginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open the Login screen with Admin tab selected
                LoginScreen loginScreen = new LoginScreen(1);
                loginScreen.setVisible(true);
                // Hide the landing page
                LandingPage.this.setVisible(false);
            }
        });
    }

    /**
     * Creates the header panel with the main title and tagline
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        // Main title
        JLabel titleLabel = new JLabel("GoAero");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 60));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add shadow effect
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 0, 5, 0),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Tagline
        JLabel taglineLabel = new JLabel("Your Journey to the Skies Begins Here");
        taglineLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        taglineLabel.setForeground(new Color(220, 220, 220));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(taglineLabel);

        return headerPanel;
    }

    /**
     * Creates the main button panel with primary actions
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        // Book Flight Button (Primary Action)
        bookTicketButton = createStyledButton("✈ Book a Flight", PRIMARY_BLUE, Color.WHITE, 20);
        bookTicketButton.setPreferredSize(new Dimension(300, 60));
        bookTicketButton.setMaximumSize(new Dimension(300, 60));
        bookTicketButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Airline Login Button (Secondary Action)
        airlineLoginButton = createStyledButton("🏢 Airline Partner Login", ACCENT_ORANGE, Color.WHITE, 16);
        airlineLoginButton.setPreferredSize(new Dimension(300, 45));
        airlineLoginButton.setMaximumSize(new Dimension(300, 45));
        airlineLoginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(bookTicketButton);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(airlineLoginButton);

        return buttonPanel;
    }

    /**
     * Creates the footer panel with admin login option
     */
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Admin Login Label (styled as a clickable link)
        adminLoginLabel = new JLabel("<html><u>System Administrator Access</u></html>");
        adminLoginLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        adminLoginLabel.setForeground(LIGHT_GRAY);
        adminLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect for admin login
        adminLoginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                adminLoginLabel.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                adminLoginLabel.setForeground(LIGHT_GRAY);
            }
        });

        footerPanel.add(adminLoginLabel);
        return footerPanel;
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
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Add hover effects
        Color originalBg = bgColor;
        Color hoverColor = bgColor == PRIMARY_BLUE ? HOVER_BLUE : 
                          bgColor == ACCENT_ORANGE ? new Color(255, 167, 38) : bgColor;

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

    // A custom panel for drawing a background image with gradient overlay
    static class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        private boolean imageLoaded = false;

        public BackgroundPanel() {
            try {
                // Create a solid aviation-themed background as fallback
                setBackground(new Color(0, 51, 102)); // Deep blue aviation color
                
                // Try to load a background image (placeholder for now)
                backgroundImage = new ImageIcon(new java.net.URL("https://images.unsplash.com/photo-1436491865332-7a61a109cc05?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1200&q=80")).getImage();
                imageLoaded = true;
            } catch (Exception e) {
                System.out.println("Background image could not be loaded, using fallback color");
                // Fallback to gradient background
                imageLoaded = false;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            // Enable antialiasing for smoother graphics
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (imageLoaded && backgroundImage != null) {
                // Draw the background image, scaling it to cover the entire panel
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
            
            // Add a gradient overlay for better text readability
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(0, 51, 102, 180),  // Semi-transparent dark blue at top
                0, getHeight(), new Color(25, 118, 210, 120)  // Semi-transparent lighter blue at bottom
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            g2d.dispose();
        }
    }
}
