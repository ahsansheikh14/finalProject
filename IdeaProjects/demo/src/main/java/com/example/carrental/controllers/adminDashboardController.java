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

public class adminDashboardController {
    @FXML private TableView<Car> carsTable;
    @FXML private TableColumn<Car, String> makeColumn;
    @FXML private TableColumn<Car, String> modelColumn;
    @FXML private TableColumn<Car, Integer> yearColumn;
    @FXML private TableColumn<Car, Double> priceColumn;
    @FXML private TableColumn<Car, String> statusColumn;
    @FXML private Label statusLabel;

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
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/com/example/carrental/addCar.fxml");
        } catch (Exception e) {
            statusLabel.setText("Error loading add car form: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewCars(ActionEvent event) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM cars";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            List<Car> cars = new ArrayList<>();
            while (rs.next()) {
                Car car = new Car(
                    rs.getString("make"),
                    rs.getString("model"),
                    rs.getInt("year"),
                    rs.getDouble("price"),
                    rs.getString("status")
                );
                cars.add(car);
            }

            ObservableList<Car> carData = FXCollections.observableArrayList(cars);
            carsTable.setItems(carData);
            statusLabel.setText("Cars loaded successfully");

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