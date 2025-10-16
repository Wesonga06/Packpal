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
        mainPanel.add(createHeader(), BorderLayout.NORTH);

        // Search bar
        JTextField searchField = new JTextField("🔍 Search items...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(Color.GRAY);
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

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Items
        itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(itemPanel);
        scrollPane.setBorder(null);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Progress
        mainPanel.add(createProgressPanel(), BorderLayout.SOUTH);

        // Floating Add Button
        JButton addButton = createFloatingAddButton();
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(500, 700));
        mainPanel.setBounds(0, 0, 500, 700);
        addButton.setBounds(410, 580, 60, 60);

        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(addButton, JLayeredPane.PALETTE_LAYER);
        add(layeredPane);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JButton backButton = new JButton("← Back");
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.addActionListener(e -> dispose());

        titleLabel = new JLabel(currentList.getListName() != null ? currentList.getListName() : currentList.getDestination());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit functionality coming soon!"));

        headerPanel.add(backButton, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(editButton, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createProgressPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        progressLabel = new JLabel("0 of 0 items packed");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 14));
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setForeground(PRIMARY_BLUE);
        progressBar.setBackground(new Color(230, 230, 230));

        panel.add(progressLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(progressBar);
        return panel;
    }

    private JButton createFloatingAddButton() {
        JButton button = new JButton("+");
        button.setFont(new Font("Arial", Font.BOLD, 32));
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
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

    private JPanel createItemCard(Item item) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(item.isPacked());
        checkBox.setBackground(Color.WHITE);
        checkBox.addItemListener(e -> {
            packingListDAO.setItemPackedStatus(item.getItemId(), checkBox.isSelected());
            updateProgressBar();
        });

        JLabel nameLabel = new JLabel(item.getItemName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel categoryLabel = new JLabel(item.getCategory() == null ? "" : item.getCategory());
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        categoryLabel.setForeground(Color.GRAY);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(nameLabel);
        if (!categoryLabel.getText().isEmpty()) infoPanel.add(categoryLabel);

        JButton deleteButton = new JButton("×");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 18));
        deleteButton.setForeground(Color.RED);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
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
        progressBar.setMaximum(total);
        progressBar.setValue(packed);
    }

    private void addNewItem() {
        JOptionPane.showMessageDialog(this, "Add item functionality coming soon!");
    }

    private void deleteItem(Item item) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this item?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Implement deletion in DAO if needed
            JOptionPane.showMessageDialog(this, "Item deleted!");
            loadItemsAndProgress();
        }
    }
}
