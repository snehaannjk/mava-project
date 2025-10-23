package com.GoAero.ui;

import com.GoAero.model.Booking;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

/**
 * Dialog to display detailed booking information with modern UI design
 */
public class BookingDetailsDialog extends JDialog {
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
    private Booking booking;
    private JButton closeButton;

    public BookingDetailsDialog(Frame parent, Booking booking) {
        super(parent, "Booking Details", true);
        this.booking = booking;
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        setSize(650, 750);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Create modern styled close button
        closeButton = createStyledButton("✈ Close", PRIMARY_BLUE, Color.WHITE, 14);
        closeButton.setPreferredSize(new Dimension(120, 40));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GRAY);

        // Main content panel with modern design
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_GRAY);
        mainPanel.setBorder(new EmptyBorder(25, 30, 30, 30));

        // Header section
        JPanel headerSection = createHeaderSection();
        mainPanel.add(headerSection, BorderLayout.NORTH);

        // Content section with booking details
        JPanel contentSection = createContentSection();  
        mainPanel.add(contentSection, BorderLayout.CENTER);

        // Button section
        JPanel buttonSection = createButtonSection();
        mainPanel.add(buttonSection, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
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

        // Title section with booking icon
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_GRAY);

        JLabel titleLabel = new JLabel("📋 Booking Confirmation Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Complete reservation information and travel itinerary");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);

        // PNR highlight panel
        JPanel pnrPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnrPanel.setBackground(BACKGROUND_GRAY);
        
        JLabel pnrBadge = new JLabel("PNR: " + booking.getPnr());
        pnrBadge.setFont(new Font("Arial", Font.BOLD, 16));
        pnrBadge.setForeground(ACCENT_ORANGE);
        pnrBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_ORANGE, 2),
            new EmptyBorder(8, 15, 8, 15)
        ));
        pnrBadge.setBackground(new Color(255, 248, 225));
        pnrBadge.setOpaque(true);
        pnrPanel.add(pnrBadge);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(pnrPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createContentSection() {
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.setBackground(BACKGROUND_GRAY);

        // Create scroll pane for content
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(BACKGROUND_GRAY);

        // Booking Summary Card
        JPanel bookingSummaryCard = createBookingSummaryCard();
        scrollContent.add(bookingSummaryCard);
        scrollContent.add(Box.createVerticalStrut(20));

        // Flight Details Card
        JPanel flightDetailsCard = createFlightDetailsCard();
        scrollContent.add(flightDetailsCard);
        scrollContent.add(Box.createVerticalStrut(20));

        // Passenger Information Card
        JPanel passengerCard = createPassengerCard();
        scrollContent.add(passengerCard);
        scrollContent.add(Box.createVerticalStrut(20));

        // Status and Payment Card
        JPanel statusCard = createStatusCard();
        scrollContent.add(statusCard);

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_GRAY);
        scrollPane.getViewport().setBackground(BACKGROUND_GRAY);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        contentContainer.add(scrollPane, BorderLayout.CENTER);
        return contentContainer;
    }

    private JPanel createBookingSummaryCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 25, 20, 25)
        ));

        // Card header
        JLabel headerLabel = new JLabel("💰 Booking Summary");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(DARK_BLUE);
        headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Content panel
        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 20, 15));
        contentPanel.setBackground(CARD_WHITE);

        // Booking Date
        JLabel bookingDateLabel = new JLabel("Booking Date");
        bookingDateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bookingDateLabel.setForeground(DARK_BLUE);
        contentPanel.add(bookingDateLabel);

        JLabel bookingDateValue = new JLabel(booking.getDateOfBooking().toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        bookingDateValue.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(bookingDateValue);

        // Total Amount
        JLabel amountLabel = new JLabel("Total Amount");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        amountLabel.setForeground(DARK_BLUE);
        contentPanel.add(amountLabel);

        JLabel amountValue = new JLabel(String.format("₹%.2f INR", booking.getAmount()));
        amountValue.setFont(new Font("Arial", Font.BOLD, 16));
        amountValue.setForeground(SUCCESS_GREEN);
        contentPanel.add(amountValue);

        // Booking Reference
        JLabel referenceLabel = new JLabel("Reference Number");
        referenceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        referenceLabel.setForeground(DARK_BLUE);
        contentPanel.add(referenceLabel);

        JLabel referenceValue = new JLabel(booking.getPnr());
        referenceValue.setFont(new Font("Arial", Font.BOLD, 14));
        referenceValue.setForeground(PRIMARY_BLUE);
        contentPanel.add(referenceValue);

        card.add(headerLabel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createFlightDetailsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 25, 20, 25)
        ));

        // Card header
        JLabel headerLabel = new JLabel("✈ Flight Information");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(DARK_BLUE);
        headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Content panel
        JPanel contentPanel = new JPanel(new GridLayout(6, 2, 20, 15));
        contentPanel.setBackground(CARD_WHITE);

        // Flight Code
        JLabel codeLabel = new JLabel("Flight Code");
        codeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        codeLabel.setForeground(DARK_BLUE);
        contentPanel.add(codeLabel);

        JLabel codeValue = new JLabel(booking.getFlightCode());
        codeValue.setFont(new Font("Arial", Font.BOLD, 14));
        codeValue.setForeground(PRIMARY_BLUE);
        contentPanel.add(codeValue);

        // Flight Name
        JLabel nameLabel = new JLabel("Flight Name");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(DARK_BLUE);
        contentPanel.add(nameLabel);

        JLabel nameValue = new JLabel(booking.getFlightName() != null ? booking.getFlightName() : "N/A");
        nameValue.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(nameValue);

        // Airline
        JLabel airlineLabel = new JLabel("Airline");
        airlineLabel.setFont(new Font("Arial", Font.BOLD, 14));
        airlineLabel.setForeground(DARK_BLUE);
        contentPanel.add(airlineLabel);

        JLabel airlineValue = new JLabel(booking.getCompanyName() != null ? booking.getCompanyName() : "N/A");
        airlineValue.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(airlineValue);

        // Route
        JLabel routeLabel = new JLabel("Route");
        routeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        routeLabel.setForeground(DARK_BLUE);
        contentPanel.add(routeLabel);

        JLabel routeValue = new JLabel(booking.getFullRoute());
        routeValue.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(routeValue);

        // Departure
        JLabel depLabel = new JLabel("Departure");
        depLabel.setFont(new Font("Arial", Font.BOLD, 14));
        depLabel.setForeground(DARK_BLUE);
        contentPanel.add(depLabel);

        JLabel depValue = new JLabel(booking.getDepartureTime()
            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        depValue.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(depValue);

        // Arrival
        JLabel arrLabel = new JLabel("Arrival");
        arrLabel.setFont(new Font("Arial", Font.BOLD, 14));
        arrLabel.setForeground(DARK_BLUE);
        contentPanel.add(arrLabel);

        JLabel arrValue = new JLabel(booking.getDestinationTime()
            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        arrValue.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(arrValue);

        card.add(headerLabel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createPassengerCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 25, 20, 25)
        ));

        // Card header
        JLabel headerLabel = new JLabel("👤 Passenger Information");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(DARK_BLUE);
        headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Content panel
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 20, 15));
        contentPanel.setBackground(CARD_WHITE);

        // Passenger Name
        JLabel nameLabel = new JLabel("Passenger Name");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(DARK_BLUE);
        contentPanel.add(nameLabel);

        JLabel nameValue = new JLabel(booking.getUserFullName() != null ? booking.getUserFullName() : "N/A");
        nameValue.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(nameValue);

        // Email
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setForeground(DARK_BLUE);
        contentPanel.add(emailLabel);

        JLabel emailValue = new JLabel(booking.getUserEmail() != null ? booking.getUserEmail() : "N/A");
        emailValue.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(emailValue);

        card.add(headerLabel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createStatusCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(20, 25, 20, 25)
        ));

        // Card header
        JLabel headerLabel = new JLabel("📊 Status Information");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(DARK_BLUE);
        headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Content panel with status badges
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 20, 15));
        contentPanel.setBackground(CARD_WHITE);

        // Booking Status
        JLabel bookingStatusLabel = new JLabel("Booking Status");
        bookingStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bookingStatusLabel.setForeground(DARK_BLUE);
        contentPanel.add(bookingStatusLabel);

        JLabel bookingStatusBadge = new JLabel(booking.getBookingStatus().getDisplayName());
        bookingStatusBadge.setFont(new Font("Arial", Font.BOLD, 12));
        bookingStatusBadge.setHorizontalAlignment(SwingConstants.CENTER);
        bookingStatusBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getStatusColor(booking.getBookingStatus().name()), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        bookingStatusBadge.setForeground(getStatusColor(booking.getBookingStatus().name()));
        bookingStatusBadge.setBackground(getStatusBackgroundColor(booking.getBookingStatus().name()));
        bookingStatusBadge.setOpaque(true);
        contentPanel.add(bookingStatusBadge);

        // Payment Status
        JLabel paymentStatusLabel = new JLabel("Payment Status");
        paymentStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        paymentStatusLabel.setForeground(DARK_BLUE);
        contentPanel.add(paymentStatusLabel);

        JLabel paymentStatusBadge = new JLabel(booking.getPaymentStatus().getDisplayName());
        paymentStatusBadge.setFont(new Font("Arial", Font.BOLD, 12));
        paymentStatusBadge.setHorizontalAlignment(SwingConstants.CENTER);
        paymentStatusBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getPaymentStatusColor(booking.getPaymentStatus().name()), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        paymentStatusBadge.setForeground(getPaymentStatusColor(booking.getPaymentStatus().name()));
        paymentStatusBadge.setBackground(getPaymentStatusBackgroundColor(booking.getPaymentStatus().name()));
        paymentStatusBadge.setOpaque(true);
        contentPanel.add(paymentStatusBadge);

        card.add(headerLabel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private Color getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "CONFIRMED":
                return SUCCESS_GREEN;
            case "CANCELLED":
                return DANGER_RED;
            case "PENDING":
                return WARNING_ORANGE;
            default:
                return LIGHT_GRAY;
        }
    }

    private Color getStatusBackgroundColor(String status) {
        switch (status.toUpperCase()) {
            case "CONFIRMED":
                return new Color(232, 245, 233);
            case "CANCELLED":
                return new Color(255, 235, 238);
            case "PENDING":
                return new Color(255, 248, 225);
            default:
                return new Color(245, 245, 245);
        }
    }

    private Color getPaymentStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "COMPLETED":
                return SUCCESS_GREEN;
            case "FAILED":
                return DANGER_RED;
            case "PENDING":
                return WARNING_ORANGE;
            default:
                return LIGHT_GRAY;
        }
    }

    private Color getPaymentStatusBackgroundColor(String status) {
        switch (status.toUpperCase()) {
            case "COMPLETED":
                return new Color(232, 245, 233);
            case "FAILED":
                return new Color(255, 235, 238);
            case "PENDING":
                return new Color(255, 248, 225);
            default:
                return new Color(245, 245, 245);
        }
    }

    private JPanel createButtonSection() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_GRAY);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        buttonPanel.add(closeButton);

        return buttonPanel;
    }



    private void setupEventListeners() {
        closeButton.addActionListener(e -> dispose());
    }
}