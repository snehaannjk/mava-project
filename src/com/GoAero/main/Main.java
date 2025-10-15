package com.GoAero.main;

import com.GoAero.ui.LandingPage;
import javax.swing.SwingUtilities;

/**
 * The main entry point for the entire Flight Booking System application.
 */
public class Main {
    public static void main(String[] args) {
        // Swing GUI should be created and updated on the Event Dispatch Thread (EDT).
        // SwingUtilities.invokeLater ensures this.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Create and show the main landing page.
                new LandingPage().setVisible(true);
            }
        });
    }
}
