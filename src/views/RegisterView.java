package packpal.views;

import packpal.dao.UserDAO;
import javax.swing.*;
import java.awt.*;

public class RegisterView extends JFrame {
    private static final Color PRIMARY_BLUE = new Color(70, 160, 255);
    private UserDAO userDAO;
    
    private JTextField fullNameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    
    public RegisterView() {
        userDAO = new UserDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("PackPal - Create Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        
        // Header
        RoundedLabel headerLabel = new RoundedLabel("Create Account");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBackground(PRIMARY_BLUE);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerLabel.setMaximumSize(new Dimension(340, 55));
        headerLabel.setPreferredSize(new Dimension(340, 55));
        
        // Back button
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setForeground(PRIMARY_BLUE);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.addActionListener(e -> {
            new WelcomeView().setVisible(true);
            dispose();
        });
        
        // Full Name field
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        
        fullNameField = createStyledTextField();
        fullNameField.setMaximumSize(new Dimension(340, 45));
        fullNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Email field
        JLabel emailLabel = new JLabel("Email address");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 13));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        
        emailField = createStyledTextField();
        emailField.setMaximumSize(new Dimension(340, 45));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 13));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        
        passwordField = createPasswordField();
        passwordField.setMaximumSize(new Dimension(340, 45));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Confirm Password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setFont(new Font("Arial", Font.BOLD, 13));
        confirmPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmPasswordLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        
        confirmPasswordField = createPasswordField();
        confirmPasswordField.setMaximumSize(new Dimension(340, 45));
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Register button
        JButton registerButton = createRoundedBlueButton("Create Account");
        registerButton.setMaximumSize(new Dimension(340, 50));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> handleRegister());
        
        // Login link
        JPanel loginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginPanel.setOpaque(false);
        loginPanel.setMaximumSize(new Dimension(340, 30));
        
        JLabel loginTextLabel = new JLabel("Already have an account?");
        loginTextLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        loginTextLabel.setForeground(Color.DARK_GRAY);
        
        JButton loginButton = new JButton("Sign in");
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));
        loginButton.setForeground(PRIMARY_BLUE);
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> {
            new LoginView().setVisible(true);
            dispose();
        });
        
        loginPanel.add(loginTextLabel);
        loginPanel.add(loginButton);
        
        // Add components
        centerPanel.add(backButton);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(headerLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        centerPanel.add(nameLabel);
        centerPanel.add(fullNameField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(emailLabel);
        centerPanel.add(emailField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(passwordLabel);
        centerPanel.add(passwordField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(confirmPasswordLabel);
        centerPanel.add(confirmPasswordField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        centerPanel.add(registerButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(loginPanel);
        centerPanel.add(Box.createVerticalGlue());
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 6 characters.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if email already exists
        if (userDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(this,
                "An account with this email already exists.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Register user
        if (userDAO.registerUser(email, password, fullName)) {
            JOptionPane.showMessageDialog(this,
                "Account created successfully!\nPlease login to continue.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            new LoginView().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Registration failed. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return field;
    }
    
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return field;
    }
    
    private JButton createRoundedBlueButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_BLUE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    class RoundedLabel extends JLabel {
        public RoundedLabel(String text) {
            super(text, SwingConstants.CENTER);
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}