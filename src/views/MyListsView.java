package views;

import models.User;
import models.PackingList;
import dao.PackingListDAO;
import services.WeatherService;
import services.WeatherService.WeatherData;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MyListsView extends JFrame {
    private static final Color PRIMARY_BLUE = new Color(70, 160, 255);
    private User currentUser;
    private PackingListDAO packingListDAO;
    private WeatherService weatherService;
    private JPanel listsPanel;
    private JTabbedPane tabbedPane;
    
    public MyListsView(User user) {
        this.currentUser = user;
        this.packingListDAO = new PackingListDAO();
        this.weatherService = new WeatherService();
        initializeUI();
        loadLists();
    }
    
    private void initializeUI() {
        setTitle("PackPal - My Lists");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header with tabs
        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed content
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setForeground(Color.DARK_GRAY);
        tabbedPane.setBackground(Color.WHITE);
        
        // Lists content
        listsPanel = new JPanel();
        listsPanel.setLayout(new BoxLayout(listsPanel, BoxLayout.Y_AXIS));
        listsPanel.setBackground(Color.WHITE);
        listsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JScrollPane listsScrollPane = new JScrollPane(listsPanel);
        listsScrollPane.setBorder(null);
        listsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Templates panel
        JPanel templatesPanel = createTemplatesPanel();
        JScrollPane templatesScrollPane = new JScrollPane(templatesPanel);
        templatesScrollPane.setBorder(null);
        
        // Shared panel (placeholder)
        JPanel sharedPanel = createSharedPanel();
        
        tabbedPane.addTab("Recent", listsScrollPane);
        tabbedPane.addTab("Templates", templatesScrollPane);
        tabbedPane.addTab("Shared", sharedPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Floating action button
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(450, 700));
        layeredPane.add(mainPanel);
        mainPanel.setBounds(0, 0, 450, 700);
        
        JButton fabButton = createFAB();
        fabButton.setBounds(370, 600, 60, 60);
        layeredPane.add(fabButton, JLayeredPane.PALETTE_LAYER);
        
        add(layeredPane);
    }
    
    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("My Lists");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JButton searchButton = new JButton("🔍");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 18));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorderPainted(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        JButton settingsButton = new JButton("⚙️");
        settingsButton.setFont(new Font("Arial", Font.PLAIN, 18));
        settingsButton.setBorderPainted(false);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        settingsButton.addActionListener(e -> openSettings());
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(searchButton);
        rightPanel.add(settingsButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private void loadLists() {
        listsPanel.removeAll();
        
        List<PackingList> lists = packingListDAO.getPackingListsByUser(currentUser.getUserId());
        
        if (lists.isEmpty()) {
            JLabel emptyLabel = new JLabel("No packing lists yet. Create your first one!");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));
            listsPanel.add(emptyLabel);
        } else {
            for (PackingList list : lists) {
                listsPanel.add(createListCard(list));
                listsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }
        
        listsPanel.revalidate();
        listsPanel.repaint();
    }
    
    private JPanel createListCard(PackingList list) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Title and destination
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(list.getListName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.BLACK);
        
        JLabel arrowLabel = new JLabel("→");
        arrowLabel.setFont(new Font("Arial", Font.BOLD, 20));
        arrowLabel.setForeground(Color.GRAY);
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(arrowLabel, BorderLayout.EAST);
        
        // Items count and destination
        JLabel infoLabel = new JLabel(
            list.getTotalItems() + " items • " + 
            (list.getDestination() != null ? list.getDestination() : "No destination")
        );
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        
        // Progress bar
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(list.getPackingPercentage());
        progressBar.setStringPainted(true);
        progressBar.setString(list.getPackedItemsCount() + " of " + list.getTotalItems() + " items packed");
        progressBar.setFont(new Font("Arial", Font.PLAIN, 11));
        progressBar.setForeground(PRIMARY_BLUE);
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        // Weather info if destination is set
if (list.getDestination() != null && !list.getDestination().isEmpty()) {
    JPanel weatherPanel = createWeatherPanel((String) list.getDestination());
    if (weatherPanel != null) {
        card.add(topPanel);
        card.add(infoLabel);
        card.add(weatherPanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(progressBar);
    } else {
        card.add(topPanel);
        card.add(infoLabel);
        card.add(progressBar);
    }
} else {
    card.add(topPanel);
    card.add(infoLabel);
    card.add(progressBar);
}

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openListDetails(list);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(248, 248, 248));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });
        
        return card;
    }
    
    private JPanel createWeatherPanel(String destination) {
        try {
            WeatherData weather = weatherService.getCurrentWeather(destination);
            if (weather != null) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(false);
                panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
                
                JLabel weatherLabel = new JLabel(
                    weather.getWeatherEmoji() + " " + 
                    String.format("%.1f°C", weather.getTemperature()) + " • " +
                    weather.getDescription()
                );
                weatherLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                weatherLabel.setForeground(new Color(50, 50, 50));
                
                panel.add(weatherLabel);
                return panel;
            }
        } catch (Exception e) {
            // Weather fetch failed, return null
        }
        return null;
    }
    
    private JPanel createTemplatesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String[] templates = {
            "Weekend Getaway", "Business Trip Template", 
            "Beach Vacation", "Camping Essentials"
        };
        
        for (String template : templates) {
            panel.add(createTemplateCard(template));
            panel.add(Box.createRigidArea(new Dimension(0, 12)));
        }
        
        return panel;
    }
    
    private JPanel createTemplateCard(String name) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel arrowLabel = new JLabel("→");
        arrowLabel.setFont(new Font("Arial", Font.BOLD, 18));
        arrowLabel.setForeground(Color.GRAY);
        
        card.add(nameLabel, BorderLayout.WEST);
        card.add(arrowLabel, BorderLayout.EAST);
        
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                useTemplate(name);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(248, 248, 248));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });
        
        return card;
    }
    
    private JPanel createSharedPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel("No shared lists yet");
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.GRAY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));
        
        panel.add(label);
        return panel;
    }
    
    private JButton createFAB() {
        JButton fab = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_BLUE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                // Shadow effect
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillOval(2, 2, getWidth(), getHeight());
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        fab.setFont(new Font("Arial", Font.BOLD, 32));
        fab.setForeground(Color.WHITE);
        fab.setOpaque(false);
        fab.setContentAreaFilled(false);
        fab.setBorderPainted(false);
        fab.setFocusPainted(false);
        fab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fab.addActionListener(e -> createNewList());
        
        return fab;
    }
    
    private void createNewList() {
        new CreateListDialog(this, currentUser).setVisible(true);
        loadLists(); // Refresh after creating
    }
    
    private void openListDetails(PackingList list) {
        new ListDetailView(currentUser, list).setVisible(true);
        loadLists(); // Refresh when coming back
    }
    
    private void useTemplate(String templateName) {
        JOptionPane.showMessageDialog(this,
            "Creating list from template: " + templateName,
            "Template",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openSettings() {
        new SettingsView(currentUser).setVisible(true);
    }
}