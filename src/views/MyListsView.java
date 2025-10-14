package views;

import dao.PackingListDAO;
import models.PackingList;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MyListsView extends JFrame {

    private User user;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnView, btnDelete, btnRefresh;
    private List<PackingList> lists;

    public MyListsView(User user) {
        this.user = user;
        setTitle("My Packing Lists");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeUI();
        loadLists();
    }

    private void initializeUI() {
        // ✅ Table model setup
        String[] columnNames = {"List ID", "Destination", "Start Date", "End Date", "Trip Type", "Items Packed"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);

        // ✅ Buttons setup
        btnAdd = new JButton("➕ Add New List");
        btnView = new JButton("👁️ View Details");
        btnDelete = new JButton("🗑️ Delete");
        btnRefresh = new JButton("🔄 Refresh");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnView);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // ✅ Button actions
        btnAdd.addActionListener(e -> openCreateList());
        btnView.addActionListener(e -> openSelectedList());
        btnDelete.addActionListener(e -> deleteSelectedList());
        btnRefresh.addActionListener(e -> loadLists());

        // ✅ Double-click to open details
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    openSelectedList();
                }
            }
        });
    }

    private void loadLists() {
        try {
            PackingListDAO packingListDAO = new PackingListDAO();
            lists = packingListDAO.getPackingListsByUser(user.getUserId());

            tableModel.setRowCount(0); // clear table
            for (PackingList list : lists) {
                Object[] row = {
                    list.getListId(),
                    list.getDestination(),
                    list.getStartDate(),
                    list.getEndDate(),
                    list.getTripType(),
                    list.getPackedItemsCount() + " / " + list.getTotalItems()
                };
                tableModel.addRow(row);
            }

            System.out.println("✅ Loaded " + lists.size() + " packing lists for user " + user.getName());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading lists: " + ex.getMessage());
        }
    }

    private void openCreateList() {
        new CreateListDialog(user).setVisible(true);
        this.dispose();
    }

    private void openSelectedList() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a list to view.");
            return;
        }

        PackingList selectedList = lists.get(selectedRow);
        new ListDetailView(user, selectedList).setVisible(true);
        this.dispose();
    }

    private void deleteSelectedList() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a list to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this list?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int listId = (int) tableModel.getValueAt(selectedRow, 0);
                PackingListDAO dao = new PackingListDAO();
                boolean deleted = dao.deletePackingList(listId);

                if (deleted) {
                    JOptionPane.showMessageDialog(this, "List deleted successfully.");
                    loadLists();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete the list.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting list: " + e.getMessage());
            }
        }
    }

  
}

