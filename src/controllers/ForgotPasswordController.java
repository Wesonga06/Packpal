package controllers;

import dao.UserDAO;
import views.ForgotPasswordView;
import views.LoginView;

import javax.swing.*;

public class ForgotPasswordController {

    private ForgotPasswordView view;
    private UserDAO userDAO;

    public ForgotPasswordController(ForgotPasswordView view) {
        this.view = view;
        this.userDAO = new UserDAO();
    }

    public void handlePasswordReset() {
        String email = view.getEmail();

        if (email.isEmpty()) {
            view.showMessage("Please enter your email.");
            return;
        }

        if (!userDAO.emailExists(email)) {
            view.showMessage("Email not found.");
            return;
        }

        // Simulate sending a reset email
        view.showMessage("Password reset link has been sent to " + email);
    }

    public void handleBackToLogin() {
        view.setVisible(false);
        new LoginView().setVisible(true);
    }
}

