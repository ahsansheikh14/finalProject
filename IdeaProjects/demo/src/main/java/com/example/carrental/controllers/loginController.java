package com.example.carrental.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.example.carrental.SceneSwitcher;
import com.example.carrental.DBConnection;
import java.sql.*;

public class loginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password cannot be empty");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Try admin login
            String sqlAdmin = "SELECT * FROM admins WHERE username=? AND password_hash=?";
            PreparedStatement stmtAdmin = conn.prepareStatement(sqlAdmin);
            stmtAdmin.setString(1, username);
            stmtAdmin.setString(2, password);
            ResultSet rsAdmin = stmtAdmin.executeQuery();

            if (rsAdmin.next()) {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                try {
                    SceneSwitcher.switchScene(stage, "/com/example/carrental/adminDashboard.fxml");
                } catch (Exception e) {
                    errorLabel.setText("Failed to load admin dashboard: " + e.getMessage());
                    e.printStackTrace();
                }
                return;
            }

            // Try customer login using email as username
            String sqlCustomer = "SELECT * FROM customers WHERE email=? AND password_hash=?";
            PreparedStatement stmtCustomer = conn.prepareStatement(sqlCustomer);
            stmtCustomer.setString(1, username);
            stmtCustomer.setString(2, password);
            ResultSet rsCustomer = stmtCustomer.executeQuery();

            if (rsCustomer.next()) {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                try {
                    // Load the user dashboard FXML manually to access the controller
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/carrental/userDashboard.fxml"));
                    Parent root = loader.load();
                    
                    // Get the controller and initialize it with the user's ID
                    userDashboardController controller = loader.getController();
                    int userId = rsCustomer.getInt("id");
                    controller.setCurrentUser(userId);
                    
                    // Set the scene with appropriate dimensions for the dashboard
                    Scene scene = new Scene(root, 1000, 700);
                    stage.setTitle("Car Rental System - User Dashboard");
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    errorLabel.setText("Failed to load user dashboard: " + e.getMessage());
                    System.out.println("Stack trace: ");
                    e.printStackTrace();
                }
            } else {
                errorLabel.setText("Invalid credentials. Please check username/license and password.");
            }
        } catch (SQLException e) {
            errorLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}