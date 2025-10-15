package views;

import dao.PackingListDAO;
import models.PackingList;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MyListsView extends JFrame {
    private static final Color PRIMARY_BLUE = new Color(70, 160, 255);
    private User user;
    private JPanel listsPanel;
    private JScrollPane scrollPane;
    private List<PackingList> lists;
    private PackingListDAO packingListDAO;
    private JButton selectedTab;

    public MyListsView(User user) {
        this.user = user;
        this.packingListDAO = new PackingListDAO();
        setTitle("My Lists - PackPal");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initializeUI();
        loadLists();
    }

    private void initializeUI() {
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header with tabs
        JPanel headerPanel = createHeader();
        
        // Lists container
        listsPanel = new JPanel();
        listsPanel.setLayout(new BoxLayout(listsPanel, BoxLayout.Y_AXIS));
        listsPanel.setBackground(Color.WHITE);
        listsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        scrollPane = new JScrollPane(listsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Center panel with search and lists
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(createSearchPanel(), BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Create layered pane for floating button
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(500, 700));
        
        // Add main panel to base layer
        mainPanel.setBounds(0, 0, 500, 700);
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        // Add floating button to palette layer
        JButton addButton = createFloatingAddButton();
        addButton.setBounds(410, 590, 60, 60);
        layeredPane.add(addButton, JLayeredPane.PALETTE_LAYER);

        add(layeredPane);
    }

    private JPanel createHeader() {
        JPanel fullHeader = new JPanel(new BorderLayout());
        fullHeader.setBackground(PRIMARY_BLUE);

        // Top header with title and settings
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(PRIMARY_BLUE);
        topHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));

        JLabel titleLabel = new JLabel("My Lists");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JButton settingsButton = new JButton("⚙");
        settingsButton.setFont(new Font("Arial", Font.PLAIN, 24));
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setBorderPainted(false);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        settingsButton.addActionListener(e -> new SettingsView(user).setVisible(true));

        topHeader.add(titleLabel, BorderLayout.WEST);
        topHeader.add(settingsButton, BorderLayout.EAST);

        // Tabs panel
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabsPanel.setBackground(PRIMARY_BLUE);
        tabsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));

        JButton recentTab = createTabButton("Recent", true);
        JButton templatesTab = createTabButton("Templates", false);
        JButton sharedTab = createTabButton("Shared", false);

        recentTab.addActionListener(e -> selectTab(recentTab));
        templatesTab.addActionListener(e -> selectTab(templatesTab));
        sharedTab.addActionListener(e -> selectTab(sharedTab));

        selectedTab = recentTab;

        tabsPanel.add(recentTab);
        tabsPanel.add(templatesTab);
        tabsPanel.add(sharedTab);

        fullHeader.add(topHeader, BorderLayout.NORTH);
        fullHeader.add(tabsPanel, BorderLayout.SOUTH);

        return fullHeader;
    }

    private JButton createTabButton(String text, boolean selected) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(selected ? Color.WHITE : new Color(200, 220, 255));
        button.setBackground(PRIMARY_BLUE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        
        if (selected) {
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, Color.WHITE),
                BorderFactory.createEmptyBorder(10, 15, 7, 15)
            ));
        } else {
            button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }
        
        return button;
    }

    private void selectTab(JButton newTab) {
        if (selectedTab != null) {
            selectedTab.setForeground(new Color(200, 220, 255));
            selectedTab.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }
        
        newTab.setForeground(Color.WHITE);
        newTab.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, Color.WHITE),
            BorderFactory.createEmptyBorder(10, 15, 7, 15)
        ));
        selectedTab = newTab;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JTextField searchField = new JTextField("🔍 Search lists...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("🔍 Search lists...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("🔍 Search lists...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        searchPanel.add(searchField, BorderLayout.CENTER);
        return searchPanel;
    }

    private JButton createFloatingAddButton() {
        JButton button = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillOval(2, 2, getWidth() - 2, getHeight() - 2);
                
                // Draw button
                g2.setColor(PRIMARY_BLUE);
                g2.fillOval(0, 0, getWidth() - 4, getHeight() - 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Arial", Font.BOLD, 32));
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> openCreateList());
        return button;
    }

    public void loadLists() {
        try {
            lists = packingListDAO.getPackingListsByUser(user.getUserId());
            refreshListsUI();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading lists: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void refreshLists() {
        loadLists();
    }

    private void refreshListsUI() {
        listsPanel.removeAll();

        if (lists == null || lists.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setBackground(Color.WHITE);
            
            JLabel emptyLabel = new JLabel("No packing lists yet");
            emptyLabel.setFont(new Font("Arial", Font.BOLD, 16));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel hintLabel = new JLabel("Tap + to create one!");
            hintLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            hintLabel.setForeground(Color.LIGHT_GRAY);
            hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            emptyPanel.add(Box.createVerticalGlue());
            emptyPanel.add(emptyLabel);
            emptyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            emptyPanel.add(hintLabel);
            emptyPanel.add(Box.createVerticalGlue());
            
            listsPanel.add(emptyPanel);
        } else {
            for (PackingList list : lists) {
                listsPanel.add(createListCard(list));
                listsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        listsPanel.revalidate();
        listsPanel.repaint();
    }

    private JPanel createListCard(PackingList list) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Left panel with list info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        String listName = list.getListName();
        if (listName == null || listName.isEmpty()) {
            listName = list.getDestination();
        }
        if (listName == null || listName.isEmpty()) {
            listName = "Untitled Trip";
        }
        
        JLabel nameLabel = new JLabel(listName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String tripType = list.getTripType();
        if (tripType == null || tripType.isEmpty()) {
            tripType = "Trip";
        }
        
        JLabel detailsLabel = new JLabel(list.getTotalItems() + " items • " + tripType);
        detailsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        detailsLabel.setForeground(Color.GRAY);
        detailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(detailsLabel);

        // Right panel with progress
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBackground(Color.WHITE);

        JLabel progressLabel = new JLabel(list.getPackedItemsCount() + " of " + list.getTotalItems() + " items packed");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 11));
        progressLabel.setForeground(PRIMARY_BLUE);
        progressLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JProgressBar progressBar = new JProgressBar(0, Math.max(list.getTotalItems(), 1));
        progressBar.setValue(list.getPackedItemsCount());
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(120, 8));
        progressBar.setMaximumSize(new Dimension(120, 8));
        progressBar.setForeground(PRIMARY_BLUE);
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setBorder(null);

        progressPanel.add(progressLabel);
        progressPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        progressPanel.add(progressBar);

        card.add(infoPanel, BorderLayout.WEST);
        card.add(progressPanel, BorderLayout.EAST);

        // Click to open details
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openListDetails(list);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 248, 248));
                infoPanel.setBackground(new Color(248, 248, 248));
                progressPanel.setBackground(new Color(248, 248, 248));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                infoPanel.setBackground(Color.WHITE);
                progressPanel.setBackground(Color.WHITE);
            }
        });

        return card;
    }

    private void openCreateList() {
        CreateListDialog dialog = new CreateListDialog(this, user);
        dialog.setVisible(true);
    }

    private void openListDetails(PackingList list) {
        new ListDetailView(user, list).setVisible(true);
    }
}

