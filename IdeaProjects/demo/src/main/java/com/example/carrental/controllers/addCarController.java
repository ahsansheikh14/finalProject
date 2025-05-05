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
            String make = makeField.getText();
            String model = modelField.getText();
            int year = Integer.parseInt(yearField.getText());
            double price = Double.parseDouble(priceField.getText());

            // Get database connection
            Connection conn = DBConnection.getConnection();
            
            // Prepare SQL statement
            String sql = "INSERT INTO cars (make, model, year, price) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, make);
            pstmt.setString(2, model);
            pstmt.setInt(3, year);
            pstmt.setDouble(4, price);

            // Execute the statement
            pstmt.executeUpdate();
            
            statusLabel.setText("Car added successfully!");
            
            // Clear the fields
            makeField.clear();
            modelField.clear();
            yearField.clear();
            priceField.clear();

        } catch (SQLException e) {
            statusLabel.setText("Error adding car: " + e.getMessage());
        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter valid numbers for year and price");
        }
    }
} 