package packpal.views;

import packpal.models.User;
import packpal.models.PackingList;
import packpal.models.Item;
import packpal.dao.PackingListDAO;
import packpal.services.WeatherService;
import packpal.services.WeatherService.WeatherData;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ListDetailView extends JFrame {
    private static final Color PRIMARY_BLUE = new Color(70, 160, 255);
    private User currentUser;
    private PackingList packingList;
    private PackingListDAO dao;
    private WeatherService weatherService;
    private JPanel itemsPanel;
    private JLabel progressLabel;
    private JProgressBar progressBar;
    
    public ListDetailView(User user, PackingList list) {
        this.currentUser = user;
        this.packingList = list;
        this.dao = new PackingListDAO();
        this.weatherService = new WeatherService();
        initializeUI();
        loadItems();
    }
    
    private void initializeUI() {
        setTitle("PackPal - " + packingList.getListName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        
        // Progress section
        JPanel progressPanel = createProgressPanel();
        contentPanel.add(progressPanel, BorderLayout.NORTH);
        
        // Items list
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // FAB for adding items
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
        
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> dispose());
        
        JLabel titleLabel = new JLabel(packingList.getListName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton editButton = new JButton("Edit");
        editButton.setFont(new Font("Arial", Font.BOLD, 14));
        editButton.setForeground(Color.WHITE);
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setFocusPainted(false);
        editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(editButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createProgressPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));
        
        // Weather info if destination is set
        if (packingList.getDestination() != null && !packingList.getDestination().isEmpty()) {
            JPanel weatherPanel = createWeatherPanel();
            if (weatherPanel != null) {
                panel.add(weatherPanel);
                panel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }
        
        // Progress label
        progressLabel = new JLabel();
        progressLabel.setFont(new Font("Arial", Font.BOLD, 16));
        progressLabel.setForeground(Color.BLACK);
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Arial", Font.BOLD, 12));
        progressBar.setForeground(PRIMARY_BLUE);
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Search bar
        JTextField searchField = new JTextField("🔍 Search items...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        searchField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("🔍 Search items...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("🔍 Search items...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        
        panel.add(progressLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(progressBar);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(searchField);
        
        return panel;
    }
    
    private JPanel createWeatherPanel() {
        try {
            WeatherData weather = weatherService.getCurrentWeather((String) packingList.getDestination());
            if (weather != null) {
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBackground(new Color(240, 248, 255));
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 220, 240), 1, true),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
                panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
                
                JLabel locationLabel = new JLabel(weather.getCity() + ", " + weather.getCountry());
                locationLabel.setFont(new Font("Arial", Font.BOLD, 14));
                locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                JLabel weatherLabel = new JLabel(
                    weather.getWeatherEmoji() + " " + 
                    String.format("%.1f°C", weather.getTemperature()) + " • " +
                    weather.getDescription() + " • Humidity: " + weather.getHumidity() + "%"
                );
                weatherLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                weatherLabel.setForeground(new Color(70, 70, 70));
                weatherLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                panel.add(locationLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 5)));
                panel.add(weatherLabel);
                
                return panel;
            }
        } catch (Exception e) {
            // Weather fetch failed
        }
        return null;
    }
    
    private void loadItems() {
        itemsPanel.removeAll();
        
        // Refresh the packing list from database
        packingList = (PackingList) dao.getPackingListById(packingList.getListId());
        List<Item> items = packingList.getItems();
        
        // Update progress
        int total = items.size();
        int packed = packingList.getPackedItemsCount();
        progressLabel.setText(packed + " of " + total + " items packed");
        progressBar.setValue(packingList.getPackingPercentage());
        progressBar.setString(packingList.getPackingPercentage() + "%");
        
        if (items.isEmpty()) {
            JLabel emptyLabel = new JLabel("No items yet. Tap + to add items!");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));
            itemsPanel.add(emptyLabel);
        } else {
            for (Item item : items) {
                itemsPanel.add(createItemCard(item));
                itemsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }
        
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }
    
    private JPanel createItemCard(Item item) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        // Checkbox
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(item.isPacked());
        checkBox.setBackground(Color.WHITE);
        checkBox.setFocusPainted(false);
        checkBox.addActionListener(e -> {
            dao.updateItemPackedStatus(item.getItemId(), checkBox.isSelected());
            loadItems(); // Refresh to update progress
        });
        
        // Item info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(item.getItemName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(item.isPacked() ? Color.GRAY : Color.BLACK);
        
        if (item.isPacked()) {
            nameLabel.setText("<html><strike>" + item.getItemName() + "</strike></html>");
        }
        
        if (item.getCategory() != null && !item.getCategory().isEmpty()) {
            JLabel categoryLabel = new JLabel(item.getCategory());
            categoryLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            categoryLabel.setForeground(Color.GRAY);
            infoPanel.add(nameLabel);
            infoPanel.add(categoryLabel);
        } else {
            infoPanel.add(nameLabel);
        }
        
        // Delete button
        JButton deleteButton = new JButton("×");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 24));
        deleteButton.setForeground(new Color(220, 53, 69));
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                "Delete this item?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                dao.deleteItem(item.getItemId());
                loadItems();
            }
        });
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(checkBox, BorderLayout.WEST);
        leftPanel.add(infoPanel, BorderLayout.CENTER);
        
        card.add(leftPanel, BorderLayout.CENTER);
        card.add(deleteButton, BorderLayout.EAST);
        
        return card;
    }
    
    private JButton createFAB() {
        JButton fab = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_BLUE);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                // Shadow
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
        fab.addActionListener(e -> addNewItem());
        
        return fab;
    }
    
    private void addNewItem() {
        new AddItemDialog(this, packingList).setVisible(true);
        loadItems();
    }
    
    // Add Item Dialog
    class AddItemDialog extends JDialog {
        private JTextField itemNameField;
        private JTextField categoryField;
        private PackingList list;
        
        public AddItemDialog(JFrame parent, PackingList list) {
            super(parent, "Add Item", true);
            this.list = list;
            initUI();
        }
        
        private void initUI() {
            setSize(400, 350);
            setLocationRelativeTo(getParent());
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            panel.setBackground(Color.WHITE);
            
            JLabel titleLabel = new JLabel("Add New Item");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel nameLabel = new JLabel("Item Name:");
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            itemNameField = new JTextField();
            itemNameField.setFont(new Font("Arial", Font.PLAIN, 14));
            itemNameField.setMaximumSize(new Dimension(320, 40));
            itemNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
            itemNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            
            JLabel categoryLabel = new JLabel("Category (optional):");
            categoryLabel.setFont(new Font("Arial", Font.BOLD, 14));
            categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            categoryField = new JTextField();
            categoryField.setFont(new Font("Arial", Font.PLAIN, 14));
            categoryField.setMaximumSize(new Dimension(320, 40));
            categoryField.setAlignmentX(Component.LEFT_ALIGNMENT);
            categoryField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            
            JButton addButton = createBlueButton("Add Item");
            addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            addButton.addActionListener(e -> saveItem());
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
            cancelButton.setForeground(Color.GRAY);
            cancelButton.setBorderPainted(false);
            cancelButton.setContentAreaFilled(false);
            cancelButton.setFocusPainted(false);
            cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cancelButton.addActionListener(e -> dispose());
            
            panel.add(titleLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 25)));
            panel.add(nameLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(itemNameField);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            panel.add(categoryLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(categoryField);
            panel.add(Box.createRigidArea(new Dimension(0, 25)));
            panel.add(addButton);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(cancelButton);
            
            add(panel);
        }
        
        private void saveItem() {
            String itemName = itemNameField.getText().trim();
            
            if (itemName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter an item name.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Item item = new Item();
            item.setListId(list.getListId());
            item.setItemName(itemName);
            item.setCategory(categoryField.getText().trim());
            item.setPacked(false);
            
            if (dao.addItem(item)) {
                JOptionPane.showMessageDialog(this,
                    "Item added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to add item.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
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