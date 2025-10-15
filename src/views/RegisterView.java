package views;

import controllers.RegisterController;
import utils.UIConstants;
import views.components.RoundedButton;
import views.components.RoundedTextField;

import javax.swing.*;
import java.awt.*;
import views.components.RoundedLabel;

public class RegisterView extends JFrame {
    private RegisterController controller;
    private RoundedTextField emailField;
    private JPasswordField passwordField;
    private RoundedButton signUpButton;

    public RegisterView() {
        controller = new RegisterController(this);
        initializeUI();
        setTitle("Sign Up - PackPal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUI() {
        setPreferredSize(new Dimension(375, 667));

        JPanel topBar = createTopBar("Sign Up", true);
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
        gbc.gridy = 0;
        mainPanel.add(emailLabel, gbc);
        emailField = new RoundedTextField(20);
        emailField.setPreferredSize(new Dimension(300, 50));
        gbc.gridy++;
        mainPanel.add(emailField, gbc);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(UIConstants.BODY_FONT);
        gbc.gridy++;
        mainPanel.add(passLabel, gbc);
        passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(300, 50));
        gbc.gridy++;
        mainPanel.add(passwordField, gbc);

        // Sign Up
        signUpButton = new RoundedButton("Create Account", UIConstants.PRIMARY_BLUE);
        signUpButton.setPreferredSize(new Dimension(300, 50));
        signUpButton.addActionListener(e -> controller.handleSignUp(emailField.getText(), new String(passwordField.getPassword())));
        gbc.gridy++;
        mainPanel.add(signUpButton, gbc);

        // Have Account
        JButton loginBtn = new JButton("I have an account");
        loginBtn.setContentAreaFilled(false);
        loginBtn.setForeground(UIConstants.PRIMARY_BLUE);
        loginBtn.setFont(UIConstants.BODY_FONT);
        loginBtn.addActionListener(e -> {
            new LoginView();
            dispose();
        });
        gbc.gridy++;
        mainPanel.add(loginBtn, gbc);

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