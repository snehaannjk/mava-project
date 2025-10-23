package com.GoAero.ui;

import com.GoAero.dao.FlightOwnerDAO;
import com.GoAero.model.FlightOwner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Panel for managing flight owners (airline companies) in the admin dashboard with modern UI design
 */
public class FlightOwnerManagementPanel extends JPanel {
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
    private JTable flightOwnersTable;
    private DefaultTableModel tableModel;
    private JButton addOwnerButton, editOwnerButton, deleteOwnerButton, refreshButton;
    private JTextField searchField;
    private JButton searchButton;
    private FlightOwnerDAO flightOwnerDAO;
    private List<FlightOwner> flightOwners;

    public FlightOwnerManagementPanel() {
        flightOwnerDAO = new FlightOwnerDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadFlightOwners();
    }

    private void initializeComponents() {
        // Modern table setup (removed ID column for cleaner look)
        String[] columnNames = {"Company Code", "Company Name", "Contact Info", "Flight Count"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        flightOwnersTable = new JTable(tableModel);
        flightOwnersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightOwnersTable.setFont(new Font("Arial", Font.PLAIN, 13));
        flightOwnersTable.setRowHeight(45);
        flightOwnersTable.setGridColor(LIGHT_GRAY);
        flightOwnersTable.setSelectionBackground(new Color(230, 240, 255));
        flightOwnersTable.setSelectionForeground(DARK_BLUE);
        flightOwnersTable.setShowVerticalLines(true);
        flightOwnersTable.setShowHorizontalLines(true);

        // Style table header
        flightOwnersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        flightOwnersTable.getTableHeader().setBackground(DARK_BLUE);
        flightOwnersTable.getTableHeader().setForeground(Color.WHITE);
        flightOwnersTable.getTableHeader().setPreferredSize(new Dimension(0, 50));

        flightOwnersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Set optimized column widths (removed ID column)
        flightOwnersTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Company Code
        flightOwnersTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Company Name
        flightOwnersTable.getColumnModel().getColumn(2).setPreferredWidth(220); // Contact Info
        flightOwnersTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Flight Count

        // Add custom cell renderer for flight count column
        flightOwnersTable.getColumnModel().getColumn(3).setCellRenderer(new FlightCountCellRenderer());

        // Modern styled buttons with icons
        addOwnerButton = createStyledButton("✈ Add Airline", PRIMARY_BLUE, Color.WHITE, 14);
        editOwnerButton = createStyledButton("✏ Edit Airline", ACCENT_ORANGE, Color.WHITE, 14);
        deleteOwnerButton = createStyledButton("🗑 Delete Airline", DANGER_RED, Color.WHITE, 14);
        refreshButton = createStyledButton("🔄 Refresh", SUCCESS_GREEN, Color.WHITE, 14);

        // Modern search components
        searchField = createStyledTextField("Search airlines by name, code, or contact...");
        searchButton = createStyledButton("🔍 Search", DARK_BLUE, Color.WHITE, 12);

        updateButtonStates();
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

        // Content section with table
        JPanel contentSection = createContentSection();
        mainPanel.add(contentSection, BorderLayout.CENTER);

        // Action buttons section
        JPanel actionSection = createActionSection();
        mainPanel.add(actionSection, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        addOwnerButton.addActionListener(e -> addFlightOwner());
        editOwnerButton.addActionListener(e -> editFlightOwner());
        deleteOwnerButton.addActionListener(e -> deleteFlightOwner());
        refreshButton.addActionListener(e -> loadFlightOwners());
        searchButton.addActionListener(e -> searchFlightOwners());
        
        // Enter key on search field
        searchField.addActionListener(e -> searchFlightOwners());

        // Double-click to edit
        flightOwnersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editFlightOwner();
                }
            }
        });
    }

    private void loadFlightOwners() {
        try {
            flightOwners = flightOwnerDAO.findAllWithFlightCounts();
            displayFlightOwners(flightOwners);
            updateInfoPanel();
        } catch (Exception e) {
            System.out.println("Failed to load flight owners: " + e.getMessage());
        }
    }

    private void displayFlightOwners(List<FlightOwner> ownerList) {
        // Clear existing data
        tableModel.setRowCount(0);

        // Add flight owners to table (removed ID column)
        for (FlightOwner owner : ownerList) {
            Object[] row = {
                owner.getCompanyCode(),
                owner.getCompanyName(),
                owner.getContactInfo(),
                owner.getFlightCount()
            };
            tableModel.addRow(row);
        }

        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean hasSelection = flightOwnersTable.getSelectedRow() != -1;
        editOwnerButton.setEnabled(hasSelection);
        deleteOwnerButton.setEnabled(hasSelection);
    }

    private void updateInfoPanel() {
        // Update the info label in the south panel
        Component[] components = ((JPanel) getComponent(2)).getComponents();
        if (components.length > 0 && components[0] instanceof JLabel) {
            ((JLabel) components[0]).setText("Total Airlines: " + (flightOwners != null ? flightOwners.size() : 0));
        }
    }

    private void addFlightOwner() {
        AdminFlightOwnerDialog dialog = new AdminFlightOwnerDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            null, 
            flightOwnerDAO
        );
        dialog.setVisible(true);
        
        if (dialog.isDataChanged()) {
            loadFlightOwners();
        }
    }

    private void editFlightOwner() {
        int selectedRow = flightOwnersTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an airline to edit.");
            return;
        }

        FlightOwner selectedOwner = flightOwners.get(selectedRow);
        AdminFlightOwnerDialog dialog = new AdminFlightOwnerDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            selectedOwner, 
            flightOwnerDAO
        );
        dialog.setVisible(true);
        
        if (dialog.isDataChanged()) {
            loadFlightOwners();
        }
    }

    private void deleteFlightOwner() {
        int selectedRow = flightOwnersTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an airline to delete.");
            return;
        }

        FlightOwner selectedOwner = flightOwners.get(selectedRow);
        
        // Create custom styled confirmation dialog
        boolean confirmed = showDeleteConfirmationDialog(selectedOwner);
        
        if (confirmed) {
            try {
                boolean success = flightOwnerDAO.delete(selectedOwner.getOwnerId());
                if (success) {
                    showSuccess("Airline deleted successfully.");
                    loadFlightOwners();
                } else {
                    showError("Failed to delete airline. It may have associated flights or bookings.");
                }
            } catch (Exception e) {
                showError("Deletion failed: " + e.getMessage());
            }
        }
    }

    private void searchFlightOwners() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            displayFlightOwners(flightOwners);
            return;
        }

        // Filter flight owners based on search term
        List<FlightOwner> filteredOwners = flightOwners.stream()
            .filter(owner ->
                owner.getCompanyName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                owner.getCompanyCode().toLowerCase().contains(searchTerm.toLowerCase()) ||
                (owner.getContactInfo() != null && owner.getContactInfo().toLowerCase().contains(searchTerm.toLowerCase()))
            )
            .collect(java.util.stream.Collectors.toList());

        displayFlightOwners(filteredOwners);
    }

    private void showError(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Airline Management Error", true);
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
        JButton ok = createStyledButton("OK", DANGER_RED, Color.WHITE, 14);
        ok.setPreferredSize(new Dimension(90, 36));
        ok.addActionListener(e -> dialog.dispose());
        btnPanel.add(ok);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(Math.max(380, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showSuccess(String message) {
        // Create a custom styled success dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Success", true);
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

        // Header
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
        JButton ok = createStyledButton("Great!", SUCCESS_GREEN, Color.WHITE, 14);
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
     * Creates a styled text field with placeholder support
     */
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(25);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
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
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
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
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
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

        // Title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_GRAY);

        JLabel titleLabel = new JLabel("✈ Airline Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Manage airline companies and flight operators");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(BACKGROUND_GRAY);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 13));
        searchLabel.setForeground(DARK_BLUE);
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentSection() {
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setBackground(CARD_WHITE);
        contentContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Table title
        JLabel tableTitle = new JLabel("Airline Companies");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setForeground(DARK_BLUE);
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(flightOwnersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_GRAY, 1));
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.getViewport().setBackground(Color.WHITE);

        contentContainer.add(tableTitle, BorderLayout.NORTH);
        contentContainer.add(scrollPane, BorderLayout.CENTER);
        
        return contentContainer;
    }

    private JPanel createActionSection() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BorderLayout());
        actionPanel.setBackground(BACKGROUND_GRAY);
        actionPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Action buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonsPanel.setBackground(BACKGROUND_GRAY);
        
        addOwnerButton.setPreferredSize(new Dimension(140, 40));
        editOwnerButton.setPreferredSize(new Dimension(140, 40));
        deleteOwnerButton.setPreferredSize(new Dimension(150, 40));
        refreshButton.setPreferredSize(new Dimension(110, 40));
        
        buttonsPanel.add(addOwnerButton);
        buttonsPanel.add(editOwnerButton);
        buttonsPanel.add(deleteOwnerButton);
        buttonsPanel.add(refreshButton);

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setBackground(BACKGROUND_GRAY);
        JLabel infoLabel = new JLabel("Total Airlines: " + (flightOwners != null ? flightOwners.size() : 0));
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoLabel.setForeground(DARK_BLUE);
        infoLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        infoLabel.setBackground(CARD_WHITE);
        infoLabel.setOpaque(true);
        infoPanel.add(infoLabel);

        actionPanel.add(buttonsPanel, BorderLayout.WEST);
        actionPanel.add(infoPanel, BorderLayout.EAST);

        return actionPanel;
    }

    /**
     * Custom cell renderer for flight count column
     */
    private static class FlightCountCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                int count = Integer.parseInt(value.toString());
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 12));

                if (!isSelected) {
                    if (count > 10) {
                        setBackground(new Color(232, 245, 233));
                        setForeground(new Color(27, 94, 32));
                    } else if (count > 5) {
                        setBackground(new Color(255, 248, 225));
                        setForeground(new Color(230, 81, 0));
                    } else if (count > 0) {
                        setBackground(new Color(255, 235, 238));
                        setForeground(new Color(183, 28, 28));
                    } else {
                        setBackground(new Color(245, 245, 245));
                        setForeground(new Color(120, 120, 120));
                    }
                }
            }

            return this;
        }
    }

    /**
     * Creates a modern styled delete confirmation dialog for airline companies
     */
    private boolean showDeleteConfirmationDialog(FlightOwner flightOwner) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Confirm Airline Deletion", true);
        dialog.setUndecorated(true);
        final boolean[] confirmed = {false};

        // Main content with rounded background and shadow
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 16;
                // shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(4, 8, getWidth() - 8, getHeight() - 8, arc, arc);
                // background with warning gradient
                GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(255, 248, 240));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 12, arc, arc);
                g2.dispose();
            }
        };
        content.setLayout(new BorderLayout());
        content.setBorder(new EmptyBorder(20, 25, 18, 25));

        // Warning header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        header.setOpaque(false);
        JLabel warningIcon = new JLabel("🏢⚠");
        warningIcon.setFont(new Font("Arial", Font.BOLD, 24));
        warningIcon.setForeground(DANGER_RED);
        header.add(warningIcon);
        JLabel title = new JLabel("Delete Airline Company");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(DARK_BLUE);
        header.add(title);

        content.add(header, BorderLayout.NORTH);

        // Airline details and warning message section
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setOpaque(false);
        messagePanel.setBorder(new EmptyBorder(10, 0, 15, 0));

        // Airline identification panel
        JPanel airlineInfoPanel = new JPanel(new BorderLayout());
        airlineInfoPanel.setOpaque(false);
        airlineInfoPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel companyCodeLabel = new JLabel(flightOwner.getCompanyCode());
        companyCodeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        companyCodeLabel.setForeground(PRIMARY_BLUE);
        
        JLabel companyNameLabel = new JLabel(flightOwner.getCompanyName());
        companyNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        companyNameLabel.setForeground(DARK_BLUE);
        
        JLabel contactInfoLabel = new JLabel(flightOwner.getContactInfo() != null ? 
            "Contact: " + flightOwner.getContactInfo() : "Contact: N/A");
        contactInfoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        contactInfoLabel.setForeground(new Color(120, 120, 120));
        
        JLabel flightCountLabel = new JLabel("Active Flights: " + flightOwner.getFlightCount());
        flightCountLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        if (flightOwner.getFlightCount() > 0) {
            flightCountLabel.setForeground(DANGER_RED);
        } else {
            flightCountLabel.setForeground(new Color(120, 120, 120));
        }

        airlineInfoPanel.add(companyCodeLabel, BorderLayout.NORTH);
        airlineInfoPanel.add(companyNameLabel, BorderLayout.CENTER);
        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.add(contactInfoLabel);
        detailsPanel.add(flightCountLabel);
        airlineInfoPanel.add(detailsPanel, BorderLayout.SOUTH);

        JLabel mainMessage = new JLabel("Are you sure you want to delete this airline company?");
        mainMessage.setFont(new Font("Arial", Font.BOLD, 15));
        mainMessage.setForeground(DARK_BLUE);
        mainMessage.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel warningMessage = new JLabel("This action cannot be undone and will permanently:");
        warningMessage.setFont(new Font("Arial", Font.PLAIN, 13));
        warningMessage.setForeground(new Color(120, 120, 120));
        warningMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
        warningMessage.setBorder(new EmptyBorder(8, 0, 5, 0));

        JLabel consequence1 = new JLabel("• Remove airline company from the system");
        consequence1.setFont(new Font("Arial", Font.PLAIN, 13));
        consequence1.setForeground(new Color(120, 120, 120));
        consequence1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel consequence2 = new JLabel("• Delete all associated flight schedules");
        consequence2.setFont(new Font("Arial", Font.PLAIN, 13));
        consequence2.setForeground(DANGER_RED);
        consequence2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel consequence3 = new JLabel("• Cancel all existing passenger bookings");
        consequence3.setFont(new Font("Arial", Font.PLAIN, 13));
        consequence3.setForeground(DANGER_RED);
        consequence3.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel consequence4 = new JLabel("• Revoke airline access credentials");
        consequence4.setFont(new Font("Arial", Font.PLAIN, 13));
        consequence4.setForeground(new Color(120, 120, 120));
        consequence4.setAlignmentX(Component.LEFT_ALIGNMENT);

        messagePanel.add(airlineInfoPanel);
        messagePanel.add(mainMessage);
        messagePanel.add(warningMessage);
        messagePanel.add(consequence1);
        messagePanel.add(consequence2);
        messagePanel.add(consequence3);
        messagePanel.add(consequence4);

        content.add(messagePanel, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        
        JButton deleteBtn = createStyledButton("🗑 Delete Airline", DANGER_RED, Color.WHITE, 14);
        deleteBtn.setPreferredSize(new Dimension(150, 40));
        deleteBtn.addActionListener(e -> {
            confirmed[0] = true;
            dialog.dispose();
        });
        
        JButton cancelBtn = createStyledButton("❌ Cancel", LIGHT_GRAY, DARK_BLUE, 14);
        cancelBtn.setPreferredSize(new Dimension(100, 40));
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(deleteBtn);
        btnPanel.add(cancelBtn);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setSize(Math.max(500, dialog.getWidth()), dialog.getHeight());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        return confirmed[0];
    }
}