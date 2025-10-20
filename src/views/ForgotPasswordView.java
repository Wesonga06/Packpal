package views;

import controllers.ForgotPasswordController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ForgotPasswordView extends JFrame {

    private JTextField emailField;
    private JButton resetButton;
    private JButton backButton;
    private JLabel messageLabel;

    public ForgotPasswordView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Forgot Password");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel emailLabel = new JLabel("Enter your registered email:");
        emailField = new JTextField();
        resetButton = new JButton("Reset Password");
        backButton = new JButton("Back to Login");
        messageLabel = new JLabel("", SwingConstants.CENTER);

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(resetButton);
        panel.add(backButton);

        add(panel, BorderLayout.CENTER);
        add(messageLabel, BorderLayout.SOUTH);

        // Controller for button actions
        ForgotPasswordController controller = new ForgotPasswordController(this);

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.handlePasswordReset();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.handleBackToLogin();
            }
        });
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public void showMessage(String message) {
        messageLabel.setText(message);
    }
}

