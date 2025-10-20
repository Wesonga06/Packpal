package views;

import dao.PackingListDAO;
import models.Item;
import utils.UIConstants;
import views.components.RoundedButton;
import views.components.ShadowPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PackingListView extends JFrame {
    private int listId;
    private PackingListDAO dao = new PackingListDAO();
    private JLabel progressLabel;
    private JProgressBar progressBar;
    private JPanel itemPanel;
    private JTextField searchField;

    public PackingListView(int listId) {
        this.listId = listId;
        try {
            dao.initSchema();  // Ensure DB ready
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        initializeUI();
        setTitle("Edit Packing List - PackPal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        setPreferredSize(new Dimension(375, 667));
        setLayout(new BorderLayout());

        JPanel topBar = createTopBar("Edit List", true);
        add(topBar, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Progress
        progressLabel = new JLabel("0 of 0 items packed", SwingConstants.CENTER);
        progressLabel.setFont(UIConstants.TITLE_FONT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(300, 20));

        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(progressLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        mainPanel.add(progressPanel, BorderLayout.NORTH);

        // Search
        searchField = new JTextField("🔍 Search items");
        searchField.setPreferredSize(new Dimension(300, 40));
        mainPanel.add(searchField, BorderLayout.NORTH);

        // Checklist
        itemPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        loadItemsAndProgress();
        JScrollPane scroll = new JScrollPane(itemPanel);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // Bottom Actions
        JPanel bottomPanel = new JPanel(new BorderLayout());

        RoundedButton saveBtn = new RoundedButton("Save", UIConstants.PRIMARY_BLUE);
        saveBtn.setPreferredSize(new Dimension(150, 50));
        saveBtn.addActionListener(e -> saveChanges());

        JButton addBtn = new JButton("+ Add Item");
        addBtn.setPreferredSize(new Dimension(150, 50));
        addBtn.setBackground(UIConstants.PRIMARY_BLUE);
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> addNewItem());

        bottomPanel.add(saveBtn, BorderLayout.WEST);
        bottomPanel.add(addBtn, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadItemsAndProgress() {
        itemPanel.removeAll();
        List<Item> items = dao.getItemsByListId(listId);
        int total = items.size();
        int packed = 0;

        for (Item item : items) {
            if (item.isPacked()) packed++;
        }

        progressBar.setMaximum(total == 0 ? 1 : total);
        progressBar.setValue(packed);
        progressLabel.setText(packed + " of " + total + " items packed");

        if (items.isEmpty()) {
            JLabel emptyLabel = new JLabel("No items yet. Add some!");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setForeground(Color.GRAY);
            itemPanel.add(emptyLabel);
        } else {
            for (Item item : items) {
                itemPanel.add(createItemCard(item));
            }
        }

        itemPanel.revalidate();
        itemPanel.repaint();
    }

    private ShadowPanel createItemCard(Item item) {
        ShadowPanel card = new ShadowPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(300, 50));

        // Checkbox
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(item.isPacked());
        checkBox.addActionListener(e -> {
            item.setPacked(checkBox.isSelected());
            dao.setItemPackedStatus(item.getItemId(), item.isPacked());
            updateProgressBar();
        });
        card.add(checkBox, BorderLayout.WEST);

        // Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        JLabel itemName = new JLabel(item.getItemName() + (item.isPacked() ? " ✓" : ""));
        itemName.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(itemName, BorderLayout.CENTER);
        card.add(infoPanel, BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        JButton editBtn = new JButton("Edit");
        editBtn.setContentAreaFilled(false);
        editBtn.setForeground(UIConstants.PRIMARY_BLUE);
        editBtn.setFont(UIConstants.BODY_FONT.deriveFont(12f));
        editBtn.setBorderPainted(false);
        editBtn.addActionListener(e -> editItem(item));

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setForeground(Color.RED);
        deleteBtn.setFont(UIConstants.BODY_FONT.deriveFont(12f));
        deleteBtn.setBorderPainted(false);
        deleteBtn.addActionListener(e -> deleteItem(item));

        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        card.add(actionPanel, BorderLayout.EAST);

        return card;
    }

    private void updateProgressBar() {
        List<Item> items = dao.getItemsByListId(listId);
        int total = items.size();
        int packed = 0;

        for (Item item : items) {
            if (item.isPacked()) packed++;
        }

        progressBar.setMaximum(total == 0 ? 1 : total);
        progressBar.setValue(packed);
        progressLabel.setText(packed + " of " + total + " items packed");
    }

    private void saveChanges() {
        JOptionPane.showMessageDialog(this, "Changes saved!");
        loadItemsAndProgress();
    }

    private void addNewItem() {
        String newItemName = JOptionPane.showInputDialog(this, "Enter new item name:");
        if (newItemName != null && !newItemName.trim().isEmpty()) {
            Item newItem = new Item();
            newItem.setListId(listId);
            newItem.setItemName(newItemName);
            newItem.setCategory("General");
            newItem.setPacked(false);

            dao.addItemToList(newItem);
            loadItemsAndProgress();
        }
    }

    private void editItem(Item item) {
        String newName = JOptionPane.showInputDialog(this, "Edit item name:", item.getItemName());
        if (newName != null && !newName.trim().isEmpty()) {
            item.setItemName(newName);
            // You can add a DAO update method if you want it persistent
            loadItemsAndProgress();
        }
    }

    private void deleteItem(Item item) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete '" + item.getItemName() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dao.deleteItem(item.getItemId());
            loadItemsAndProgress();
        }
    }

    private JPanel createTopBar(String title, boolean showBack) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIConstants.PRIMARY_BLUE);
        bar.setPreferredSize(new Dimension(375, 60));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setFont(UIConstants.TITLE_FONT.deriveFont(18f));
        bar.add(titleLbl, BorderLayout.CENTER);

        if (showBack) {
            JButton back = new JButton("← Back");
            back.setForeground(Color.WHITE);
            back.setContentAreaFilled(false);
            back.setBorderPainted(false);
            back.addActionListener(e -> {
                new DashboardView();
                dispose();
            });
            bar.add(back, BorderLayout.WEST);
        }
        return bar;
    }
}
