package com.example.carrental.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import com.example.carrental.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class addCarController {
    @FXML private TextField makeField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private TextField priceField;
    @FXML private Button submitButton;
    @FXML private Label statusLabel;

    @FXML
    private void handleAddCar(ActionEvent event) {
        try {
            // Validate input
            String brand = makeField.getText();
            String model = modelField.getText();
            if (brand.isEmpty() || model.isEmpty()) {
                statusLabel.setText("Make and Model cannot be empty");
                return;
            }

            int year = Integer.parseInt(yearField.getText());
            double pricePerDay = Double.parseDouble(priceField.getText());

            // Get database connection and prepare statement
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO cars (brand, model, year, price_per_day) VALUES (?, ?, ?, ?)"
                 )) {
                pstmt.setString(1, brand);
                pstmt.setString(2, model);
                pstmt.setInt(3, year);
                pstmt.setDouble(4, pricePerDay);

                // Execute the statement
                pstmt.executeUpdate();

                statusLabel.setText("Car added successfully!");

                // Clear the fields
                makeField.clear();
                modelField.clear();
                yearField.clear();
                priceField.clear();
            }
        } catch (SQLException e) {
            statusLabel.setText("Error adding car: " + e.getMessage());
        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter valid numbers for year and price");
        }
    }
}