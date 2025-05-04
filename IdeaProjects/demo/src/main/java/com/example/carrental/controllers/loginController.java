package com.example.carrental.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import com.example.carrental.SceneSwitcher;

public class loginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        // TODO: Add authentication logic here
        if (username.equals("admin") && password.equals("admin")) {
            errorLabel.setText("Login successful!");
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                SceneSwitcher.switchScene(stage, "/com/example/carrental/dashboard.fxml");
            } catch (Exception e) {
                errorLabel.setText("Failed to load dashboard.");
            }
        } else {
            errorLabel.setText("Invalid credentials.");
        }
    }
}