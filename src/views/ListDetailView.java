package views;

import dao.PackingListDAO;
import models.Item;
import models.PackingList;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ListDetailView extends JFrame {

    private User currentUser;
    private PackingList selectedList;
    private JTable itemTable;
    private JLabel lblPackedStatus;
    private PackingListDAO packingListDAO;

    public ListDetailView(User user, PackingList list) {
        this.currentUser = user;
        this.selectedList = list;
        this.packingListDAO = new PackingListDAO();

        initComponents();
        initUI();
        loadItems();
    }

    // Fallback constructor for older calls
    public ListDetailView(PackingList list) {
        this.selectedList = list;
        this.packingListDAO = new PackingListDAO();

        initComponents();
        initUI();
        loadItems();
    }

    private void initComponents() {
        setTitle("Packing List Details - " + selectedList.getListName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel titleLabel = new JLabel(selectedList.getListName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        itemTable = new JTable(new DefaultTableModel(
            new Object[]{"Item Name", "Category", "Priority", "Packed"}, 0
        ));
        JScrollPane scrollPane = new JScrollPane(itemTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addBtn = new JButton("Add Item");
        JButton deleteBtn = new JButton("Delete Item");
        JButton markPackedBtn = new JButton("Mark as Packed");
        JButton refreshBtn = new JButton("Refresh");

        lblPackedStatus = new JLabel("0 of 0 items packed");

        bottomPanel.add(addBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(markPackedBtn);
        bottomPanel.add(refreshBtn);
        bottomPanel.add(lblPackedStatus);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Add panel to frame
        add(panel);

        // Button listeners
        addBtn.addActionListener(e -> addItem());
        deleteBtn.addActionListener(e -> deleteSelectedItem());
        markPackedBtn.addActionListener(e -> markItemAsPacked());
        refreshBtn.addActionListener(e -> loadItems());
    }

    private void loadItems() {
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        model.setRowCount(0);

        List<Item> items = packingListDAO.getItemsByListId(selectedList.getListId());
        int packedCount = 0;

        for (Item item : items) {
            model.addRow(new Object[]{
                item.getItemName(),
                item.getCategory(),
                item.getPriority(),
                item.isPacked() ? "✅" : "❌"
            });
            if (item.isPacked()) packedCount++;
        }

        lblPackedStatus.setText(packedCount + " of " + items.size() + " items packed");
    }

    private void addItem() {
        String itemName = JOptionPane.showInputDialog(this, "Enter item name:");
        if (itemName == null || itemName.trim().isEmpty()) return;

        String category = JOptionPane.showInputDialog(this, "Enter category:");
        if (category == null) category = "General";

        int priority = 1;
        try {
            String pStr = JOptionPane.showInputDialog(this, "Enter priority (1-5):", "1");
            if (pStr != null && !pStr.isEmpty()) {
                priority = Integer.parseInt(pStr);
            }
        } catch (NumberFormatException ignored) {}

        Item item = new Item();
        item.setListId(selectedList.getListId());
        item.setItemName(itemName);
        item.setCategory(category);
        item.setPriority(priority);
        item.setPacked(false);
        item.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        if (packingListDAO.addItem(item)) {
            JOptionPane.showMessageDialog(this, "Item added successfully!");
            loadItems();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add item.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedItem() {
        int row = itemTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
            return;
        }

        String itemName = (String) itemTable.getValueAt(row, 0);
        int itemId = packingListDAO.getItemIdByNameAndList(itemName, selectedList.getListId());

        int confirm = JOptionPane.showConfirmDialog(this, "Delete '" + itemName + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            packingListDAO.deleteItem(itemId);
            loadItems();
        }
    }

    private void markItemAsPacked() {
        int row = itemTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to mark as packed.");
            return;
        }

        String itemName = (String) itemTable.getValueAt(row, 0);
        int itemId = packingListDAO.getItemIdByNameAndList(itemName, selectedList.getListId());

        packingListDAO.updateItemPackedStatus(itemId, true);
        loadItems();
    }
}

