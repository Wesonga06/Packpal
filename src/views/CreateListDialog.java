package views;

import models.User;
import models.PackingList;
import dao.PackingListDAO;
import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class CreateListDialog extends JDialog {
    private static final Color PRIMARY_BLUE = new Color(70, 160, 255);
    private User currentUser;
    private PackingListDAO dao;
    private JFrame parentFrame;
    
    private JTextField listNameField;
    private JTextField descriptionField;
    private JTextField destinationField;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JComboBox<String> tripTypeCombo;
    private JTextField nameField;  // Fixed: Changed from Object to JTextField
    
    public CreateListDialog(JFrame parent, User user) {
        super(parent, "Create New List", true);
        this.parentFrame = parent;
        this.currentUser = user;
        this.dao = new PackingListDAO();
        initUI();
    }
    
    private void initUI() {
        setSize(450, 600);
        setLocationRelativeTo(parentFrame);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        mainPanel.setBackground(Color.WHITE);
        
        // Back button
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setForeground(PRIMARY_BLUE);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> dispose());
        
        // Title
        JLabel titleLabel = new JLabel("Create New List");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Trip name
        JLabel nameLabel = createLabel("Trip name (e.g. Weekend Getaway)");
        listNameField = createTextField();
        nameField = listNameField;  // Assign to nameField if needed for compatibility
        
        // Description
        JLabel descriptionLabel = createLabel("Description");
        descriptionField = createTextField();
        
        // Destination
        JLabel destinationLabel = createLabel("Destination");
        destinationField = createTextField();
        
        // Start date
        JLabel startDateLabel = createLabel("MM / dd / yyyy");
        SpinnerDateModel startModel = new SpinnerDateModel();
        startDateSpinner = new JSpinner(startModel);
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "MM/dd/yyyy");
        startDateSpinner.setEditor(startEditor);
        startDateSpinner.setMaximumSize(new Dimension(370, 45));
        startDateSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // End date
        JLabel endDateLabel = createLabel("MM / dd / yyyy");
        SpinnerDateModel endModel = new SpinnerDateModel();
        endDateSpinner = new JSpinner(endModel);
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDateSpinner, "MM/dd/yyyy");
        endDateSpinner.setEditor(endEditor);
        endDateSpinner.setMaximumSize(new Dimension(370, 45));
        endDateSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Trip type
        JLabel tripTypeLabel = createLabel("Trip Type");
        String[] tripTypes = {"Weekend", "Business", "Beach", "Camping", "Adventure", "Other"};
        tripTypeCombo = new JComboBox<>(tripTypes);
        tripTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        tripTypeCombo.setMaximumSize(new Dimension(370, 45));
        tripTypeCombo.setBackground(Color.WHITE);
        
        // Generate List button
        JButton generateButton = createBlueButton("Generate List");
        generateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateButton.addActionListener(e -> generateList());
        
        // Add components
        mainPanel.add(backButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(nameLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(listNameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(descriptionLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(descriptionField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(destinationLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(destinationField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(startDateLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(startDateSpinner);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(endDateLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(endDateSpinner);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(tripTypeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(tripTypeCombo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(generateButton);
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(370, 45));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        return field;
    }
    
    private JButton createBlueButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_BLUE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(300, 50));
        return button;
    }
    
    private void generateList() {
    String listName = listNameField.getText().trim();
    String description = descriptionField.getText().trim();
    String destination = destinationField.getText().trim();
    String tripType = tripTypeCombo.getSelectedItem().toString();

    if (listName.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please enter a trip name.",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Create and populate model
    PackingList list = new PackingList();
    list.setUserId(currentUser.getUserId());
    list.setListName(listName);
    list.setDescription(description);
    list.setDestination(destination);
    list.setTripType(tripType);

    java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
    java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();
    list.setStartDate(new Date(startDate.getTime()));
    list.setEndDate(new Date(endDate.getTime()));

    // Call DAO to save it
    int listId = dao.createPackingList(list);
    if (listId > 0) {
        JOptionPane.showMessageDialog(this, "Packing List created successfully!");
        dispose(); // Close dialog after success
    } else {
        JOptionPane.showMessageDialog(this, "Failed to create packing list.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    // Removed erroneous static inner classes; add dateField if needed
    private JTextField dateField = new JTextField();  // Added if missing; adjust as needed
}