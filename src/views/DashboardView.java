package views;

import controllers.DashboardController;
import dao.PackingListDAO;
import models.PackingList;
import models.ProfileModel;
import models.User;
import utils.UIConstants;
import views.components.RoundedButton;
import views.components.ShadowPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

public class DashboardView extends JFrame {
    private DashboardController controller;
    private JTabbedPane tabbedPane;
    private JPanel topBar;
    private JButton backButton;
    private List<RoundedButton> tabButtons;
    private User currentUser;
    private PackingListDAO dao;
    private JPanel myListsContentPanel;

    public DashboardView() {
        // For testing - create a mock user with ID 2 (Max from your database)
        this.currentUser = new User();
        this.currentUser.setUserId(2);
        this.currentUser.setName("Max");
        this.currentUser.setEmail("max@gmail.com");
        
        this.dao = new PackingListDAO();
        this.controller = new DashboardController(this);
        
        initializeUI();
        setTitle("PackPal Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public DashboardView(User user) {
        this.currentUser = user;
        this.dao = new PackingListDAO();
        this.controller = new DashboardController(this);
        
        initializeUI();
        setTitle("PackPal Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        setPreferredSize(new Dimension(500, 700));
        setMinimumSize(new Dimension(450, 650));
        setResizable(true);

        // Top Bar
        topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        // Tabbed Content
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setPreferredSize(new Dimension(500, 600));
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
        bar.setPreferredSize(new Dimension(500, 60));

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
        myListsTab.setPreferredSize(new Dimension(200, 40));
        myListsTab.setActionCommand("0");
        myListsTab.addActionListener(createTabActionListener(0));
        tabsPanel.add(myListsTab);

        RoundedButton settingsTab = new RoundedButton("⚙️ Settings", Color.WHITE);
        settingsTab.setForeground(UIConstants.PRIMARY_BLUE);
        settingsTab.setPreferredSize(new Dimension(200, 40));
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

    private JPanel createMyListsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("My Packing Lists");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(UIConstants.PRIMARY_BLUE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        // Content panel that will be refreshed
        myListsContentPanel = new JPanel(new BorderLayout());
        myListsContentPanel.setBackground(Color.WHITE);
        loadPackingLists();
        panel.add(myListsContentPanel, BorderLayout.CENTER);

        // Button to add a new list
        JButton addListBtn = new JButton("+ Create New List");
        addListBtn.setBackground(UIConstants.PRIMARY_BLUE);
        addListBtn.setForeground(Color.WHITE);
        addListBtn.setFocusPainted(false);
        addListBtn.setFont(new Font("Arial", Font.BOLD, 14));
        addListBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addListBtn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        addListBtn.addActionListener(e -> {
            CreateListDialog dialog = new CreateListDialog(this, currentUser, this::loadPackingLists);
            dialog.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(addListBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPackingLists() {
        myListsContentPanel.removeAll();
        
        // Fetch lists from database
        List<PackingList> lists = dao.getPackingListsByUser(currentUser.getUserId());
        
        if (lists.isEmpty()) {
            JLabel emptyLabel = new JLabel("No packing lists yet. Create one to get started!");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
            myListsContentPanel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            // Create table
            String[] columns = {"ID", "List Name", "Destination", "Trip Type", "Start Date"};
            DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            for (PackingList list : lists) {
                Object[] row = {
                    list.getListId(),
                    list.getListName(),
                    list.getDestination(),
                    list.getTripType(),
                    list.getStartDate() != null ? list.getStartDate().toString() : "N/A"
                };
                tableModel.addRow(row);
            }
            
            JTable table = new JTable(tableModel);
            table.setRowHeight(30);
            table.setFont(new Font("Arial", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
            table.getTableHeader().setBackground(new Color(240, 240, 240));
            table.setSelectionBackground(new Color(230, 240, 255));
            table.setGridColor(new Color(220, 220, 220));
            
            // Add mouse listener for double-click to open list
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int row = table.getSelectedRow();
                        if (row != -1) {
                            int listId = (int) table.getValueAt(row, 0);
                            PackingList selectedList = dao.getPackingListById(listId);
                            if (selectedList != null) {
                                new ListDetailView(currentUser, selectedList).setVisible(true);
                            }
                        }
                    }
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            myListsContentPanel.add(scrollPane, BorderLayout.CENTER);
        }
        
        myListsContentPanel.revalidate();
        myListsContentPanel.repaint();
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        // Profile Section
        ShadowPanel profileCard = new ShadowPanel(new BorderLayout());
        profileCard.setPreferredSize(new Dimension(400, 200));
        profileCard.setMaximumSize(new Dimension(400, 200));

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
                String initials = currentUser.getName().substring(0, Math.min(2, currentUser.getName().length())).toUpperCase();
                g2.drawString(initials, 20, 40);
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(70, 70));
        avatarPanel.setBackground(Color.WHITE);

        JPanel profileInfo = new JPanel(new GridBagLayout());
        profileInfo.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setForeground(Color.GRAY);
        JTextField nameField = new JTextField(currentUser.getName());
        nameField.setPreferredSize(new Dimension(250, 30));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridy = 0;
        profileInfo.add(nameLabel, gbc);
        gbc.gridy++;
        profileInfo.add(nameField, gbc);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 12));
        emailLabel.setForeground(Color.GRAY);
        JTextField emailField = new JTextField(currentUser.getEmail());
        emailField.setPreferredSize(new Dimension(250, 30));
        emailField.setEditable(false);
        emailField.setBackground(new Color(245, 245, 245));
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

        RoundedButton saveProfileBtn = new RoundedButton("Save Profile", UIConstants.PRIMARY_BLUE);
        saveProfileBtn.setPreferredSize(new Dimension(120, 35));
        saveProfileBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveProfileBtn.addActionListener(e -> {
            currentUser.setName(nameField.getText());
            avatarPanel.repaint();
            JOptionPane.showMessageDialog(panel, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        profileCard.add(saveProfileBtn, BorderLayout.SOUTH);

        content.add(profileCard);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel settingsTitle = new JLabel("General Settings");
        settingsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        settingsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(settingsTitle);
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        String[][] settings = {
            {"Push Notifications", "Enable app notifications"},
            {"Dark Mode", "Switch to dark theme"},
            {"Auto-Sync Lists", "Sync packing lists across devices"}
        };

        for (String[] setting : settings) {
            ShadowPanel toggleCard = new ShadowPanel(new BorderLayout());
            toggleCard.setPreferredSize(new Dimension(400, 60));
            toggleCard.setMaximumSize(new Dimension(400, 60));

            JPanel toggleLeft = new JPanel(new BorderLayout());
            toggleLeft.setOpaque(false);

            JLabel toggleLabel = new JLabel(setting[0]);
            toggleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            JLabel descLabel = new JLabel(setting[1]);
            descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
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

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

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