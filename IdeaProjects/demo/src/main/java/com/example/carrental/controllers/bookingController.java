package com.example.carrental.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import com.example.carrental.SceneSwitcher;
import com.example.carrental.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class bookingController {
    @FXML private Label carDetailsLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label totalPriceLabel;
    @FXML private Label statusLabel;
    
    private int carId;
    private double dailyPrice;

    public void setCarDetails(int carId, String make, String model, double price) {
        this.carId = carId;
        this.dailyPrice = price;
        carDetailsLabel.setText(String.format("%s %s - $%.2f per day", make, model, price));
        updateTotalPrice();
    }

    @FXML
    private void initialize() {
        // Set minimum date to today
        startDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        // End date must be after start date
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                endDatePicker.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(empty || date.isBefore(newVal));
                    }
                });
                updateTotalPrice();
            }
        });

        endDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateTotalPrice();
        });
    }

    private void updateTotalPrice() {
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
            long days = ChronoUnit.DAYS.between(startDatePicker.getValue(), endDatePicker.getValue());
            if (days >= 0) {
                double total = days * dailyPrice;
                totalPriceLabel.setText(String.format("$%.2f", total));
            }
        }
    }

    @FXML
    private void handleConfirmBooking(ActionEvent event) {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            statusLabel.setText("Please select both start and end dates");
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            
            // Check if car is available for these dates
            String checkSql = "SELECT COUNT(*) FROM bookings WHERE car_id = ? AND status = 'confirmed' " +
                            "AND ((start_date <= ? AND end_date >= ?) OR (start_date <= ? AND end_date >= ?))";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, carId);
            checkStmt.setDate(2, Date.valueOf(endDatePicker.getValue()));
            checkStmt.setDate(3, Date.valueOf(startDatePicker.getValue()));
            checkStmt.setDate(4, Date.valueOf(endDatePicker.getValue()));
            checkStmt.setDate(5, Date.valueOf(startDatePicker.getValue()));
            
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                statusLabel.setText("Car is not available for selected dates");
                return;
            }

            // Create booking
            String sql = "INSERT INTO bookings (user_id, car_id, start_date, end_date, total_price, status) " +
                        "VALUES (?, ?, ?, ?, ?, 'confirmed')";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, 1); // TODO: Get actual user ID from session
            pstmt.setInt(2, carId);
            pstmt.setDate(3, Date.valueOf(startDatePicker.getValue()));
            pstmt.setDate(4, Date.valueOf(endDatePicker.getValue()));
            pstmt.setDouble(5, Double.parseDouble(totalPriceLabel.getText().replace("$", "")));
            
            pstmt.executeUpdate();
            
            // Update car status
            String updateCarSql = "UPDATE cars SET status = 'Booked' WHERE id = ?";
            PreparedStatement updateCarStmt = conn.prepareStatement(updateCarSql);
            updateCarStmt.setInt(1, carId);
            updateCarStmt.executeUpdate();

            statusLabel.setText("Booking confirmed successfully!");
            
            // Return to user dashboard after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    SceneSwitcher.switchScene(stage, "/com/example/carrental/userDashboard.fxml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (SQLException e) {
            statusLabel.setText("Error creating booking: " + e.getMessage());
        }
    }
}