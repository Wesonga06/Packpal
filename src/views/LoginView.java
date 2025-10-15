package views;

import controllers.LoginController;
import utils.UIConstants;
import views.components.RoundedButton;
import views.components.RoundedTextField;

import javax.swing.*;
import java.awt.*;
import views.components.RoundedLabel;

public class LoginView extends JFrame {
    private LoginController controller;
    private RoundedTextField emailField;
    private JPasswordField passwordField;
    private RoundedButton signInButton;

    public LoginView() {
        controller = new LoginController(this);
        initializeUI();
        setTitle("Login - PackPal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        setPreferredSize(new Dimension(375, 667));

        JPanel topBar = createTopBar("Login", true);
        add(topBar, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 20, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Email
        JLabel emailLabel = new JLabel("Email address");
        emailLabel.setFont(UIConstants.BODY_FONT);
        emailLabel.setForeground(Color.BLACK);
        gbc.gridy = 0;
        mainPanel.add(emailLabel, gbc);
        emailField = new RoundedTextField(20);
        emailField.setPreferredSize(new Dimension(300, 50));
        gbc.gridy++;
        mainPanel.add(emailField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UIConstants.BODY_FONT);
        passLabel.setForeground(Color.BLACK);
        gbc.gridy++;
        mainPanel.add(passLabel, gbc);
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(300, 50));
        gbc.gridy++;
        mainPanel.add(passwordField, gbc);

        // Sign In
        signInButton = new RoundedButton("Sign In", UIConstants.PRIMARY_BLUE);
        signInButton.setPreferredSize(new Dimension(300, 50));
        signInButton.addActionListener(e -> controller.handleSignIn(emailField.getText(), new String(passwordField.getPassword())));
        gbc.gridy++;
        mainPanel.add(signInButton, gbc);

        // Create Account
        JButton createBtn = new JButton("Create Account");
        createBtn.setContentAreaFilled(false);
        createBtn.setForeground(UIConstants.PRIMARY_BLUE);
        createBtn.setFont(UIConstants.BODY_FONT);
        createBtn.addActionListener(e -> {
            new RegisterView();
            dispose();
        });
        gbc.gridy++;
        mainPanel.add(createBtn, gbc);

        // Forgot Password
        JButton forgotBtn = new JButton("Forgot password?");
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setForeground(Color.GRAY);
        forgotBtn.setFont(UIConstants.BODY_FONT.deriveFont(12f));
        forgotBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Password reset link sent to your email!"));
        gbc.gridy++;
        mainPanel.add(forgotBtn, gbc);

        // Social Login Placeholder
        gbc.gridy++;
        gbc.gridwidth = 2;
        JLabel socialLabel = new JLabel("Or continue with");
        socialLabel.setHorizontalAlignment(SwingConstants.CENTER);
        socialLabel.setForeground(Color.GRAY);
        mainPanel.add(socialLabel, gbc);

        add(mainPanel, BorderLayout.CENTER);
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
            back.setFocusPainted(false);
            back.addActionListener(e -> {
                new WelcomeView();
                dispose();
            });
            bar.add(back, BorderLayout.WEST);
        }
        return bar;
    }
}