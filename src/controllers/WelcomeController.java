package controllers;

import views.WelcomeView;
import views.LoginView;
import views.RegisterView;

public class WelcomeController {
    private WelcomeView view;
    
    public WelcomeController(WelcomeView view) {
        this.view = view;
    }
    
    public void handleGetStarted() {
        // Navigate to Create Account (Register) page
        view.setVisible(false);
        RegisterView registerView = new RegisterView();
        registerView.setVisible(true);
    }
    
    public void handleHaveAccount() {
        // Navigate to Login page
        view.setVisible(false);
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }
}
