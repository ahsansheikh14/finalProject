package com.example.carrental.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.carrental.DBConnection;
import com.example.carrental.models.car;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class adminDashboardController {
    @FXML private TableView<car> carsTable;
    @FXML private TableColumn<car, String> makeColumn;
    @FXML private TableColumn<car, String> modelColumn;
    @FXML private TableColumn<car, Integer> yearColumn;
    @FXML private TableColumn<car, Double> priceColumn;
    @FXML private TableColumn<car, String> statusColumn;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        // Initialize table columns using lambda expressions to avoid reflection issues
        makeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBrand()));
        modelColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getModel()));
        yearColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getYear()).asObject());
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPricePerDay()).asObject());
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        // Load cars on initialization
        loadCars();
    }

    @FXML
    private void handleViewCars(ActionEvent event) {
        loadCars();
    }

    private void loadCars() {
        ObservableList<car> carList = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM cars");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                car car = new car(
                        rs.getInt("id"),
                        rs.getString("model"),
                        rs.getString("brand"),
                        rs.getInt("year"),
                        rs.getDouble("price_per_day"),
                        rs.getString("status")
                );
                carList.add(car);
                System.out.println("Loaded car: " + car.getBrand() + " " + car.getModel());
            }
            carsTable.setItems(carList);
            System.out.println("Set " + carList.size() + " items to TableView");
            carsTable.refresh(); // Ensure the TableView refreshes
            statusLabel.setText("Loaded " + carList.size() + " cars successfully");
        } catch (SQLException e) {
            statusLabel.setText("Error loading cars: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddCar(ActionEvent event) throws IOException {
        // Load the addCar.fxml window with the correct path
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/carrental/addCar.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Add New Car");
        stage.show();
    }

    @FXML
    private void handleManageUsers(ActionEvent event) throws IOException {
        // Placeholder for manage users functionality
        statusLabel.setText("Manage Users functionality not implemented yet");
    }
}