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
import java.util.ArrayList;
import java.util.List;
import com.example.carrental.dsa.customerLinkedList;
import com.example.carrental.models.customer;
import com.example.carrental.models.car;

public class userDashboardController {
    @FXML private TableView<Car> availableCarsTable;
    @FXML private TableColumn<Car, String> makeColumn;
    @FXML private TableColumn<Car, String> modelColumn;
    @FXML private TableColumn<Car, Integer> yearColumn;
    @FXML private TableColumn<Car, Double> priceColumn;
    @FXML private TableColumn<Car, Button> actionColumn;
    @FXML private Label statusLabel;

    private int currentUserId = 1; // This should be set based on logged-in user
    private customerLinkedList customerListDSA = new customerLinkedList(); // DSA for in-memory customer management

    @FXML
    public void initialize() {
        // Set up table columns
        makeColumn.setCellValueFactory(new PropertyValueFactory<>("make"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Set up action column with booking button
        actionColumn.setCellFactory(col -> new TableCell<Car, Button>() {
            private final Button bookButton = new Button("Book Now");

            {
                bookButton.setOnAction(event -> {
                    Car car = getTableView().getItems().get(getIndex());
                    handleBookCar(car);
                });
                bookButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(bookButton);
                }
            }
        });

        // Load initial data
        loadAvailableCars();
        loadCustomersToDSA();
    }

    @FXML
    private void handleViewAvailableCars(ActionEvent event) {
        loadAvailableCars();
    }

    private void loadAvailableCars() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM cars WHERE status = 'Available'";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            List<Car> cars = new ArrayList<>();
            while (rs.next()) {
                Car car = new Car(
                        rs.getInt("id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("price_per_day"),
                        rs.getString("status")
                );
                cars.add(car);
            }

            ObservableList<Car> carData = FXCollections.observableArrayList(cars);
            availableCarsTable.setItems(carData);

            if (cars.isEmpty()) {
                statusLabel.setText("No available cars found");
            } else {
                statusLabel.setText("Found " + cars.size() + " available cars");
            }

        } catch (SQLException e) {
            statusLabel.setText("Error loading cars: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyBookings(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/com/example/carrental/myBookings.fxml");
        } catch (Exception e) {
            statusLabel.setText("Error loading bookings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleBookCar(Car car) {
        // Store the selected car data in a session or static variable that the booking controller can access
        // For simplicity, we'll just pass the car ID through SceneSwitcher or a custom method
        try {
            // Store selected car ID for booking form
            selectedCarForBooking = car;

            // Switch to booking form
            Stage stage = (Stage) availableCarsTable.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/com/example/carrental/booking.fxml");

            // The booking controller should check for this static variable in its initialize method
        } catch (Exception e) {
            statusLabel.setText("Error starting booking process: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCustomersToDSA() {
        // Load customers from DB and populate customerLinkedList
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM customers";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                customer c = new customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("license_number"),
                        rs.getString("password_hash")
                );
                customerListDSA.add(c);
            }
        } catch (SQLException e) {
            statusLabel.setText("Error loading customers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Static variable to pass car info between controllers
    public static Car selectedCarForBooking;

    // Car class to represent car data
    public static class Car {
        private int id;
        private String make;
        private String model;
        private int year;
        private double price;
        private String status;

        public Car(int id, String make, String model, int year, double price, String status) {
            this.id = id;
            this.make = make;
            this.model = model;
            this.year = year;
            this.price = price;
            this.status = status;
        }

        // Getters
        public int getId() { return id; }
        public String getMake() { return make; }
        public String getModel() { return model; }
        public int getYear() { return year; }
        public double getPrice() { return price; }
        public String getStatus() { return status; }
    }
} 