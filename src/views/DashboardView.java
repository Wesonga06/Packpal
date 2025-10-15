/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package views;

import controllers.DashboardController;
import models.ProfileModel;
import utils.UIConstants;
import views.components.RoundedButton;
import views.components.RoundedTextField;
import views.components.ShadowPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardView extends JFrame {
    private DashboardController controller;
    private JTabbedPane tabbedPane;
    private JPanel topBar;
    private JButton backButton;

    public DashboardView() {
        controller = new DashboardController(this);
        initializeUI();
        setTitle("PackPal Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        setPreferredSize(new Dimension(375, 667));
        setMinimumSize(new Dimension(350, 600));
        setResizable(true);

        // Top Bar
        topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        // Tabbed Content
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setPreferredSize(new Dimension(375, 600));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tabbedPane.addChangeListener(new TabChangeListener());

        // Add My Lists Tab
        JPanel myListsPanel = createMyListsPanel();
        tabbedPane.addTab(null, myListsPanel);  // Icon/label set later

        // Add Settings Tab
        JPanel settingsPanel = createSettingsPanel();
        tabbedPane.addTab(null, settingsPanel);

        // Set Initial Tab
        tabbedPane.setSelectedIndex(0);
        updateTabLabels();  // Proper labeling on load

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIConstants.PRIMARY_BLUE);
        bar.setPreferredSize(new Dimension(375, 60));

        // Back Button
        backButton = new JButton("← Back");
        backButton.setForeground(Color.WHITE);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setFont(UIConstants.BODY_FONT.deriveFont(14f));
        backButton.addActionListener(e -> controller.handleBackToWelcome());
        bar.add(backButton, BorderLayout.WEST);

        // Tab Buttons (Custom)
        JPanel tabsPanel = new JPanel(new GridLayout(1, 2));
        tabsPanel.setOpaque(false);

        RoundedButton myListsTab = new RoundedButton("📋 My Lists", UIConstants.PRIMARY_BLUE);
        myListsTab.setForeground(Color.WHITE);
        myListsTab.setPreferredSize(new Dimension(150, 40));
        myListsTab.setActionCommand("0");  // Tab index
        myListsTab.addActionListener(createTabActionListener(0));
        tabsPanel.add(myListsTab);

        RoundedButton settingsTab = new RoundedButton("⚙️ Settings", Color.WHITE);
        settingsTab.setForeground(UIConstants.PRIMARY_BLUE);
        settingsTab.setPreferredSize(new Dimension(150, 40));
        settingsTab.setActionCommand("1");
        settingsTab.addActionListener(createTabActionListener(1));
        tabsPanel.add(settingsTab);

        bar.add(tabsPanel, BorderLayout.CENTER);
        return bar;
    }

    private ActionListener createTabActionListener(int tabIndex) {
        return e -> {
            tabbedPane.setSelectedIndex(tabIndex);
            updateTabLabels();  // Refresh styling
        };
    }

    private void updateTabLabels() {
        int selected = tabbedPane.getSelectedIndex();
        Component[] tabs = ((JPanel) topBar.getComponent(1)).getComponents();  // Tab buttons array
        for (int i = 0; i < tabs.length; i++) {
            RoundedButton tabBtn = (RoundedButton) tabs[i];
            if (i == selected) {
                tabBtn.setBackground(UIConstants.PRIMARY_BLUE);
                tabBtn.setForeground(Color.WHITE);
                tabBtn.setFont(UIConstants.TITLE_FONT.deriveFont(Font.BOLD));
                tabBtn.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedButton.UnderlineBorder(Color.WHITE, 3),
                    tabBtn.getBorder()
                ));
            } else {
                tabBtn.setBackground(Color.WHITE);
                tabBtn.setForeground(UIConstants.PRIMARY_BLUE);
                tabBtn.setFont(UIConstants.BODY_FONT);
                tabBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            }
        }
        topBar.repaint();
    }

    private JPanel createMyListsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Search Bar
        RoundedTextField searchField = new RoundedTextField(20);
        searchField.setText("🔍 Search lists.");
        searchField.setEditable(true);
        searchField.setPreferredSize(new Dimension(300, 50));
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        panel.add(searchPanel, BorderLayout.NORTH);

        // Lists (Scrollable)
        JPanel listsPanel = new JPanel();
        listsPanel.setLayout(new BoxLayout(listsPanel, BoxLayout.Y_AXIS));
        listsPanel.setBackground(Color.WHITE);

        addSection(listsPanel, "Recent", new String[][]{
            {"Weekend Getaway", "13 items - packed"},
            {"Business Trip", "18 items - packed"}
        });

        addSection(listsPanel, "Templates", new String[][]{
            {"Camping Essentials", "31 items - Template"},
            {"Default Template", "18 items - Template"}
        });

        addSection(listsPanel, "Shared", new String[][]{
            {"Shared List", "12 items - Shared"}
        });

        JScrollPane scroll = new JScrollPane(listsPanel);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Main Scrollable Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        // Profile Section (Top Card)
        ShadowPanel profileCard = new ShadowPanel(new BorderLayout());
        profileCard.setPreferredSize(new Dimension(300, 150));
        profileCard.setMaximumSize(new Dimension(300, 150));

        // Avatar Placeholder
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.PRIMARY_BLUE);
                g2.fillOval(0, 0, 60, 60);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 24));
                g2.drawString("JD", 15, 45);  // Initials (dynamic from user name)
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(60, 60));
        avatarPanel.setBackground(Color.WHITE);

        // Profile Info
        JPanel profileInfo = new JPanel(new GridBagLayout());
        profileInfo.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(UIConstants.TITLE_FONT.deriveFont(12f));
        nameLabel.setForeground(Color.GRAY);
        JTextField nameField = new JTextField(ProfileModel.getName());  // Load from model
        nameField.setPreferredSize(new Dimension(200, 30));
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 0;
        profileInfo.add(nameLabel, gbc);
        gbc.gridy++;
        profileInfo.add(nameField, gbc);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(UIConstants.TITLE_FONT.deriveFont(12f));
        emailLabel.setForeground(Color.GRAY);
        JTextField emailField = new JTextField(ProfileModel.getEmail());
        emailField.setPreferredSize(new Dimension(200, 30));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy++;
        profileInfo.add(emailLabel, gbc);
        gbc.gridy++;
        profileInfo.add(emailField, gbc);

        profileCard.add(avatarPanel, BorderLayout.WEST);
        profileCard.add(profileInfo, BorderLayout.CENTER);

        // Save Profile Button
        RoundedButton saveProfileBtn = new RoundedButton("Save Profile", UIConstants.PRIMARY_BLUE);
        saveProfileBtn.setPreferredSize(new Dimension(100, 35));
        saveProfileBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveProfileBtn.addActionListener(e -> {
            // TODO: Update user model/DAO with nameField.getText(), emailField.getText()
            ProfileModel.setName(nameField.getText());
            ProfileModel.setEmail(emailField.getText());
            JOptionPane.showMessageDialog(this, "Profile updated!");
        });
        profileCard.add(saveProfileBtn, BorderLayout.SOUTH);

        content.add(profileCard);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // General Settings Section
        JLabel settingsTitle = new JLabel("General Settings");
        settingsTitle.setFont(UIConstants.TITLE_FONT.deriveFont(16f).deriveFont(Font.BOLD));
        settingsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(settingsTitle);
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Toggle Cards (Styled Checkboxes)
        String[][] settings = {
            {"Push Notifications", "Enable app notifications"},
            {"Dark Mode", "Switch to dark theme"},
            {"Auto-Sync Lists", "Sync packing lists across devices"}
        };
        for (String[] setting : settings) {
            ShadowPanel toggleCard = new ShadowPanel(new BorderLayout());
            toggleCard.setPreferredSize(new Dimension(300, 60));
            toggleCard.setMaximumSize(new Dimension(300, 60));

            JPanel toggleLeft = new JPanel(new BorderLayout());
            toggleLeft.setOpaque(false);
            JLabel toggleLabel = new JLabel(setting[0]);
            toggleLabel.setFont(UIConstants.BODY_FONT.deriveFont(16f));
            JLabel descLabel = new JLabel(setting[1]);
            descLabel.setFont(UIConstants.BODY_FONT.deriveFont(12f));
            descLabel.setForeground(Color.GRAY);
            toggleLeft.add(toggleLabel, BorderLayout.NORTH);
            toggleLeft.add(descLabel, BorderLayout.SOUTH);
            toggleCard.add(toggleLeft, BorderLayout.WEST);

            // Styled Toggle (JCheckBox as switch)
            JCheckBox toggleSwitch = new JCheckBox();
            toggleSwitch.setHorizontalAlignment(SwingConstants.RIGHT);
            toggleSwitch.addActionListener(e -> {
                // TODO: Save preference to user settings
                System.out.println(setting[0] + " toggled: " + toggleSwitch.isSelected());
            });
            toggleCard.add(toggleSwitch, BorderLayout.EAST);

            content.add(toggleCard);
            content.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // Advanced Options List
        JLabel advancedTitle = new JLabel("Advanced");
        advancedTitle.setFont(UIConstants.TITLE_FONT.deriveFont(16f).deriveFont(Font.BOLD));
        advancedTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(advancedTitle);
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] advanced = {"Default Templates", "Share & Export", "Help & Support", "Privacy Policy", "About PackPal"};
        for (String option : advanced) {
            ShadowPanel advCard = new ShadowPanel(new BorderLayout());
            advCard.setPreferredSize(new Dimension(300, 50));
            advCard.setMaximumSize(new Dimension(300, 50));
            JLabel label = new JLabel("  " + option);
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            label.setFont(UIConstants.BODY_FONT);
            advCard.add(label, BorderLayout.WEST);
            JLabel arrow = new JLabel("→");
            arrow.setHorizontalAlignment(SwingConstants.RIGHT);
            arrow.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            advCard.add(arrow, BorderLayout.EAST);

            // Action for each
            advCard.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    if (option.equals("Privacy Policy")) {
                        JOptionPane.showMessageDialog(DashboardView.this, "PackPal Privacy Policy: Your data is secure.");
                    } else if (option.equals("About PackPal")) {
                        JOptionPane.showMessageDialog(DashboardView.this, "PackPal v1.0 - Smart Packing for Travelers");
                    } else {
                        JOptionPane.showMessageDialog(DashboardView.this, option + " opened.");
                    }
                }
            });

            content.add(advCard);
            content.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // Logout Button (Bottom)
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        RoundedButton logoutBtn = new RoundedButton("Logout", Color.LIGHT_GRAY);
        logoutBtn.setForeground(Color.BLACK);
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Logout and return to Welcome?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.handleLogout();
            }
        });
        content.add(logoutBtn);

        // App Version Footer
        JLabel versionLabel = new JLabel("App Version 1.0");
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        versionLabel.setFont(UIConstants.BODY_FONT.deriveFont(10f));
        versionLabel.setForeground(Color.GRAY);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        content.add(versionLabel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void addSection(JPanel parent, String title, String[][] listItems) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.TITLE_FONT.deriveFont(16f).deriveFont(Font.BOLD));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        parent.add(titleLabel);

        for (String[] item : listItems) {
            ShadowPanel card = new ShadowPanel(new BorderLayout());
            card.setPreferredSize(new Dimension(300, 70));
            card.setMaximumSize(new Dimension(300, 70));

            JPanel leftPanel = new JPanel(new GridLayout(2, 1));
            leftPanel.setOpaque(false);
            JLabel title = new JLabel(item[0]);
            title.setFont(UIConstants.BODY_FONT.deriveFont(16f));
            title.setForeground(Color.BLACK);
            JLabel subtitle = new JLabel(item[1]);
            subtitle.setFont(UIConstants.BODY_FONT.deriveFont(12f));
            subtitle.setForeground(Color.GRAY);
            leftPanel.add(title);
            leftPanel.add(subtitle);
            card.add(leftPanel, BorderLayout.WEST);

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightPanel.setOpaque(false);

            JLabel blueDot = new JLabel("•");
            blueDot.setFont(new Font("Arial", Font.BOLD, 20));
            blueDot.setForeground(UIConstants.PRIMARY_BLUE);
            rightPanel.add(blueDot);

            JButton editBtn = new JButton("Edit");
            editBtn.setContentAreaFilled(false);
            editBtn.setForeground(UIConstants.PRIMARY_BLUE);
            editBtn.setFont(UIConstants.BODY_FONT.deriveFont(12f));
            editBtn.setBorderPainted(false);
            editBtn.addActionListener(e -> {
                // TODO: Open dynamic list view
                JOptionPane.showMessageDialog(this, "Opening " + item[0] + " for editing.");
            });
            rightPanel.add(editBtn);

            card.add(rightPanel, BorderLayout.EAST);

            parent.add(card);
            parent.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        parent.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    // Inner class for tab change (sync button styles)
    private class TabChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            updateTabLabels();
        }
    }

    // Getters for controller
    public JTabbedPane getTabbedPane() { return tabbedPane; }
    public void navigateToWelcome() {
        new WelcomeView();
        dispose();
    }
}