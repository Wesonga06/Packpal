package views;

import dao.PackingListDAO;
import models.Item;
import models.PackingList;
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
    private JTextField searchField;

    public ListDetailView(User currentUser, PackingList list) {
        this.currentUser = currentUser;
        this.currentList = list;
        this.packingListDAO = new PackingListDAO();

        setTitle("PackPal - " + (list.getListName() != null ? list.getListName() : list.getDestination()));
        setSize(550, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        initComponents();
        loadItemsAndProgress();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header
        mainPanel.add(createHeader(), BorderLayout.NORTH);

        // Search and Items Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        
        // Search bar
        searchField = new JTextField("🔍 Search items...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
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

        // Add real-time search functionality
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterItems(searchField.getText());
            }
        });

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Items list
        itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JScrollPane scrollPane = new JScrollPane(itemPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Progress and Add Button Panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(createProgressPanel(), BorderLayout.NORTH);
        bottomPanel.add(createAddButtonPanel(), BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JButton backButton = new JButton("← Back");
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> dispose());

        titleLabel = new JLabel(currentList.getListName() != null ? currentList.getListName() : currentList.getDestination());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton deleteButton = new JButton("🗑️ Delete List");
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 12));
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(e -> deleteList());

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(deleteButton, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createProgressPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        progressLabel = new JLabel("0 of 0 items packed");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setForeground(PRIMARY_BLUE);
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setStringPainted(true);

        panel.add(progressLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(progressBar);
        return panel;
    }

    private JPanel createAddButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JButton addButton = new JButton("+ Add Item");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setForeground(Color.WHITE);
        addButton.setBackground(PRIMARY_BLUE);
        addButton.setFocusPainted(false);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        addButton.addActionListener(e -> addNewItem());

        panel.add(addButton);
        return panel;
    }

    private void loadItemsAndProgress() {
        itemPanel.removeAll();
        List<Item> items = packingListDAO.getItemsByListId(currentList.getListId());

        if (items.isEmpty()) {
            JLabel emptyLabel = new JLabel("No items yet. Tap '+ Add Item' to start packing!");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
            itemPanel.add(emptyLabel);
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

    private void filterItems(String searchText) {
        itemPanel.removeAll();
        
        if (searchText.equals("🔍 Search items...") || searchText.trim().isEmpty()) {
            loadItemsAndProgress();
            return;
        }
        
        List<Item> items = packingListDAO.getItemsByListId(currentList.getListId());
        String lowerSearch = searchText.toLowerCase();
        boolean found = false;
        
        for (Item item : items) {
            if (item.getItemName().toLowerCase().contains(lowerSearch) || 
                (item.getCategory() != null && item.getCategory().toLowerCase().contains(lowerSearch))) {
                itemPanel.add(createItemCard(item));
                itemPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                found = true;
            }
        }
        
        if (!found) {
            JLabel noResultsLabel = new JLabel("No items match your search");
            noResultsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            noResultsLabel.setForeground(Color.GRAY);
            noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemPanel.add(noResultsLabel);
        }
        
        itemPanel.revalidate();
        itemPanel.repaint();
    }

    private JPanel createItemCard(Item item) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(item.isPacked());
        checkBox.setBackground(Color.WHITE);
        checkBox.addItemListener(e -> {
            item.setPacked(checkBox.isSelected());
            packingListDAO.setItemPackedStatus(item.getItemId(), checkBox.isSelected());
            updateProgressBar();
        });

        JLabel nameLabel = new JLabel(item.getItemName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        if (item.isPacked()) {
            nameLabel.setForeground(Color.GRAY);
        }

        JLabel categoryLabel = new JLabel(item.getCategory() == null ? "Uncategorized" : item.getCategory());
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        categoryLabel.setForeground(Color.GRAY);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(nameLabel);
        infoPanel.add(categoryLabel);

        JButton deleteButton = new JButton("×");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 20));
        deleteButton.setForeground(Color.RED);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(e -> deleteItem(item));

        card.add(checkBox, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(deleteButton, BorderLayout.EAST);
        return card;
    }

    private void updateProgressBar() {
        int total = packingListDAO.getTotalItemsCount(currentList.getListId());
        int packed = packingListDAO.getPackedItemsCount(currentList.getListId());
        
        progressLabel.setText("Packed " + packed + " of " + total + " items");
        
        if (total > 0) {
            int percentage = (int) ((packed * 100.0) / total);
            progressBar.setMaximum(100);
            progressBar.setValue(percentage);
            progressBar.setString(percentage + "%");
        } else {
            progressBar.setMaximum(100);
            progressBar.setValue(0);
            progressBar.setString("0%");
        }
    }

    private void addNewItem() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        
        JTextField nameField = new JTextField();
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{
            "Clothing", "Toiletries", "Electronics", "Documents", "Food", "Other"
        });

        panel.add(new JLabel("Item Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Item", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String itemName = nameField.getText().trim();
            String category = (String) categoryCombo.getSelectedItem();

            if (!itemName.isEmpty()) {
                Item newItem = new Item();
                newItem.setListId(currentList.getListId());
                newItem.setItemName(itemName);
                newItem.setCategory(category);
                newItem.setPacked(false);

                boolean success = packingListDAO.addItemToList(newItem);
                if (success) {
                    loadItemsAndProgress();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add item", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Item name cannot be empty", "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void deleteItem(Item item) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete '" + item.getItemName() + "'?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = packingListDAO.deleteItem(item.getItemId());
            if (success) {
                loadItemsAndProgress();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete item", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteList() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this entire packing list?\nThis action cannot be undone.",
            "Confirm Delete List",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = packingListDAO.deletePackingList(currentList.getListId());
            if (success) {
                JOptionPane.showMessageDialog(this, "Packing list deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete packing list", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
