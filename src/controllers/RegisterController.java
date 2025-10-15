/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import javax.swing.JOptionPane;
import views.DashboardView;
import views.RegisterView;

public class RegisterController {
    private RegisterView view;

    public RegisterController(RegisterView view) {
        this.view = view;
    }

    public void handleSignUp(String email, String password) {
        // TODO: Save to DAO
        if (!email.isEmpty() && !password.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Account created!");
            new DashboardView();
            view.dispose();
        } else {
            JOptionPane.showMessageDialog(view, "Please fill all fields.");
        }
    }
}