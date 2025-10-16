package views;

import controllers.DashboardController;
import dao.PackingListDAO;
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
import java.util.Arrays;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class DashboardView extends JFrame {
    private DashboardController controller;
    private JTabbedPane tabbedPane;
    private JPanel topBar;
    private JButton backButton;
    private List<RoundedButton> tabButtons;

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
        tabbedPane.addTab(null, myListsPanel);

        // Add Settings Tab
        JPanel settingsPanel = createSettingsPanel();
        tabbedPane.addTab(null, settingsPanel);

        // Set Initial Tab
        tabbedPane.setSelectedIndex(0);
        updateTabLabels();

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

        // Tab Buttons
        JPanel tabsPanel = new JPanel(new GridLayout(1, 2));
        tabsPanel.setOpaque(false);

        RoundedButton myListsTab = new RoundedButton("📋 My Lists", UIConstants.PRIMARY_BLUE);
        myListsTab.setForeground(Color.WHITE);
        myListsTab.setPreferredSize(new Dimension(150, 40));
        myListsTab.setActionCommand("0");
        myListsTab.addActionListener(createTabActionListener(0));
        tabsPanel.add(myListsTab);

        RoundedButton settingsTab = new RoundedButton("⚙️ Settings", Color.WHITE);
        settingsTab.setForeground(UIConstants.PRIMARY_BLUE);
        settingsTab.setPreferredSize(new Dimension(150, 40));
        settingsTab.setActionCommand("1");
        settingsTab.addActionListener(createTabActionListener(1));
        tabsPanel.add(settingsTab);

        bar.add(tabsPanel, BorderLayout.CENTER);

        tabButtons = Arrays.asList(myListsTab, settingsTab);

        return bar;
    }

    private ActionListener createTabActionListener(int tabIndex) {
        return e -> {
            tabbedPane.setSelectedIndex(tabIndex);
            updateTabLabels();
        };
    }

    private void updateTabLabels() {
        int selected = tabbedPane.getSelectedIndex();
        for (int i = 0; i < tabButtons.size(); i++) {
            RoundedButton tabBtn = tabButtons.get(i);
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

    private JPanel createSettingsPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Scrollable main content
    JPanel content = new JPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.setBackground(Color.WHITE);

    // ===== PROFILE CARD =====
    ShadowPanel profileCard = new ShadowPanel(new BorderLayout());
    profileCard.setPreferredSize(new Dimension(300, 200));
    profileCard.setMaximumSize(new Dimension(300, 200));

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
            String initials = ProfileModel.getName().substring(0, Math.min(2, ProfileModel.getName().length())).toUpperCase();
            g2.drawString(initials, 15, 45);
            g2.dispose();
        }
    };
    avatarPanel.setPreferredSize(new Dimension(70, 70));
    avatarPanel.setBackground(Color.WHITE);

    // Profile Info Panel
    JPanel profileInfo = new JPanel(new GridBagLayout());
    profileInfo.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 10, 5, 10);
    gbc.anchor = GridBagConstraints.WEST;

    JLabel nameLabel = new JLabel("Name");
    nameLabel.setFont(UIConstants.TITLE_FONT.deriveFont(12f));
    nameLabel.setForeground(Color.GRAY);
    JTextField nameField = new JTextField(ProfileModel.getName());
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

    // Save Button
    RoundedButton saveProfileBtn = new RoundedButton("Save Profile", UIConstants.PRIMARY_BLUE);
    saveProfileBtn.setPreferredSize(new Dimension(120, 35));
    saveProfileBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
    saveProfileBtn.addActionListener(e -> {
        ProfileModel.setName(nameField.getText());
        ProfileModel.setEmail(emailField.getText());
        avatarPanel.repaint();
        JOptionPane.showMessageDialog(panel, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    });
    profileCard.add(saveProfileBtn, BorderLayout.SOUTH);

    content.add(profileCard);
    content.add(Box.createRigidArea(new Dimension(0, 20)));

    // ===== GENERAL SETTINGS =====
    JLabel settingsTitle = new JLabel("General Settings");
    settingsTitle.setFont(UIConstants.TITLE_FONT.deriveFont(16f).deriveFont(Font.BOLD));
    settingsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    content.add(settingsTitle);
    content.add(Box.createRigidArea(new Dimension(0, 10)));

    // Settings List
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

        JToggleButton toggleSwitch = new JToggleButton();
        toggleSwitch.setPreferredSize(new Dimension(50, 25));
        toggleSwitch.setFocusPainted(false);
        toggleSwitch.setBackground(Color.LIGHT_GRAY);
        toggleSwitch.addItemListener(e -> {
            if (toggleSwitch.isSelected()) {
                toggleSwitch.setBackground(UIConstants.PRIMARY_BLUE);
            } else {
                toggleSwitch.setBackground(Color.LIGHT_GRAY);
            }
        });

        toggleCard.add(toggleLeft, BorderLayout.CENTER);
        toggleCard.add(toggleSwitch, BorderLayout.EAST);

        content.add(toggleCard);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    // Scroll Pane
    JScrollPane scrollPane = new JScrollPane(content);
    scrollPane.setBorder(null);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
}


    private void addSection(JPanel parent, String title, String[][] listItems) {
        JLabel sectionTitleLabel = new JLabel(title);
        sectionTitleLabel.setFont(UIConstants.TITLE_FONT.deriveFont(16f).deriveFont(Font.BOLD));
        sectionTitleLabel.setForeground(Color.BLACK);
        sectionTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        parent.add(sectionTitleLabel);

        for (String[] item : listItems) {
            ShadowPanel card = new ShadowPanel(new BorderLayout());
            card.setPreferredSize(new Dimension(300, 70));
            card.setMaximumSize(new Dimension(300, 70));

            JPanel leftPanel = new JPanel(new GridLayout(2, 1));
            leftPanel.setOpaque(false);
            JLabel itemNameLabel = new JLabel(item[0]);  // Fixed naming
            itemNameLabel.setFont(UIConstants.BODY_FONT.deriveFont(16f));
            itemNameLabel.setForeground(Color.BLACK);
            JLabel subtitle = new JLabel(item[1]);
            subtitle.setFont(UIConstants.BODY_FONT.deriveFont(12f));
            subtitle.setForeground(Color.GRAY);
            leftPanel.add(itemNameLabel);
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
                // Parse ID from name or extend data structure; mock for now
                int listId = Integer.parseInt(item[0].replaceAll("[^0-9]", ""));  // Simple parse; improve with ID in data
                new PackingListView(listId);
            });
            rightPanel.add(editBtn);

            card.add(rightPanel, BorderLayout.EAST);

            parent.add(card);
            parent.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        parent.add(Box.createRigidArea(new Dimension(0, 20)));
    }

   private JPanel createMyListsPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel title = new JLabel("My Packing Lists");
    title.setFont(new Font("Arial", Font.BOLD, 20));
    title.setForeground(new Color(33, 150, 243));
    title.setHorizontalAlignment(SwingConstants.CENTER);
    panel.add(title, BorderLayout.NORTH);

    // Table model
    String[] columns = {"List ID", "List Name", "Destination", "Date Created"};
    DefaultTableModel tableModel = new DefaultTableModel(columns, 0);

    // Example data (replace with database or controller call)
    Object[][] data = {
        {1, "Beach Trip", "Mombasa", "2025-10-14"},
        {2, "Business Trip", "Nairobi", "2025-10-10"}
    };
    for (Object[] row : data) {
        tableModel.addRow(row);
    }

    JTable table = new JTable(tableModel);
    table.setRowHeight(25);
    table.setFillsViewportHeight(true);
    table.setGridColor(Color.LIGHT_GRAY);
    table.setSelectionBackground(new Color(224, 242, 241));

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    panel.add(scrollPane, BorderLayout.CENTER);

    // Button to add a new list
    JButton addListBtn = new JButton("Create New List");
    addListBtn.setBackground(new Color(33, 150, 243));
    addListBtn.setForeground(Color.WHITE);
    addListBtn.setFocusPainted(false);
    addListBtn.setFont(new Font("Arial", Font.BOLD, 14));

    addListBtn.addActionListener(e -> {
        JOptionPane.showMessageDialog(panel, "Create List Dialog Coming Soon!");
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.add(addListBtn);

    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
}


    // TabChangeListener and getters (unchanged)
    private class TabChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            updateTabLabels();
        }
    }

    public JTabbedPane getTabbedPane() { return tabbedPane; }
    public void navigateToWelcome() {
        new WelcomeView();
        dispose();
    }
}