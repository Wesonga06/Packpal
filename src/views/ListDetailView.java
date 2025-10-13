package views;

import dao.PackingListDAO;
import models.Item;
import models.PackingList;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListDetailView extends JFrame {

    private PackingList currentList;
    private User currentUser;
    private PackingListDAO dao;

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
        setTitle("Packing List Details - " + currentList.getListName());
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"Item Name", "Category", "Packed"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing category and packed columns
                return column == 1 || column == 2;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 2 ? Boolean.class : String.class;
            }
        };
        itemsTable = new JTable(tableModel);
        itemsTable.setRowHeight(25);

        // Listen for category changes and packed checkbox
        itemsTable.getModel().addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row >= 0 && col >= 0) {
                int itemId = dao.getItemIdByNameAndList(
                        tableModel.getValueAt(row, 0).toString(),
                        currentList.getListId()
                );
                if (col == 1) { // Category changed
                    String newCategory = tableModel.getValueAt(row, 1).toString();
                    dao.updateItemCategory(itemId, newCategory);
                } else if (col == 2) { // Packed changed
                    boolean packed = (boolean) tableModel.getValueAt(row, 2);
                    dao.updateItemPackedStatus(itemId, packed);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with Add Item
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField newItemField = new JTextField(20);
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"Clothing", "Electronics", "Toiletries", "Other"});
        JButton addButton = new JButton("Add Item");

        addButton.addActionListener(e -> {
            String itemName = newItemField.getText().trim();
            if (!itemName.isEmpty()) {
                Item item = new Item();
                item.setListId(currentList.getListId());
                item.setItemName(itemName);
                item.setCategory(categoryCombo.getSelectedItem().toString());
                item.setPacked(false);
                item.setPriority(0);
                item.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

                if (dao.addItem(item)) {
                    tableModel.addRow(new Object[]{itemName, item.getCategory(), item.isPacked()});
                    newItemField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add item", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        bottomPanel.add(new JLabel("Item Name:"));
        bottomPanel.add(newItemField);
        bottomPanel.add(new JLabel("Category:"));
        bottomPanel.add(categoryCombo);
        bottomPanel.add(addButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadItems() {
        tableModel.setRowCount(0);
        List<Item> items = dao.getItemsByListId(currentList.getListId());
        for (Item item : items) {
            tableModel.addRow(new Object[]{item.getItemName(), item.getCategory(), item.isPacked()});
        }
    }
}
