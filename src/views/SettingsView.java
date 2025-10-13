package packpal.views;

import packpal.models.User;
import packpal.dao.UserDAO;
import javax.swing.*;
import java.awt.*;

public class SettingsView extends JFrame {
    private static final Color PRIMARY_BLUE = new Color(70, 160, 255);
    private User currentUser;
    
    public SettingsView(User user) {
        this.currentUser = user;
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("PackPal - Settings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Settings content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Settings options
        contentPanel.add(createSettingItem("Profile", "Manage your account details", e -> openProfile()));
        contentPanel.add(createDivider());
        
        contentPanel.add(createSettingItem("Notifications", "Configure notification preferences", e -> openNotifications()));
        contentPanel.add(createDivider());
        
        contentPanel.add(createSettingItem("Default Templates", "Set your preferred templates", e -> openDefaultTemplates()));
        contentPanel.add(createDivider());
        
        contentPanel.add(createSettingItem("Share & Export", "Share lists or export data", e -> openShareExport()));
        contentPanel.add(createDivider());
        
        contentPanel.add(createSettingItem("Help & Support", "Get help and contact support", e -> openHelpSupport()));
        contentPanel.add(createDivider());
        
        contentPanel.add(createSettingItem("Privacy Policy", "View our privacy policy", e -> openPrivacyPolicy()));
        contentPanel.add(createDivider());
        
        contentPanel.add(createSettingItem("About PackPal", "App version and information", e -> showAbout()));
        contentPanel.add(createDivider());
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Logout button
        JButton logoutButton = new JButton("Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 53, 69)); // Red color
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutButton.setOpaque(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(340, 50));
        logoutButton.addActionListener(e -> handleLogout());
        
        contentPanel.add(logoutButton);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> dispose());
        
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createSettingItem(String title, String subtitle, java.awt.event.ActionListener action) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.BLACK);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(subtitleLabel);
        
        JLabel arrowLabel = new JLabel("→");
        arrowLabel.setFont(new Font("Arial", Font.BOLD, 18));
        arrowLabel.setForeground(Color.GRAY);
        
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(arrowLabel, BorderLayout.EAST);
        
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                action.actionPerformed(null);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(245, 245, 245));
                textPanel.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(Color.WHITE);
                textPanel.setBackground(Color.WHITE);
            }
        });
        
        return panel;
    }
    
    private JPanel createDivider() {
        JPanel divider = new JPanel();
        divider.setBackground(new Color(230, 230, 230));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
        return divider;
    }
    
    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            new WelcomeView().setVisible(true);
        }
    }
    
    private void openProfile() {
        new ProfileDialog(this, currentUser).setVisible(true);
    }
    
    private void openNotifications() {
        JOptionPane.showMessageDialog(this, 
            "Notifications settings coming soon!", 
            "Notifications", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openDefaultTemplates() {
        JOptionPane.showMessageDialog(this, 
            "Default templates settings coming soon!", 
            "Default Templates", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openShareExport() {
        JOptionPane.showMessageDialog(this, 
            "Share & Export features coming soon!", 
            "Share & Export", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openHelpSupport() {
        JOptionPane.showMessageDialog(this, 
            "Help & Support:\n\nEmail: support@packpal.com\nPhone: +254 700 000 000", 
            "Help & Support", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openPrivacyPolicy() {
        JOptionPane.showMessageDialog(this, 
            "Privacy Policy:\n\nYour privacy is important to us.\nVisit packpal.com/privacy for details.", 
            "Privacy Policy", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAbout() {
        JOptionPane.showMessageDialog(this, 
            "PackPal v1.0.0\n\nNever forget essential items again!\n\n© 2025 PackPal. All rights reserved.", 
            "About PackPal", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Profile Dialog
    class ProfileDialog extends JDialog {
        private User user;
        private JTextField nameField;
        private JTextField emailField;
        
        public ProfileDialog(JFrame parent, User user) {
            super(parent, "Profile Settings", true);
            this.user = user;
            initUI();
        }
        
        private void initUI() {
            setSize(400, 400);
            setLocationRelativeTo(getParent());
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            panel.setBackground(Color.WHITE);
            
            JLabel titleLabel = new JLabel("Profile Information");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Avatar placeholder
            JLabel avatarLabel = new JLabel("👤");
            avatarLabel.setFont(new Font("Arial", Font.PLAIN, 60));
            avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel nameLabel = new JLabel("Full Name:");
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            nameField = new JTextField(user.getFullName());
            nameField.setFont(new Font("Arial", Font.PLAIN, 14));
            nameField.setMaximumSize(new Dimension(320, 40));
            nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
            nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            
            JLabel emailLabel = new JLabel("Email:");
            emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
            emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            emailField = new JTextField(user.getEmail());
            emailField.setFont(new Font("Arial", Font.PLAIN, 14));
            emailField.setMaximumSize(new Dimension(320, 40));
            emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
            emailField.setEnabled(false); // Email cannot be changed
            emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            
            JButton saveButton = createBlueButton("Save Changes");
            saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            saveButton.addActionListener(e -> saveProfile());
            
            panel.add(titleLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
            panel.add(avatarLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 25)));
            panel.add(nameLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(nameField);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            panel.add(emailLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(emailField);
            panel.add(Box.createRigidArea(new Dimension(0, 25)));
            panel.add(saveButton);
            
            add(panel);
        }
        
        private void saveProfile() {
            // Update user profile logic here
            JOptionPane.showMessageDialog(this, 
                "Profile updated successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
        
        private JButton createBlueButton(String text) {
            JButton button = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(PRIMARY_BLUE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setMaximumSize(new Dimension(200, 45));
            return button;
        }
    }
}
