package views;

import controllers.DashboardController;
import dao.PackingListDAO;
import models.PackingList;
import models.User;
import services.WeatherService;
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
import services.WeatherService.WeatherData;


public class DashboardView extends JFrame {
    private DashboardController controller;
    private JTabbedPane tabbedPane;
    private JPanel topBar;
    private JButton backButton;
    private List<RoundedButton> tabButtons;
    private User currentUser;
    private PackingListDAO dao;
    private JPanel myListsContentPanel;

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

        // Top Bar
        topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        // Tabbed Content
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.setPreferredSize(new Dimension(500, 600));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tabbedPane.addChangeListener(new TabChangeListener());

        // Add My Lists Tab
        JPanel myListsPanel = createMyListsPanel();
        tabbedPane.addTab(null, myListsPanel);

        // Add Settings Tab
        JPanel settingsPanel = createSettingsPanel();
        tabbedPane.addTab(null, settingsPanel);

        // Set Initial Tab
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
        myListsTab.setPreferredSize(new Dimension(200, 40));
        myListsTab.setActionCommand("0");
        myListsTab.addActionListener(createTabActionListener(0));
        tabsPanel.add(myListsTab);

        RoundedButton settingsTab = new RoundedButton("⚙️ Settings", Color.WHITE);
        settingsTab.setForeground(UIConstants.PRIMARY_BLUE);
        settingsTab.setPreferredSize(new Dimension(200, 40));
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
                tabBtn.setFont(UIConstants.TITLE_FONT.deriveFont(Font.BOLD));
                tabBtn.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedButton.UnderlineBorder(Color.WHITE, 3),
                        tabBtn.getBorder()
                ));
            } else {
                tabBtn.setBackground(Color.WHITE);
                tabBtn.setForeground(UIConstants.PRIMARY_BLUE);
                tabBtn.setFont(UIConstants.BODY_FONT);
                tabBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
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
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        myListsContentPanel = new JPanel(new BorderLayout());
        myListsContentPanel.setBackground(Color.WHITE);
        loadPackingLists();
        panel.add(myListsContentPanel, BorderLayout.CENTER);

        JButton addListBtn = new JButton("+ Create New List");
        addListBtn.setBackground(UIConstants.PRIMARY_BLUE);
        addListBtn.setForeground(Color.WHITE);
        addListBtn.setFocusPainted(false);
        addListBtn.setFont(new Font("Arial", Font.BOLD, 14));
        addListBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addListBtn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        addListBtn.addActionListener(e -> {
            CreateListDialog dialog = new CreateListDialog(this, currentUser, this::loadPackingLists);
            dialog.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(addListBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadPackingLists() {
        myListsContentPanel.removeAll();
        List<PackingList> lists = dao.getPackingListsByUser(currentUser.getUserId());

        if (lists.isEmpty()) {
            JLabel emptyLabel = new JLabel("No packing lists yet. Create one to get started!");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));
            myListsContentPanel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            String[] columns = {"ID", "List Name", "Destination", "Trip Type", "Start Date"};
            DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (PackingList list : lists) {
                Object[] row = {
                        list.getListId(),
                        list.getListName(),
                        list.getDestination(),
                        list.getTripType(),
                        list.getStartDate() != null ? list.getStartDate().toString() : "N/A"
                };
                tableModel.addRow(row);
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);
            table.setFont(new Font("Arial", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
            table.getTableHeader().setBackground(new Color(240, 240, 240));
            table.setSelectionBackground(new Color(230, 240, 255));
            table.setGridColor(new Color(220, 220, 220));

            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int row = table.getSelectedRow();
                        if (row != -1) {
                            int listId = (int) table.getValueAt(row, 0);
                            PackingList selectedList = dao.getPackingListById(listId);
                            if (selectedList != null) {
                                showWeatherForDestination(selectedList.getDestination());
                                new ListDetailView(currentUser, selectedList).setVisible(true);
                            }
                        }
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            myListsContentPanel.add(scrollPane, BorderLayout.CENTER);
        }

        myListsContentPanel.revalidate();
        myListsContentPanel.repaint();
    }

    // 🌤️ WEATHER THREAD IMPLEMENTATION
    private void showWeatherForDestination(String destination) {
        if (destination == null || destination.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No destination found for this list.",
                    "Weather Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SwingWorker<WeatherData, Void> worker = new SwingWorker<>() {
            @Override
            protected WeatherData doInBackground() throws Exception {
                WeatherService service = new WeatherService();
                return service.getCurrentWeather(destination);
            }

            @Override
            protected void done() {
                try {
                    WeatherData data = get();
                    if (data != null) {
                        String info = String.format(
                                "🌍 City: %s (%s)\n🌡 Temperature: %.1f°C (Feels like %.1f°C)\n" +
                                "💧 Humidity: %d%%\n💨 Wind: %.1f m/s\n☁️ Condition: %s %s",
                                data.getCity(), data.getCountry(), data.getTemperature(), data.getFeelsLike(),
                                data.getHumidity(), data.getWindSpeed(), data.getDescription(),
                                data.getWeatherEmoji()
                        );

                        JOptionPane.showMessageDialog(DashboardView.this, info,
                                "Current Weather", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(DashboardView.this,
                                "Could not fetch weather data for " + destination,
                                "Weather Info", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DashboardView.this,
                            "Error fetching weather: " + ex.getMessage(),
                            "Weather Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute(); // start background thread
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel info = new JLabel("Settings will appear here soon...");
        info.setHorizontalAlignment(SwingConstants.CENTER);
        info.setFont(new Font("Arial", Font.PLAIN, 14));
        info.setForeground(Color.GRAY);
        panel.add(info, BorderLayout.CENTER);

        return panel;
    }

    private class TabChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            updateTabLabels();
        }
    }

    public JTabbedPane getTabbedPane() { return tabbedPane; }

    public void navigateToWelcome() {
        new WelcomeView();
        dispose();
    }
}