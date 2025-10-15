package views;

import dao.PackingListDAO;
import utils.UIConstants;
import views.components.RoundedButton;
import views.components.RoundedTextField;
import views.components.ShadowPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import views.components.RoundedLabel;

public class CreateNewListView extends JFrame {
    private JComboBox<String> typeCombo;
    private JTextField nameField, destField, dateField;
    private JLabel weatherLabel;
    private JLabel templatesLabel;
    private ShadowPanel weatherPanel;
    private ShadowPanel templatesPanel;
    private PackingListDAO dao = new PackingListDAO();

    public CreateNewListView() {
        dao.initSchema();  // Ensure DB ready using your DatabaseConfig
        initializeUI();
        setTitle("Create New List - PackPal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        setPreferredSize(new Dimension(375, 667));

        JPanel topBar = createTopBar("Create New List", true);
        add(topBar, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 20, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Trip Name
        JLabel nameLabel = new JLabel("Trip name (e.g., Weekend Getaway)");
        nameLabel.setFont(UIConstants.BODY_FONT);
        gbc.gridy = 0;
        mainPanel.add(nameLabel, gbc);
        nameField = new RoundedTextField(20);
        nameField.setPreferredSize(new Dimension(300, 50));
        gbc.gridy++;
        mainPanel.add(nameField, gbc);

        // Destination
        JLabel destLabel = new JLabel("Destination");
        gbc.gridy++;
        mainPanel.add(destLabel, gbc);
        destField = new RoundedTextField(20);
        destField.setPreferredSize(new Dimension(300, 50));
        gbc.gridy++;
        mainPanel.add(destField, gbc);

        // Dates
        JLabel dateLabel = new JLabel("Dates");
        gbc.gridy++;
        mainPanel.add(dateLabel, gbc);
        dateField = new JTextField("dd/mm/yyyy");
        dateField.setPreferredSize(new Dimension(300, 50));
        gbc.gridy++;
        mainPanel.add(dateField, gbc);

        // Trip Type
        JLabel typeLabel = new JLabel("Trip Type");
        gbc.gridy++;
        mainPanel.add(typeLabel, gbc);
        String[] types = {"Beach", "Business", "Camping", "Weekend"};
        typeCombo = new JComboBox<>(types);
        typeCombo.setPreferredSize(new Dimension(300, 50));
        typeCombo.addActionListener(e -> refreshList());
        gbc.gridy++;
        mainPanel.add(typeCombo, gbc);

        // Weather Widget
        gbc.gridy++;
        weatherPanel = new ShadowPanel(new BorderLayout());
        weatherPanel.setPreferredSize(new Dimension(300, 80));
        weatherLabel = new JLabel("Sunny, 75°F - Suggested: Sunscreen, Hat");
        weatherLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        weatherPanel.add(weatherLabel, BorderLayout.CENTER);
        mainPanel.add(weatherPanel, gbc);

        // Generate Button
        RoundedButton generateBtn = new RoundedButton("Generate List", UIConstants.PRIMARY_BLUE);
        generateBtn.setPreferredSize(new Dimension(300, 50));
        generateBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String dest = destField.getText().trim();
            String dates = dateField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            if (!name.isEmpty() && !dest.isEmpty() && !dates.isEmpty() && type != null) {
                int listId = dao.createPackingList(name, dest, dates, type);
                if (listId > 0) {
                    JOptionPane.showMessageDialog(this, "List created with ID: " + listId);
                    new PackingListView(listId);  // Open editor with DB data
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create list. Check console.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
            }
        });
        gbc.gridy++;
        mainPanel.add(generateBtn, gbc);

        // Suggested Templates
        gbc.gridy++;
        templatesPanel = new ShadowPanel(new BorderLayout());
        templatesPanel.setPreferredSize(new Dimension(300, 100));
        templatesLabel = new JLabel("Suggested Templates\n• Beach Essentials\n• Adventure Kit");
        templatesLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        templatesPanel.add(templatesLabel, BorderLayout.CENTER);
        mainPanel.add(templatesPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        refreshList();
    }

    public void refreshList() {
        String selectedType = (String) typeCombo.getSelectedItem();
        if (selectedType == null) return;

        // Update Weather (Mock; extend with API)
        String weatherText = switch (selectedType) {
            case "Beach" -> "Sunny, 85°F - Suggested: Swimsuit, Towel";
            case "Business" -> "Cloudy, 65°F - Suggested: Laptop, Notebook";
            case "Camping" -> "Clear, 50°F - Suggested: Tent, Sleeping Bag";
            case "Weekend" -> "Partly Cloudy, 70°F - Suggested: Casual Clothes, Snacks";
            default -> "Loading weather...";
        };
        weatherLabel.setText(weatherText);
        weatherPanel.revalidate();
        weatherPanel.repaint();

        // Update Templates (Mock)
        List<String> templates = switch (selectedType) {
            case "Beach" -> Arrays.asList("Beach Essentials", "Summer Vacation Kit");
            case "Business" -> Arrays.asList("Work Trip Basics", "Professional Attire");
            case "Camping" -> Arrays.asList("Outdoor Gear", "Survival Essentials");
            case "Weekend" -> Arrays.asList("Quick Getaway", "Road Trip Pack");
            default -> Arrays.asList("Default Template");
        };
        StringBuilder sb = new StringBuilder("Suggested Templates\n");
        for (String t : templates) sb.append("• ").append(t).append("\n");
        templatesLabel.setText(sb.toString());
        templatesPanel.revalidate();
        templatesPanel.repaint();

        revalidate();
        repaint();
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