package views;

import dao.PackingListDAO;
import models.PackingList;
import models.Item;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ListDetailView extends JFrame {
    private static final Color PRIMARY_BLUE = new Color(70, 160, 255);
    private User currentUser;
    private PackingList currentList;
    private PackingListDAO packingListDAO;
    
    private JLabel titleLabel, progressLabel;
    private JProgressBar progressBar;
    private JPanel itemPanel;
    private JScrollPane scrollPane;
    
    public ListDetailView(User currentUser, PackingList list) {
        this.currentUser = currentUser;
        this.currentList = list;
        this.packingListDAO = new PackingListDAO();
        
        setTitle("PackPal - " + (list.getListName() != null ? list.getListName() : list.getDestination()));
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initComponents();
        loadItemsAndProgress();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Search bar
        JPanel searchPanel = createSearchPanel();
        
        // Items Panel
        itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        scrollPane = new JScrollPane(itemPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Panel with progress
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Floating Add Button
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(500, 700));
        layeredPane.add(mainPanel);
        mainPanel.setBounds(0, 0, 500, 700);
        
        JButton addButton = createFloatingAddButton();
        addButton.setBounds(410, 580, 60, 60);
        layeredPane.add(addButton, JLayeredPane.PALETTE_LAYER);
        
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
        
        titleLabel = new JLabel(currentList.getListName() != null ? currentList.getListName() : currentList.getDestination());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JButton editButton = new JButton("Edit");
        editButton.setFont(new Font("Arial", Font.PLAIN, 14));
        editButton.setForeground(PRIMARY_BLUE);
        editButton.setBackground(Color.WHITE);
        editButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        editButton.setFocusPainted(false);
        editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit functionality coming soon!"));
        
        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(editButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        
        JTextField searchField = new JTextField("🔍 Search items...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("🔍 Search items...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("🔍 Search items...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        return searchPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));
        
        progressLabel = new JLabel("0 of 0 items packed");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(460, 10));
        progressBar.setMaximumSize(new Dimension(460, 10));
        progressBar.setForeground(PRIMARY_BLUE);
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        bottomPanel.add(progressLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bottomPanel.add(progressBar);
        
        return bottomPanel;
    }
    
    private JButton createFloatingAddButton() {
        JButton button = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_BLUE);
                g2.fillOval(0, 0, getWidth(), getHeight());
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
        button.addActionListener(e -> addNewItem());
        return button;
    }
    
    private void loadItemsAndProgress() {
        itemPanel.removeAll();
        List<Item> items = packingListDAO.getItemsByListId(currentList.getListId());
        
        if (items.isEmpty()) {
            JLabel emptyLabel = new JLabel("No items yet. Tap + to add items!");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemPanel.add(Box.createVerticalGlue());
            itemPanel.add(emptyLabel);
            itemPanel.add(Box.createVerticalGlue());
        } else {
            for (Item item : items) {
                itemPanel.add(createItemCard(item));
                itemPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        updateProgressBar();
        itemPanel.revalidate();
        itemPanel.repaint();
    }
    
    private JPanel createItemCard(Item item) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        // Checkbox
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(item.isPacked());
        checkBox.setBackground(Color.WHITE);
        checkBox.setFocusPainted(false);
        checkBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        checkBox.addItemListener(e -> {
            boolean packed = checkBox.isSelected();
            packingListDAO.setItemPackedStatus(item.getItemId(), packed);
            refreshProgress();
        });
        
        // Item info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel nameLabel = new JLabel(item.getItemName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (item.getCategory() != null && !item.getCategory().isEmpty()) {
            JLabel categoryLabel = new JLabel(item.getCategory());
            categoryLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            categoryLabel.setForeground(Color.GRAY);
            categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(nameLabel);
            infoPanel.add(categoryLabel);
        } else {
            infoPanel.add(nameLabel);
        }
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        actionPanel.setBackground(Color.WHITE);
        
        JButton editButton = new JButton("✏");
        editButton.setFont(new Font("Arial", Font.PLAIN, 16));
        editButton.setForeground(Color.GRAY);
        editButton.setBorderPainted(false);
        editButton.setContentAreaFilled(false);
        editButton.setFocusPainted(false);
        editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        editButton.addActionListener(e -> editItem(item));
        
        JButton deleteButton = new JButton("×");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 20));
        deleteButton.setForeground(new Color(220, 53, 69));
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(e -> deleteItem(item));
        
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        
        card.add(checkBox, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);
        
        return card;
    }
    
    private void refreshProgress() {
        currentList.setTotalItems(packingListDAO.getTotalItemsCount(currentList.getListId()));
        currentList.setPackedItemsCount(packingListDAO.getPackedItemsCount(currentList.getListId()));
        updateProgressBar();
    }
    
    private void updateProgressBar() {
        int total = packingListDAO.getTotalItemsCount(currentList.getListId());
        int packed = packingListDAO.getPackedItemsCount(currentList.getListId());
        
        progressLabel.setText("Packed " + packed + " of " + total + " items");
        progressBar.setMaximum(total);
        progressBar.setValue(packed);
    }
    
    private void addNewItem() {
        JOptionPane.showMessageDialog(this, 
            "Add item functionality coming soon!", 
            "Add Item", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void editItem(Item item) {
        JOptionPane.showMessageDialog(this, 
            "Edit item functionality coming soon!", 
            "Edit Item", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteItem(Item item) {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this item?",
            "Delete Item",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            // Delete item logic here
            JOptionPane.showMessageDialog(this, "Item deleted!");
            loadItemsAndProgress();
        }
    }
}
