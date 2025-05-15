package com.example.carrental.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import com.example.carrental.SceneSwitcher;
import com.example.carrental.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.example.carrental.dsa.bookingQueue;
import com.example.carrental.models.booking;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.example.carrental.models.BookingEntry;

public class myBookingsController {
    @FXML private TableView<BookingEntry> bookingsTable;
    @FXML private TableColumn<BookingEntry, Integer> bookingIdColumn;
    @FXML private TableColumn<BookingEntry, String> carInfoColumn;
    @FXML private TableColumn<BookingEntry, Date> startDateColumn;
    @FXML private TableColumn<BookingEntry, Date> endDateColumn;
    @FXML private TableColumn<BookingEntry, Double> totalPriceColumn;
    @FXML private TableColumn<BookingEntry, Double> paymentAmountColumn;
    @FXML private TableColumn<BookingEntry, String> paymentStatusColumn;
    @FXML private TableColumn<BookingEntry, String> statusColumn;
    @FXML private TableColumn<BookingEntry, String> actionColumn;
    @FXML private Label statusLabel;

    private bookingQueue bookingQueueDSA = new bookingQueue(); // DSA for in-memory booking management
    private int currentUserId = 1; // This should be set based on logged-in user

    @FXML
    public void initialize() {
        // Set up table columns
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        carInfoColumn.setCellValueFactory(new PropertyValueFactory<>("carInfo"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set up action column with cancel button
        actionColumn.setCellFactory(col -> new TableCell<BookingEntry, String>() {
            private final Button cancelButton = new Button("Cancel");

            {
                cancelButton.setOnAction(event -> {
                    BookingEntry booking = getTableView().getItems().get(getIndex());
                    handleCancelBooking(booking);
                });
                cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    BookingEntry booking = getTableView().getItems().get(getIndex());
                    if ("Ongoing".equalsIgnoreCase(booking.getStatus())) {
                        setGraphic(cancelButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // Load bookings
        loadBookings();
    }

    private void loadBookings() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT b.*, c.brand, c.model, c.year, p.amount AS payment_amount, p.status AS payment_status FROM bookings b " +
                    "JOIN cars c ON b.car_id = c.id " +
                    "LEFT JOIN payments p ON b.id = p.booking_id " +
                    "WHERE b.customer_id = ? " +
                    "ORDER BY b.start_date DESC";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            List<BookingEntry> bookings = new ArrayList<>();
            while (rs.next()) {
                BookingEntry entry = new BookingEntry(
                        rs.getInt("id"),
                        rs.getString("brand") + " " + rs.getString("model") + " (" + rs.getInt("year") + ")",
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getDouble("total_price"),
                        rs.getString("status"),
                        rs.getDouble("payment_amount"),
                        rs.getString("payment_status")
                );
                bookings.add(entry);
                System.out.println("Loaded booking: " + entry.getBookingId() + " | " + entry.getCarInfo() + " | " + entry.getStartDate() + " - " + entry.getEndDate());

                // Also add to our DSA for practice
                booking b = new booking(
                        rs.getInt("id"),
                        rs.getInt("car_id"),
                        rs.getInt("customer_id"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getString("status"),
                        rs.getDouble("total_price")
                );
                bookingQueueDSA.enqueue(b);
            }
            System.out.println("Total bookings loaded: " + bookings.size());

            ObservableList<BookingEntry> bookingData = FXCollections.observableArrayList(bookings);
            bookingsTable.setItems(bookingData);
            statusLabel.setText("Your bookings loaded successfully");

        } catch (SQLException e) {
            statusLabel.setText("Error loading bookings: " + e.getMessage());
        }
    }

    private void handleCancelBooking(BookingEntry booking) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Booking");
        confirmAlert.setHeaderText("Cancel Booking Confirmation");
        confirmAlert.setContentText("Are you sure you want to cancel this booking?\n" + booking.getCarInfo());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getConnection()) {
                // Update booking status in DB
                String sql = "UPDATE bookings SET status = 'Cancelled' WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, booking.getBookingId());
                int affected = pstmt.executeUpdate();

                if (affected > 0) {
                    // Update car status to Available
                    sql = "UPDATE cars c JOIN bookings b ON c.id = b.car_id SET c.status = 'Available' WHERE b.id = ?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, booking.getBookingId());
                    pstmt.executeUpdate();

                    statusLabel.setText("Booking cancelled successfully");

                    // Refresh bookings
                    loadBookings();
                } else {
                    statusLabel.setText("Failed to cancel booking");
                }
            } catch (SQLException e) {
                statusLabel.setText("Error cancelling booking: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/carrental/userDashboard.fxml"));
            Parent root = loader.load();
            userDashboardController controller = loader.getController();
            controller.setCurrentUser(currentUserId);
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.setTitle("Car Rental System - User Dashboard");
            stage.show();
        } catch (Exception e) {
            statusLabel.setText("Error returning to dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadBookings();
    }

    public void setCurrentUser(int userId) {
        this.currentUserId = userId;
        loadBookings();
    }
}