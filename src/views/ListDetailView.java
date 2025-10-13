package views;

import dao.PackingListDAO;
import models.Item;
import models.PackingList;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class ListDetailView extends JFrame {
    private User currentUser;
    private PackingList currentList;
    private PackingListDAO dao;

    private JLabel listNameLabel;
    private JTable itemsTable;
    private DefaultTableModel tableModel;

    public ListDetailView(User user, PackingList list) {
        this.currentUser = user;
        this.currentList = list;
        this.dao = new PackingListDAO();

        initUI();
        loadItems();
    }

    private void initUI() {
        setTitle("Packing List Details");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        listNameLabel = new JLabel("Packing List: " + currentList.getListName());
        listNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(listNameLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table for items
        tableModel = new DefaultTableModel(new Object[]{"Item Name", "Category", "Packed"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 2; // Allow editing category and packed
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Boolean.class; // checkbox
                return String.class;
            }
        };
        itemsTable = new JTable(tableModel);
        itemsTable.setRowHeight(30);
        JScrollPane tableScroll = new JScrollPane(itemsTable);
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addItemButton = new JButton("Add Item");
        JButton updateItemButton = new JButton("Update Item");
        JButton deleteItemButton = new JButton("Delete Item");

        addItemButton.addActionListener(e -> addItem());
        updateItemButton.addActionListener(e -> updateSelectedItem());
        deleteItemButton.addActionListener(e -> deleteSelectedItem());

        buttonPanel.add(addItemButton);
        buttonPanel.add(updateItemButton);
        buttonPanel.add(deleteItemButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadItems() {
        tableModel.setRowCount(0); // clear table
        List<Item> items = currentList.getItems();
        if (items == null) {
            items = dao.getPackingListById(currentList.getListId()).getItems();
        }

        if (items != null) {
            for (Item item : items) {
                tableModel.addRow(new Object[]{
                        item.getItemName(),
                        item.getCategory(),
                        item.isPacked()
                });
            }
        }
    }

    private void addItem() {
        String itemName = JOptionPane.showInputDialog(this, "Enter item name:");
        if (itemName == null || itemName.trim().isEmpty()) return;

        String category = JOptionPane.showInputDialog(this, "Enter item category:");
        if (category == null) category = "";

        Item newItem = new Item();
        newItem.setListId(currentList.getListId());
        newItem.setItemName(itemName.trim());
        newItem.setCategory(category.trim());
        newItem.setPacked(false);
        newItem.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        int itemId = dao.addItem(newItem);
        if (itemId > 0) {
            newItem.setItemId(itemId);
            currentList.getItems().add(newItem);
            tableModel.addRow(new Object[]{itemName, category, false});
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add item", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelectedItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an item to update", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String itemName = tableModel.getValueAt(selectedRow, 0).toString();
        String category = tableModel.getValueAt(selectedRow, 1).toString();
        boolean isPacked = (boolean) tableModel.getValueAt(selectedRow, 2);

        int itemId = dao.getItemIdByNameAndList(currentList.getListId(), itemName);
        if (itemId == -1) {
            JOptionPane.showMessageDialog(this, "Item not found in database", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update category
        boolean categoryUpdated = dao.updateItemCategory(itemId, category);
        // Update packed status
        boolean packedUpdated = dao.updateItemPackedStatus(itemId, isPacked);

        if (categoryUpdated || packedUpdated) {
            JOptionPane.showMessageDialog(this, "Item updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update item", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select an item to delete", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String itemName = tableModel.getValueAt(selectedRow, 0).toString();
        int itemId = dao.getItemIdByNameAndList(currentList.getListId(), itemName);
        if (itemId == -1) return;

        if (dao.deleteItem(itemId)) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Item deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete item", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
