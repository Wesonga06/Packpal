/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package packpal;

import javax.swing.*;
import views.WelcomeView;

public class Main {
    public static void main(String[] args) {
        // Set system look and feel - MORE SPECIFIC APPROACH
        try {
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
            // Continue with default look and feel
        }
        
        // Create and show the WELCOME screen
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WelcomeView welcomeView = new WelcomeView();
                welcomeView.setVisible(true);
            }
        });
    }
}
