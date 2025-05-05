package com.example.carrental.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.sql.*;
import com.example.carrental.DBConnection;
import com.example.carrental.dsa.bookingQueue;
import com.example.carrental.models.booking;
import java.time.LocalDate;

public class bookingController {
    @FXML private Label carDetailsLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label totalPriceLabel;
    @FXML private Label statusLabel;

    private bookingQueue bookingQueueDSA = new bookingQueue(); // DSA for in-memory booking management

    @FXML
    private void handleConfirmBooking(ActionEvent event) {
        // Example: Create a booking and add to both DSA and DB
        int carId = 1; // Replace with actual car ID
        int customerId = 1; // Replace with actual customer ID
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        double totalCost = 100.0; // Replace with actual calculation
        booking newBooking = new booking(0, carId, customerId, startDate, endDate, "Confirmed", totalCost);
        bookingQueueDSA.enqueue(newBooking); // Add to DSA
        // Add to DB
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO bookings (car_id, customer_id, start_date, end_date, status, total_price) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, carId);
            pstmt.setInt(2, customerId);
            pstmt.setDate(3, Date.valueOf(startDate));
            pstmt.setDate(4, Date.valueOf(endDate));
            pstmt.setString(5, "Confirmed");
            pstmt.setDouble(6, totalCost);
            pstmt.executeUpdate();
            statusLabel.setText("Booking confirmed (DB + DSA)!");
        } catch (SQLException e) {
            statusLabel.setText("Error booking: " + e.getMessage());
        }
    }
}