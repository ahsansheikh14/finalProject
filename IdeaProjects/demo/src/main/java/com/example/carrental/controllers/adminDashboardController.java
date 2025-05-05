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
import com.example.carrental.dsa.carTree;
import com.example.carrental.models.car;

public class adminDashboardController {
    @FXML private TableView<Car> carsTable;
    @FXML private TableColumn<Car, String> makeColumn;
    @FXML private TableColumn<Car, String> modelColumn;
    @FXML private TableColumn<Car, Integer> yearColumn;
    @FXML private TableColumn<Car, Double> priceColumn;
    @FXML private TableColumn<Car, String> statusColumn;
    @FXML private Label statusLabel;

    private carTree carTreeDSA = new carTree(); // DSA for in-memory car management

    @FXML
    public void initialize() {
        // Set up table columns
        makeColumn.setCellValueFactory(new PropertyValueFactory<>("make"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Load initial data
        handleViewCars(null);
    }

    @FXML
    private void handleAddCar(ActionEvent event) {
        // Example: Add a car to both DB and carTreeDSA
        // You would get these values from a form in a real app
        car newCar = new car(0, "ModelX", "BrandY", 2024, 100.0, "Available");
        carTreeDSA.insert(newCar); // Add to DSA
        // Add to DB (you can use your addCarController logic here)
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO cars (model, brand, year, price_per_day, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newCar.getModel());
            pstmt.setString(2, newCar.getBrand());
            pstmt.setInt(3, newCar.getYear());
            pstmt.setDouble(4, newCar.getPricePerDay());
            pstmt.setString(5, newCar.getStatus());
            pstmt.executeUpdate();
            statusLabel.setText("Car added to DB and DSA!");
        } catch (SQLException e) {
            statusLabel.setText("Error adding car: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewCars(ActionEvent event) {
        // Load from DB and populate carTreeDSA
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM cars";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            List<Car> cars = new ArrayList<>();
            carTreeDSA = new carTree(); // Reset tree
            while (rs.next()) {
                car c = new car(
                    rs.getInt("id"),
                    rs.getString("model"),
                    rs.getString("brand"),
                    rs.getInt("year"),
                    rs.getDouble("price_per_day"),
                    rs.getString("status")
                );
                cars.add(new Car(c.getBrand(), c.getModel(), c.getYear(), c.getPricePerDay(), c.getStatus()));
                carTreeDSA.insert(c); // Add to DSA
            }

            ObservableList<Car> carData = FXCollections.observableArrayList(cars);
            carsTable.setItems(carData);
            statusLabel.setText("Cars loaded successfully (DB + DSA)");

        } catch (SQLException e) {
            statusLabel.setText("Error loading cars: " + e.getMessage());
        }
    }

    @FXML
    private void handleManageUsers(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/com/example/carrental/manageUsers.fxml");
        } catch (Exception e) {
            statusLabel.setText("Error loading user management: " + e.getMessage());
        }
    }

    // Car class to represent car data
    public static class Car {
        private String make;
        private String model;
        private int year;
        private double price;
        private String status;

        public Car(String make, String model, int year, double price, String status) {
            this.make = make;
            this.model = model;
            this.year = year;
            this.price = price;
            this.status = status;
        }

        // Getters
        public String getMake() { return make; }
        public String getModel() { return model; }
        public int getYear() { return year; }
        public double getPrice() { return price; }
        public String getStatus() { return status; }
    }
} 