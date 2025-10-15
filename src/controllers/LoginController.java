/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import javax.swing.JOptionPane;
import views.DashboardView;
import views.LoginView;

public class LoginController {
    private LoginView view;

    public LoginController(LoginView view) {
        this.view = view;
    }

    public void handleSignIn(String email, String password) {
        // TODO: Validate with DAO
        if (!email.isEmpty() && !password.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Logged in successfully!");
            new DashboardView();
            view.dispose();
        } else {
            JOptionPane.showMessageDialog(view, "Invalid credentials.");
        }
    }
}
