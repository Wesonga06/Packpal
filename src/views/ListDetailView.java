package views;

import dao.PackingListDAO;
import models.PackingList;
import models.Item;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import models.User;
import models.PackingList;

public class ListDetailView extends javax.swing.JFrame {
    private User currentUser;
    private PackingList currentList;
    private PackingListDAO packingListDAO;
    private PackingList packingList;
    private JLabel titleLabel, progressLabel;
    private JProgressBar progressBar;
    private JPanel itemPanel;
    private JScrollPane scrollPane;
    
    public ListDetailView(User currentUser, PackingList list){
        this.currentUser = currentUser;
        this.currentList = list;
        
        initComponents();
        
        setTitle("Packing List Details - " + list.getListName());
    }
    private void initComponents() {
    // ✅ Basic window settings
    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setSize(600, 400);
    setLocationRelativeTo(null);

    // ✅ Example layout components
    javax.swing.JPanel panel = new javax.swing.JPanel();
    javax.swing.JLabel titleLabel = new javax.swing.JLabel("Packing List Details");
    javax.swing.JLabel listNameLabel = new javax.swing.JLabel("List: " + currentList.getListName());
    javax.swing.JLabel userLabel = new javax.swing.JLabel("User: " + (currentUser != null ? currentUser.getName() : "Guest"));
    javax.swing.JLabel progressLabel = new javax.swing.JLabel("0 of 0 items packed");

    // Layout manager
    panel.setLayout(new java.awt.GridLayout(4, 1, 10, 10));
    panel.add(titleLabel);
    panel.add(listNameLabel);
    panel.add(userLabel);
    panel.add(progressLabel);

    add(panel);
}

    

    public ListDetailView(PackingList list) {
        this.packingListDAO = new PackingListDAO();
        this.packingList = list;

        setTitle("Packing List Details - " + list.getListName());
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ---------- Header ----------
        titleLabel = new JLabel(list.getListName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // ---------- Items Panel ----------
        itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(itemPanel);
        add(scrollPane, BorderLayout.CENTER);

        // ---------- Bottom Panel ----------
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));

        progressLabel = new JLabel();
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        bottomPanel.add(progressLabel, BorderLayout.NORTH);
        bottomPanel.add(progressBar, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        loadItemsAndProgress();

        setVisible(true);
    }

    /**
     * Loads all items and updates progress
     */
    private void loadItemsAndProgress() {
        itemPanel.removeAll();
        List<Item> items = packingListDAO.getItemsByListId(packingList.getListId());

        for (Item item : items) {
            JCheckBox checkBox = new JCheckBox(item.getItemName(), item.isPacked());
            checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));

            // Handle check/uncheck
            checkBox.addItemListener(e -> {
                boolean packed = checkBox.isSelected();
                packingListDAO.updateItemPackedStatus(item.getItemId(), packed);
                refreshProgress();  // refresh counts after toggle
            });

            itemPanel.add(checkBox);
        }

        updateProgressBar();
        itemPanel.revalidate();
        itemPanel.repaint();
    }

    /**
     * Refreshes the progress after any item update
     */
    private void refreshProgress() {
        packingList.setTotalItems(packingListDAO.getTotalItemsCount(packingList.getListId()));
        packingList.setPackedItemsCount(packingListDAO.getPackedItemsCount(packingList.getListId()));
        updateProgressBar();
    }

    /**
     * Updates progress bar and label text
     */
    private void updateProgressBar() {
        int total = packingListDAO.getTotalItemsCount(packingList.getListId());
        int packed = packingListDAO.getPackedItemsCount(packingList.getListId());

        progressLabel.setText("Packed " + packed + " of " + total + " items");
        progressBar.setMaximum(total);
        progressBar.setValue(packed);
    }
}
