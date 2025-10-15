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

    private JPanel createMyListsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create Button (New)
        RoundedButton createBtn = new RoundedButton("Create New List", UIConstants.PRIMARY_BLUE);
        createBtn.setPreferredSize(new Dimension(300, 50));
        createBtn.addActionListener(e -> {
            new CreateNewListView();
            // Optional: dispose() this if modal
        });
        panel.add(createBtn, BorderLayout.NORTH);
        panel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.NORTH);  // Spacing

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

        // Load from DB
        PackingListDAO dao = new PackingListDAO();
        dao.initSchema();  // Ensure tables
        List<PackingListDAO.PackingList> dbLists = dao.getLists();

        // Dynamic sections (group by status/type)
        String[][] recent = dbLists.stream()
                .filter(l -> l.getSubtitle().contains("packed") || l.getSubtitle().contains("in progress"))
                .limit(2)
                .map(l -> new String[]{l.getName(), l.getSubtitle()})
                .toArray(String[][]::new);
        String[][] templates = dbLists.stream()
                .filter(l -> l.getSubtitle().contains("Template"))
                .map(l -> new String[]{l.getName(), l.getSubtitle()})
                .toArray(String[][]::new);
        String[][] shared = dbLists.stream()
                .filter(l -> l.getSubtitle().contains("Shared"))
                .map(l -> new String[]{l.getName(), l.getSubtitle()})
                .toArray(String[][]::new);

        addSection(listsPanel, "Recent", recent);
        addSection(listsPanel, "Templates", templates);
        addSection(listsPanel, "Shared", shared);

        JScrollPane scroll = new JScrollPane(listsPanel);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSettingsPanel() {
        // (Unchanged from previous; profile/toggles using DatabaseConfig if extended)
        // ... (paste the full createSettingsPanel from earlier response)
        // For brevity, assume it's the same as before
        JPanel panel = new JPanel(new BorderLayout());
        // ... (full code from previous)
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