package packpal.views;

import packpal.dao.UserDAO;
import packpal.models.User;
import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private static final Color PRIMARY_BLUE = new Color(70, 160, 255);
    private UserDAO userDAO;
    
    private JTextField emailField;
    private JPasswordField passwordField;
    
    public LoginView() {
        userDAO = new UserDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("PackPal - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        
        // Header
        RoundedLabel headerLabel = new RoundedLabel("Login");
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
        
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        passwordField.setMaximumSize(new Dimension(340, 45));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Sign in button
        JButton signInButton = createRoundedBlueButton("Sign in");
        signInButton.setMaximumSize(new Dimension(340, 50));
        signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signInButton.addActionListener(e -> handleLogin());
        
        // Create Account button
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setFont(new Font("Arial", Font.PLAIN, 14));
        createAccountButton.setForeground(PRIMARY_BLUE);
        createAccountButton.setBorderPainted(false);
        createAccountButton.setContentAreaFilled(false);
        createAccountButton.setFocusPainted(false);
        createAccountButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountButton.addActionListener(e -> {
            new RegisterView().setVisible(true);
            dispose();
        });
        
        // Forgot password panel
        JPanel forgotPanel = new JPanel(new BorderLayout());
        forgotPanel.setOpaque(false);
        forgotPanel.setMaximumSize(new Dimension(340, 40));
        forgotPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        
        JButton forgotPasswordButton = new JButton("Forgot password link");
        forgotPasswordButton.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotPasswordButton.setForeground(PRIMARY_BLUE);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setFocusPainted(false);
        forgotPasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.addActionListener(e -> {
            new ForgotPasswordView().setVisible(true);
            dispose();
        });
        
        forgotPanel.add(forgotPasswordButton, BorderLayout.CENTER);
        
        // Add components
        centerPanel.add(backButton);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(headerLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        centerPanel.add(emailLabel);
        centerPanel.add(emailField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(passwordLabel);
        centerPanel.add(passwordField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        centerPanel.add(signInButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(createAccountButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(forgotPanel);
        centerPanel.add(Box.createVerticalGlue());
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both email and password.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        User user = userDAO.loginUser(email, password);
        
        if (user != null) {
            JOptionPane.showMessageDialog(this,
                "Login successful! Welcome, " + user.getFullName() + "!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Open main app view
            new MyListsView(user).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid email or password.",
                "Login Failed",
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