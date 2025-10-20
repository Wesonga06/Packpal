package views;

import controllers.DashboardController;
import dao.PackingListDAO;
import models.PackingList;
import models.User;
import utils.UIConstants;
import views.components.RoundedButton;
import views.components.ShadowPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

/**
 * Updated DashboardView with SwingWorker for thread-safe background data loading.
 */
public class DashboardView extends JFrame {
    private DashboardController controller;
    private JTabbedPane tabbedPane;
    private JPanel topBar;
    private JButton backButton;
    private List<RoundedButton> tabButtons;
    private User currentUser;
    private PackingListDAO dao;
    private JPanel myListsContentPanel;
    private JLabel loadingLabel; // new - shows loading message

    public DashboardView() {
        this.currentUser = new User();
        this.currentUser.setUserId(2);
        this.currentUser.setName("Max");
        this.currentUser.setEmail("max@gmail.com");

        this.dao = new PackingListDAO();
        this.controller = new DashboardController(this);

        initializeUI();
        setTitle("PackPal Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public DashboardView(User user) {
        this.currentUser = user;
        this.dao = new PackingListDAO();
        this.controller = new DashboardController(this);

        initializeUI();
        setTitle("PackPal Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        setPreferredSize(new Dimension(500, 700));
        setMinimumSize(new Dimension(450, 650));
        setResizable(true);

        topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setPreferredSize(new Dimension(500, 600));
        tabbedPane.addChangeListener(new TabChangeListener());

        JPanel myListsPanel = createMyListsPanel();
        tabbedPane.addTab(null, myListsPanel);

        JPanel settingsPanel = createSettingsPanel();
        tabbedPane.addTab(null, settingsPanel);

        tabbedPane.setSelectedIndex(0);
        updateTabLabels();

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIConstants.PRIMARY_BLUE);
        bar.setPreferredSize(new Dimension(500, 60));

        backButton = new JButton("← Back");
        backButton.setForeground(Color.WHITE);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setFont(UIConstants.BODY_FONT.deriveFont(14f));
        backButton.addActionListener(e -> controller.handleBackToWelcome());
        bar.add(backButton, BorderLayout.WEST);

        JPanel tabsPanel = new JPanel(new GridLayout(1, 2));
        tabsPanel.setOpaque(false);

        RoundedButton myListsTab = new RoundedButton("📋 My Lists", UIConstants.PRIMARY_BLUE);
        myListsTab.setForeground(Color.WHITE);
        myListsTab.setActionCommand("0");
        myListsTab.addActionListener(createTabActionListener(0));
        tabsPanel.add(myListsTab);

        RoundedButton settingsTab = new RoundedButton("⚙️ Settings", Color.WHITE);
        settingsTab.setForeground(UIConstants.PRIMARY_BLUE);
        settingsTab.setActionCommand("1");
        settingsTab.addActionListener(createTabActionListener(1));
        tabsPanel.add(settingsTab);

        bar.add(tabsPanel, BorderLayout.CENTER);
        tabButtons = Arrays.asList(myListsTab, settingsTab);
        return bar;
    }

    private ActionListener createTabActionListener(int tabIndex) {
        return e -> {
            tabbedPane.setSelectedIndex(tabIndex);
            updateTabLabels();
        };
    }

    private void updateTabLabels() {
        int selected = tabbedPane.getSelectedIndex();
        for (int i = 0; i < tabButtons.size(); i++) {
            RoundedButton tabBtn = tabButtons.get(i);
            if (i == selected) {
                tabBtn.setBackground(UIConstants.PRIMARY_BLUE);
                tabBtn.setForeground(Color.WHITE);
            } else {
                tabBtn.setBackground(Color.WHITE);
                tabBtn.setForeground(UIConstants.PRIMARY_BLUE);
            }
        }
        topBar.repaint();
    }

    private JPanel createMyListsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("My Packing Lists");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(UIConstants.PRIMARY_BLUE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

        myListsContentPanel = new JPanel(new BorderLayout());
        myListsContentPanel.setBackground(Color.WHITE);

        // Loading label (for thread feedback)
        loadingLabel = new JLabel("Loading your lists...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        loadingLabel.setForeground(Color.GRAY);
        myListsContentPanel.add(loadingLabel, BorderLayout.CENTER);

        // Load lists in a background thread
        loadPackingListsAsync();

        panel.add(myListsContentPanel, BorderLayout.CENTER);

        JButton addListBtn = new JButton("+ Create New List");
        addListBtn.setBackground(UIConstants.PRIMARY_BLUE);
        addListBtn.setForeground(Color.WHITE);
        addListBtn.setFocusPainted(false);
        addListBtn.setFont(new Font("Arial", Font.BOLD, 14));
        addListBtn.addActionListener(e -> {
            CreateListDialog dialog = new CreateListDialog(this, currentUser, this::loadPackingListsAsync);
            dialog.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addListBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Loads packing lists in a background thread (thread-safe).
     */
    private void loadPackingListsAsync() {
        myListsContentPanel.removeAll();
        myListsContentPanel.add(loadingLabel, BorderLayout.CENTER);
        myListsContentPanel.revalidate();
        myListsContentPanel.repaint();

        SwingWorker<List<PackingList>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<PackingList> doInBackground() throws Exception {
                // Background thread — fetch data from DB
                return dao.getPackingListsByUser(currentUser.getUserId());
            }

            @Override
            protected void done() {
                try {
                    List<PackingList> lists = get();
                    myListsContentPanel.removeAll();

                    if (lists.isEmpty()) {
                        JLabel emptyLabel = new JLabel("No packing lists yet. Create one to get started!");
                        emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                        emptyLabel.setForeground(Color.GRAY);
                        emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        myListsContentPanel.add(emptyLabel, BorderLayout.CENTER);
                    } else {
                        String[] columns = {"ID", "List Name", "Destination", "Trip Type", "Start Date"};
                        DefaultTableModel model = new DefaultTableModel(columns, 0) {
                            @Override
                            public boolean isCellEditable(int r, int c) { return false; }
                        };

                        for (PackingList list : lists) {
                            model.addRow(new Object[]{
                                list.getListId(),
                                list.getListName(),
                                list.getDestination(),
                                list.getTripType(),
                                list.getStartDate() != null ? list.getStartDate().toString() : "N/A"
                            });
                        }

                        JTable table = new JTable(model);
                        table.setRowHeight(30);
                        table.setFont(new Font("Arial", Font.PLAIN, 13));
                        table.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2) {
                                    int row = table.getSelectedRow();
                                    if (row != -1) {
                                        int listId = (int) table.getValueAt(row, 0);
                                        PackingList selected = dao.getPackingListById(listId);
                                        if (selected != null) {
                                            new ListDetailView(currentUser, selected).setVisible(true);
                                        }
                                    }
                                }
                            }
                        });

                        JScrollPane scrollPane = new JScrollPane(table);
                        myListsContentPanel.add(scrollPane, BorderLayout.CENTER);
                    }

                    myListsContentPanel.revalidate();
                    myListsContentPanel.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DashboardView.this, "Error loading lists: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel("⚙️ Settings Coming Soon...");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(UIConstants.PRIMARY_BLUE);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label);
        return panel;
    }

    private class TabChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            updateTabLabels();
        }
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public void navigateToWelcome() {
        new WelcomeView();
        dispose();
    }
}
