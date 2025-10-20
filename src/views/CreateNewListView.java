package views;

import dao.PackingListDAO;
import services.WeatherService; 
import utils.UIConstants;
import views.components.RoundedButton;
import views.components.RoundedTextField;
import views.components.ShadowPanel;
import views.components.RoundedLabel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;


public class CreateNewListView extends JFrame {
    private JComboBox<String> typeCombo;
    private JTextField nameField, destField, dateField;
    private JLabel weatherLabel;
    private JLabel templatesLabel;
    private ShadowPanel weatherPanel;
    private ShadowPanel templatesPanel;
    private PackingListDAO dao = new PackingListDAO();

    public CreateNewListView() {
        
        new Thread(() -> dao.initSchema()).start();

        initializeUI();
        setTitle("Create New List - PackPal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        setPreferredSize(new Dimension(375, 667));
        setLayout(new BorderLayout());

        JPanel topBar = createTopBar("Create New List", true);
        add(topBar, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 20, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Trip name (e.g., Weekend Getaway)");
        nameLabel.setFont(UIConstants.BODY_FONT);
        gbc.gridy = 0;
        mainPanel.add(nameLabel, gbc);

        nameField = new RoundedTextField(20);
        nameField.setPreferredSize(new Dimension(300, 50));
        gbc.gridy++;
        mainPanel.add(nameField, gbc);

        JLabel destLabel = new JLabel("Destination (city)");
        gbc.gridy++;
        mainPanel.add(destLabel, gbc);

        destField = new RoundedTextField(20);
        destField.setPreferredSize(new Dimension(300, 50));
        gbc.gridy++;
        mainPanel.add(destField, gbc);

        JLabel dateLabel = new JLabel("Dates");
        gbc.gridy++;
        mainPanel.add(dateLabel, gbc);

        dateField = new JTextField("dd/mm/yyyy");
        dateField.setPreferredSize(new Dimension(300, 50));
        gbc.gridy++;
        mainPanel.add(dateField, gbc);

        JLabel typeLabel = new JLabel("Trip Type");
        gbc.gridy++;
        mainPanel.add(typeLabel, gbc);

        String[] types = {"Beach", "Business", "Camping", "Weekend"};
        typeCombo = new JComboBox<>(types);
        typeCombo.setPreferredSize(new Dimension(300, 50));
        typeCombo.addActionListener(e -> refreshListInBackground());
        gbc.gridy++;
        mainPanel.add(typeCombo, gbc);

        gbc.gridy++;
        weatherPanel = new ShadowPanel(new BorderLayout());
        weatherPanel.setPreferredSize(new Dimension(300, 80));
        weatherLabel = new JLabel("Loading weather...");
        weatherLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        weatherPanel.add(weatherLabel, BorderLayout.CENTER);
        mainPanel.add(weatherPanel, gbc);

        RoundedButton generateBtn = new RoundedButton("Generate List", UIConstants.PRIMARY_BLUE);
        generateBtn.setPreferredSize(new Dimension(300, 50));
        generateBtn.addActionListener(e -> createListInBackground());
        gbc.gridy++;
        mainPanel.add(generateBtn, gbc);

        gbc.gridy++;
        templatesPanel = new ShadowPanel(new BorderLayout());
        templatesPanel.setPreferredSize(new Dimension(300, 100));
        templatesLabel = new JLabel("Loading templates...");
        templatesLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        templatesPanel.add(templatesLabel, BorderLayout.CENTER);
        mainPanel.add(templatesPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        refreshListInBackground();
    }

    
    private void createListInBackground() {
        String name = nameField.getText().trim();
        String dest = destField.getText().trim();
        String dates = dateField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();

        if (name.isEmpty() || dest.isEmpty() || dates.isEmpty() || type == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() {
                return dao.createPackingList(name, dest, dates, type);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    int listId = get();
                    if (listId > 0) {
                        JOptionPane.showMessageDialog(CreateNewListView.this,
                                "List created successfully! ID: " + listId);
                        new PackingListView(listId);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(CreateNewListView.this,
                                "Failed to create list. Check console.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CreateNewListView.this,
                            "Error creating list: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    
    private void refreshListInBackground() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private String weatherText;
            private StringBuilder templatesText;

            @Override
            protected Void doInBackground() {
                String selectedType = (String) typeCombo.getSelectedItem();
                String city = destField.getText().trim();

                // Fetch weather (real API if destination entered)
                if (!city.isEmpty()) {
                    weatherText = WeatherService.getWeather(city);
                } else {
                    weatherText = "Enter a destination to load weather.";
                }

                // Template suggestions based on trip type
                List<String> templates = switch (selectedType) {
                    case "Beach" -> Arrays.asList("Beach Essentials", "Summer Vacation Kit");
                    case "Business" -> Arrays.asList("Work Trip Basics", "Professional Attire");
                    case "Camping" -> Arrays.asList("Outdoor Gear", "Survival Essentials");
                    case "Weekend" -> Arrays.asList("Quick Getaway", "Road Trip Pack");
                    default -> Arrays.asList("Default Template");
                };

                templatesText = new StringBuilder("<html>Suggested Templates:<br>");
                for (String t : templates) templatesText.append("• ").append(t).append("<br>");
                templatesText.append("</html>");
                return null;
            }

            @Override
            protected void done() {
                weatherLabel.setText(weatherText);
                templatesLabel.setText(templatesText.toString());
                weatherPanel.revalidate();
                templatesPanel.revalidate();
            }
        };
        worker.execute();
    }

    private JPanel createTopBar(String title, boolean showBack) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIConstants.PRIMARY_BLUE);
        bar.setPreferredSize(new Dimension(375, 60));

        RoundedLabel titleLbl = new RoundedLabel(title);
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
