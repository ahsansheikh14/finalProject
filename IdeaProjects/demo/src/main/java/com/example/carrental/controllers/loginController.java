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

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT role FROM users WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                if ("admin".equalsIgnoreCase(role)) {
                    SceneSwitcher.switchScene(stage, "/com/example/carrental/adminDashboard.fxml");
                } else {
                    SceneSwitcher.switchScene(stage, "/com/example/carrental/userDashboard.fxml");
                }
            } else {
                errorLabel.setText("Invalid credentials.");
            }
        } catch (SQLException e) {
            errorLabel.setText("Database error: " + e.getMessage());
        }
    }
}