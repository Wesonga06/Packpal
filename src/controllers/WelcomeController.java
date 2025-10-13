/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packpal.controllers;

import packpal.views.WelcomeView;
import packpal.views.LoginView;

public class WelcomeController {
    private WelcomeView view;
    
    public WelcomeController(WelcomeView view) {
        this.view = view;
    }
    
    public void handleGetStarted() {
        // Navigate to Create Account page
        view.setVisible(false);
        CreateAccountView createAccountView = new CreateAccountView();
        createAccountView.setVisible(true);
    }
    
    public void handleHaveAccount() {
        // Navigate to Login page
        view.setVisible(false);
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }

    private static class CreateAccountView {

        public CreateAccountView() {
        }

        private void setVisible(boolean b) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
}