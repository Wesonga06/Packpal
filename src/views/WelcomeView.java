/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views;

import controllers.WelcomeController;
import javax.swing.*;
import java.awt.*;

public class WelcomeView extends JFrame {
    private WelcomeController controller;

    private static final Color PRIMARY_BLUE = new Color(70, 160, 255);

    public WelcomeView() {
        controller = new WelcomeController(this);
        initializeUI();
    }

    private void initializeUI() {
         setTitle("PackPal - Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        // --- Main container ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        // --- Center content ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        // 1️⃣ Header bar (now matches button width)
        RoundedLabel appTitleLabel = new RoundedLabel("PackPal");
        appTitleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        appTitleLabel.setForeground(Color.WHITE);
        appTitleLabel.setBackground(PRIMARY_BLUE);
        appTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        appTitleLabel.setMaximumSize(new Dimension(340, 55)); // matches button width
        appTitleLabel.setPreferredSize(new Dimension(340, 55));
        appTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 2️⃣ Bag image
        JLabel bagImage = new JLabel();
        bagImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        bagImage.setBorder(BorderFactory.createEmptyBorder(35, 0, 25, 0));
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/packpal/assets/bag.JPEG"));
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            bagImage.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            bagImage.setText("👜");
            bagImage.setFont(new Font("Arial", Font.PLAIN, 72));
        }

        // 3️⃣ Welcome text
        JLabel welcomeLabel = new JLabel("Welcome to PackPal", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 22));
        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // 4️⃣ Subtitle
        JLabel subtitleLabel = new JLabel(
            "<html><div style='text-align: center;'>Never forget essential items again!<br>Create smart packing lists for any trip.</div></html>",
            SwingConstants.CENTER
        );
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 40, 0));

        // 5️⃣ Get Started button (blue)
        JButton getStartedButton = createRoundedBlueButton("Get Started");
        getStartedButton.setMaximumSize(new Dimension(340, 50));
        getStartedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        getStartedButton.addActionListener(e -> controller.handleGetStarted());

        // 6️⃣ "I have an account" button (gray)
        JButton haveAccountButton = new JButton("I have an account") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 230)); // light gray
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        haveAccountButton.setOpaque(false);
        haveAccountButton.setContentAreaFilled(false);
        haveAccountButton.setForeground(Color.DARK_GRAY);
        haveAccountButton.setFont(new Font("Arial", Font.BOLD, 16));
        haveAccountButton.setFocusPainted(false);
        haveAccountButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        haveAccountButton.setMaximumSize(new Dimension(340, 50));
        haveAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        haveAccountButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        haveAccountButton.addActionListener(e -> controller.handleHaveAccount());

        // --- Add components ---
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(appTitleLabel);
        centerPanel.add(bagImage);
        centerPanel.add(welcomeLabel);
        centerPanel.add(subtitleLabel);
        centerPanel.add(getStartedButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(haveAccountButton);
        centerPanel.add(Box.createVerticalGlue());

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    // --- Rounded blue button factory ---
    private JButton createRoundedBlueButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_BLUE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void setVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void setDefaultCloseOperation(int EXIT_ON_CLOSE) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void setSize(int i, int i0) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void setLocationRelativeTo(Object object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void setResizable(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    //private void setTitle(String packPal__Welcome) {
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    //}

    private void add(JPanel mainPanel) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }



    // --- Rounded background label ---
    class RoundedLabel extends JLabel {
        public RoundedLabel(String text) {
            super(text, SwingConstants.CENTER);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}

