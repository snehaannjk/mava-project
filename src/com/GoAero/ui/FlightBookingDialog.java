package com.GoAero.ui;

import com.GoAero.dao.BookingDAO;
import com.GoAero.dao.FlightDAO;
import com.GoAero.model.Booking;
import com.GoAero.model.Flight;
import com.GoAero.model.SessionManager;
import com.GoAero.model.User;
import com.GoAero.util.PNRGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

/**
 * Dialog for booking a selected flight with modern UI design
 */
public class FlightBookingDialog extends JDialog {
    // Professional color scheme (consistent with other pages)
    private static final Color DARK_BLUE = new Color(13, 71, 161);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    private static final Color BACKGROUND_GRAY = new Color(250, 250, 250);
    private static final Color CARD_WHITE = Color.WHITE;
    private Flight selectedFlight;
    private User currentUser;
    private BookingDAO bookingDAO;
    private FlightDAO flightDAO;
    
    private JLabel flightInfoLabel, priceLabel, passengerInfoLabel;
    private JButton confirmBookingButton, cancelButton;

    public FlightBookingDialog(Frame parent, Flight flight) {
        super(parent, "Book Flight", true);
        this.selectedFlight = flight;
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        this.bookingDAO = new BookingDAO();
        this.flightDAO = new FlightDAO();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeComponents() {
        setSize(600, 550);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Flight information with modern styling
        String flightInfo = String.format(
            "<html><div style='font-family: Arial; padding: 10px;'>" +
            "<h3 style='color: #0D47A1; margin-bottom: 15px;'>✈ Flight Details</h3>" +
            "<div style='line-height: 1.6;'>" +
            "<b>Flight:</b> %s (%s)<br>" +
            "<b>Airline:</b> %s<br>" +
            "<b>Route:</b> %s<br>" +
            "<b>Departure:</b> %s<br>" +
            "<b>Arrival:</b> %s<br>" +
            "<b>Available Seats:</b> %d" +
            "</div></div></html>",
            selectedFlight.getFlightCode(),
            selectedFlight.getFlightName(),
            selectedFlight.getCompanyName(),
            selectedFlight.getFullRoute(),
            selectedFlight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            selectedFlight.getDestinationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            selectedFlight.getAvailableSeats()
        );
        flightInfoLabel = new JLabel(flightInfo);
        flightInfoLabel.setVerticalAlignment(SwingConstants.TOP);

        // Price information with modern styling
        priceLabel = new JLabel(String.format(
            "<html><div style='text-align: center; font-family: Arial;'>" +
            "<h2 style='color: #4CAF50; margin: 10px 0;'>💰 Total Price: ₹%.2f</h2>" +
            "</div></html>",
            selectedFlight.getPrice()
        ));
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Passenger information with modern styling
        String passengerInfo = String.format(
            "<html><div style='font-family: Arial; padding: 10px;'>" +
            "<h3 style='color: #0D47A1; margin-bottom: 15px;'>👤 Passenger Information</h3>" +
            "<div style='line-height: 1.6;'>" +
            "<b>Name:</b> %s<br>" +
            "<b>Email:</b> %s<br>" +
            "<b>Phone:</b> %s" +
            "</div></div></html>",
            currentUser.getFullName(),
            currentUser.getEmail(),
            currentUser.getPhone() != null ? currentUser.getPhone() : "Not provided"
        );
        passengerInfoLabel = new JLabel(passengerInfo);
        passengerInfoLabel.setVerticalAlignment(SwingConstants.TOP);

        // Modern styled buttons
        confirmBookingButton = createStyledButton("✅ Confirm Booking", SUCCESS_GREEN, Color.WHITE, 16);
        confirmBookingButton.setPreferredSize(new Dimension(180, 45));

        cancelButton = createStyledButton("❌ Cancel", new Color(108, 117, 125), Color.WHITE, 14);
        cancelButton.setPreferredSize(new Dimension(120, 45));
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
        JLabel titleLabel = new JLabel("Flight Booking");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(DARK_BLUE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Review your booking details");
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

        // Flight info card
        JPanel flightCard = createInfoCard(flightInfoLabel);
        contentPanel.add(flightCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // Price panel
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pricePanel.setBackground(CARD_WHITE);
        pricePanel.add(priceLabel);
        contentPanel.add(pricePanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Passenger info card
        JPanel passengerCard = createInfoCard(passengerInfoLabel);
        contentPanel.add(passengerCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // Terms and conditions
        JLabel termsLabel = new JLabel(
            "<html><div style='text-align: center; font-family: Arial; color: #666;'>" +
            "<small><i>📋 By confirming this booking, you agree to the terms and conditions.<br>" +
            "Your booking will be confirmed and a PNR will be generated.</i></small>" +
            "</div></html>"
        );
        termsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(termsLabel);

        return contentPanel;
    }

    private JPanel createInfoCard(JLabel infoLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(248, 249, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_GRAY, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        card.add(infoLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createButtonSection() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BACKGROUND_GRAY);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmBookingButton);

        return buttonPanel;
    }

    private void setupEventListeners() {
        confirmBookingButton.addActionListener(e -> confirmBooking());
        cancelButton.addActionListener(e -> dispose());
    }

    private void confirmBooking() {
        // Disable button to prevent double-clicking
        confirmBookingButton.setEnabled(false);
        
        try {
            // Check if flight still has available seats
            int currentAvailableSeats = flightDAO.getAvailableSeats(selectedFlight.getFlightId());
            if (currentAvailableSeats <= 0) {
                showError("Sorry, this flight is now fully booked.");
                return;
            }

            // Generate unique PNR
            String pnr;
            do {
                pnr = PNRGenerator.generatePNRWithAirline(selectedFlight.getCompanyCode());
            } while (bookingDAO.pnrExists(pnr));

            // Create booking
            Booking booking = new Booking();
            booking.setUserId(currentUser.getUserId());
            booking.setFlightId(selectedFlight.getFlightId());
            booking.setDepartureAirportId(selectedFlight.getDepartureAirportId());
            booking.setDestinationAirportId(selectedFlight.getDestinationAirportId());
            booking.setDepartureTime(selectedFlight.getDepartureTime());
            booking.setDestinationTime(selectedFlight.getDestinationTime());
            booking.setPnr(pnr);
            booking.setDateOfDeparture(selectedFlight.getDepartureTime().toLocalDate());
            booking.setDateOfDestination(selectedFlight.getDestinationTime().toLocalDate());
            booking.setAmount(selectedFlight.getPrice());
            booking.setPaymentStatus(Booking.PaymentStatus.PENDING);
            booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);

            // Save booking to database
            Booking savedBooking = bookingDAO.create(booking);
            
            if (savedBooking != null) {
                showBookingConfirmation(savedBooking);
                dispose();
            } else {
                showError("Booking failed. Please try again.");
            }

        } catch (Exception e) {
            showError("Booking failed: " + e.getMessage());
        } finally {
            confirmBookingButton.setEnabled(true);
        }
    }

    private void showBookingConfirmation(Booking booking) {
        String confirmationMessage = String.format(
            "Booking Confirmed!\n\n" +
            "PNR: %s\n" +
            "Flight: %s\n" +
            "Route: %s\n" +
            "Departure: %s\n" +
            "Amount: ₹%.2f\n\n" +
            "Please save your PNR for future reference.\n" +
            "You can view your booking details in 'My Bookings'.",
            booking.getPnr(),
            selectedFlight.getFlightCode(),
            selectedFlight.getFullRoute(),
            selectedFlight.getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            booking.getAmount()
        );

        JOptionPane.showMessageDialog(
            this,
            confirmationMessage,
            "Booking Confirmed",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Booking Error", JOptionPane.ERROR_MESSAGE);
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
        if (originalColor.equals(SUCCESS_GREEN)) {
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