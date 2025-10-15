package controllers;

import views.DashboardView;
import views.LoginView;
import views.RegisterView;
import views.WelcomeView;

public class WelcomeController {
    private WelcomeView view;

    public WelcomeController(WelcomeView view) {
        this.view = view;
    }

    public void handleGetStarted() {
        new RegisterView();
        view.dispose();
    }

    public void handleLogin() {
        new LoginView();
        view.dispose();
    }
}