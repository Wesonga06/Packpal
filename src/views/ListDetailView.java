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

    private User user;
    private PackingList packingList;
    private PackingListDAO packingListDAO;
    private JPanel itemPanel;
    private JLabel summaryLabel;

    // ✅ Constructor with User + PackingList
    public ListDetailView(User user, PackingList packingList) {
        this.user = user;
        this.packingList = packingList;
        this.packingListDAO = new PackingListDAO();

        initUI();
    }

    // ✅ You can still keep this simple constructor for backward compatibility
    public ListDetailView(PackingList packingList) {
        this(null, packingList); // call the main constructor
    }

    // ===========================
    // UI Initialization
    // ===========================
    private void initUI() {
        setTitle("Packing List - " + packingList.getListName());
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel(packingList.getListName(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Item panel
        itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(itemPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Summary label
        summaryLabel = new JLabel("", SwingConstants.CENTER);
        summaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(summaryLabel, BorderLayout.SOUTH);

        // Load data
        loadItems();
        updateSummary();
    }

    // ===========================
    // Load Items
    // ===========================
    private void loadItems() {
        itemPanel.removeAll();

        List<Item> items = packingListDAO.getItemsByListId(packingList.getListId());
        for (Item item : items) {
            JCheckBox checkBox = new JCheckBox(item.getItemName());
            checkBox.setSelected(item.isPacked());

            checkBox.addItemListener(e -> {
                boolean packed = checkBox.isSelected();
                packingListDAO.updateItemPackedStatus(item.getItemId(), packed);
                updateSummary(); // refresh summary each time
            });

            itemPanel.add(checkBox);
        }

        itemPanel.revalidate();
        itemPanel.repaint();
    }

    // ===========================
    // Update Summary
    // ===========================
    private void updateSummary() {
        List<Item> items = packingListDAO.getItemsByListId(packingList.getListId());
        long packedCount = items.stream().filter(Item::isPacked).count();
        long totalCount = items.size();

        summaryLabel.setText("Packed: " + packedCount + " of " + totalCount + " items");
    }
}

