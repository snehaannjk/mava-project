package com.GoAero.ui;

import com.GoAero.dao.AirportDAO;
import com.GoAero.model.Airport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Panel for managing airports in the admin dashboard with modern UI design
 */
public class AirportManagementPanel extends JPanel {
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
    private JTable airportsTable;
    private DefaultTableModel tableModel;
    private JButton addAirportButton, editAirportButton, deleteAirportButton, refreshButton;
    private JTextField searchField;
    private JButton searchButton;
    private AirportDAO airportDAO;
    private List<Airport> airports;

    public AirportManagementPanel() {
        airportDAO = new AirportDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadAirports();
    }

    private void initializeComponents() {
        // Modern table setup (removed ID column for cleaner look)
        String[] columnNames = {"Code", "Airport Name", "City", "Country"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        airportsTable = new JTable(tableModel);
        airportsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        airportsTable.setFont(new Font("Arial", Font.PLAIN, 13));
        airportsTable.setRowHeight(45);
        airportsTable.setGridColor(LIGHT_GRAY);
        airportsTable.setSelectionBackground(new Color(230, 240, 255));
        airportsTable.setSelectionForeground(DARK_BLUE);
        airportsTable.setShowVerticalLines(true);
        airportsTable.setShowHorizontalLines(true);

        // Style table header
        airportsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        airportsTable.getTableHeader().setBackground(DARK_BLUE);
        airportsTable.getTableHeader().setForeground(Color.WHITE);
        airportsTable.getTableHeader().setPreferredSize(new Dimension(0, 50));

        airportsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Set optimized column widths (removed ID column)
        airportsTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Code
        airportsTable.getColumnModel().getColumn(1).setPreferredWidth(300); // Airport Name
        airportsTable.getColumnModel().getColumn(2).setPreferredWidth(180); // City
        airportsTable.getColumnModel().getColumn(3).setPreferredWidth(180); // Country

        // Add custom cell renderer for airport codes
        airportsTable.getColumnModel().getColumn(0).setCellRenderer(new AirportCodeCellRenderer());

        // Modern styled buttons with icons
        addAirportButton = createStyledButton("🛫 Add Airport", PRIMARY_BLUE, Color.WHITE, 14);
        editAirportButton = createStyledButton("✏ Edit Airport", ACCENT_ORANGE, Color.WHITE, 14);
        deleteAirportButton = createStyledButton("🗑 Delete Airport", DANGER_RED, Color.WHITE, 14);
        refreshButton = createStyledButton("🔄 Refresh", SUCCESS_GREEN, Color.WHITE, 14);

        // Modern search components
        searchField = createStyledTextField("Search airports by code, name, city, or country...");
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
        addAirportButton.addActionListener(e -> addAirport());
        editAirportButton.addActionListener(e -> editAirport());
        deleteAirportButton.addActionListener(e -> deleteAirport());
        refreshButton.addActionListener(e -> loadAirports());
        searchButton.addActionListener(e -> searchAirports());
        
        // Enter key on search field
        searchField.addActionListener(e -> searchAirports());

        // Double-click to edit
        airportsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editAirport();
                }
            }
        });
    }

    private void loadAirports() {
        try {
            airports = airportDAO.findAll();
            displayAirports(airports);
            updateInfoPanel();
        } catch (Exception e) {
            System.out.println("Failed to load airports: " + e.getMessage());
        }
    }

    private void displayAirports(List<Airport> airportList) {
        // Clear existing data
        tableModel.setRowCount(0);

        // Add airports to table (removed ID column)
        for (Airport airport : airportList) {
            Object[] row = {
                airport.getAirportCode(),
                airport.getAirportName(),
                airport.getCity(),
                airport.getCountry()
            };
            tableModel.addRow(row);
        }

        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean hasSelection = airportsTable.getSelectedRow() != -1;
        editAirportButton.setEnabled(hasSelection);
        deleteAirportButton.setEnabled(hasSelection);
    }

    private void updateInfoPanel() {
        // Update the info label in the south panel
        Component[] components = ((JPanel) getComponent(2)).getComponents();
        if (components.length > 0 && components[0] instanceof JLabel) {
            ((JLabel) components[0]).setText("Total Airports: " + (airports != null ? airports.size() : 0));
        }
    }

    private void addAirport() {
        AdminAirportDialog dialog = new AdminAirportDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            null, 
            airportDAO
        );
        dialog.setVisible(true);
        
        if (dialog.isDataChanged()) {
            loadAirports();
        }
    }

    private void editAirport() {
        int selectedRow = airportsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an airport to edit.");
            return;
        }

        Airport selectedAirport = airports.get(selectedRow);
        AdminAirportDialog dialog = new AdminAirportDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            selectedAirport, 
            airportDAO
        );
        dialog.setVisible(true);
        
        if (dialog.isDataChanged()) {
            loadAirports();
        }
    }

    private void deleteAirport() {
        int selectedRow = airportsTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select an airport to delete.");
            return;
        }

        Airport selectedAirport = airports.get(selectedRow);
        
        int choice = JOptionPane.showConfirmDialog(
            this,
            String.format("Are you sure you want to delete airport '%s (%s)'?\n\nThis action cannot be undone.", 
                selectedAirport.getAirportName(), selectedAirport.getAirportCode()),
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = airportDAO.delete(selectedAirport.getAirportId());
                if (success) {
                    showSuccess("Airport deleted successfully.");
                    loadAirports();
                } else {
                    showError("Failed to delete airport. It may be referenced by existing flights.");
                }
            } catch (Exception e) {
                showError("Deletion failed: " + e.getMessage());
            }
        }
    }

    private void searchAirports() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty() || searchTerm.equals("Search airports by code, name, city, or country...")) {
            displayAirports(airports);
            return;
        }

        // Filter airports based on search term
        List<Airport> filteredAirports = airports.stream()
            .filter(airport ->
                airport.getAirportCode().toLowerCase().contains(searchTerm.toLowerCase()) ||
                airport.getAirportName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                airport.getCity().toLowerCase().contains(searchTerm.toLowerCase()) ||
                airport.getCountry().toLowerCase().contains(searchTerm.toLowerCase())
            )
            .collect(java.util.stream.Collectors.toList());

        displayAirports(filteredAirports);
    }

    private void showError(String message) {
        // Create a custom styled error dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Airport Management Error", true);
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
        JLabel successIcon = new JLabel("🛫✅");
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

        JLabel titleLabel = new JLabel("🛫 Airport Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Manage airport locations and transportation hubs");
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
        JLabel tableTitle = new JLabel("Global Airport Network");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setForeground(DARK_BLUE);
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(airportsTable);
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
        
        addAirportButton.setPreferredSize(new Dimension(140, 40));
        editAirportButton.setPreferredSize(new Dimension(140, 40));
        deleteAirportButton.setPreferredSize(new Dimension(150, 40));
        refreshButton.setPreferredSize(new Dimension(110, 40));
        
        buttonsPanel.add(addAirportButton);
        buttonsPanel.add(editAirportButton);
        buttonsPanel.add(deleteAirportButton);
        buttonsPanel.add(refreshButton);

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setBackground(BACKGROUND_GRAY);
        JLabel infoLabel = new JLabel("Total Airports: " + (airports != null ? airports.size() : 0));
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
     * Custom cell renderer for airport code column
     */
    private static class AirportCodeCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null) {
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(new Font("Arial", Font.BOLD, 12));

                if (!isSelected) {
                    // Color code based on airport code characteristics
                    String code = value.toString();
                    if (code.length() == 3) {
                        setBackground(new Color(232, 245, 255));
                        setForeground(new Color(13, 71, 161));
                    } else {
                        setBackground(new Color(255, 248, 225));
                        setForeground(new Color(230, 81, 0));
                    }
                }
            }

            return this;
        }
    }
}