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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import com.example.carrental.DBConnection;
import com.example.carrental.models.car;
import com.example.carrental.models.customer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class adminDashboardController {
    @FXML private TableView<car> carsTable;
    @FXML private TableColumn<car, String> makeColumn;
    @FXML private TableColumn<car, String> modelColumn;
    @FXML private TableColumn<car, Integer> yearColumn;
    @FXML private TableColumn<car, Double> priceColumn;
    @FXML private TableColumn<car, String> statusColumn;
    @FXML private Label statusLabel;
    private ContextMenu contextMenu;

    @FXML
    private void initialize() {
        // Initialize table columns using lambda expressions to avoid reflection issues
        makeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBrand()));
        modelColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getModel()));
        yearColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getYear()).asObject());
        priceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPricePerDay()).asObject());
        statusColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        // Setup context menu for updating car status
        setupContextMenu();
        
        // Load cars on initialization
        loadCars();
    }
    
    private void setupContextMenu() {
        contextMenu = new ContextMenu();
        
        MenuItem availableItem = new MenuItem("Set Available");
        availableItem.setOnAction(e -> updateCarStatus("Available"));
        
        MenuItem rentedItem = new MenuItem("Set Rented");
        rentedItem.setOnAction(e -> updateCarStatus("Rented"));
        
        MenuItem maintenanceItem = new MenuItem("Set Maintenance");
        maintenanceItem.setOnAction(e -> updateCarStatus("Maintenance"));
        
        MenuItem deleteItem = new MenuItem("Delete Car");
        deleteItem.setOnAction(e -> deleteCar());
        
        contextMenu.getItems().addAll(availableItem, rentedItem, maintenanceItem, deleteItem);
        
        // Add context menu to table on right-click
        carsTable.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) { // Right click
                contextMenu.show(carsTable, event.getScreenX(), event.getScreenY());
            }
        });
    }
    
    private void deleteCar() {
        car selectedCar = carsTable.getSelectionModel().getSelectedItem();
        if (selectedCar == null) {
            statusLabel.setText("Please select a car to delete");
            return;
        }
        
        // Confirm before deletion
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Car");
        confirmAlert.setHeaderText("Delete Car Confirmation");
        confirmAlert.setContentText("Are you sure you want to delete the selected car? \n" + 
                                   "Make: " + selectedCar.getBrand() + "\n" +
                                   "Model: " + selectedCar.getModel());
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM cars WHERE id = ?")) {
                pstmt.setInt(1, selectedCar.getCarId());
                
                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    statusLabel.setText("Car deleted successfully");
                    // Remove from the table
                    loadCars(); // Refresh the table to reflect changes
                } else {
                    statusLabel.setText("Failed to delete car");
                }
            } catch (SQLException e) {
                statusLabel.setText("Error deleting car: " + e.getMessage());
                e.printStackTrace();
                
                // Show error alert
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setTitle("Database Error");
                errorAlert.setHeaderText("Error Deleting Car");
                errorAlert.setContentText("An error occurred while deleting the car: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }
    
    private void updateCarStatus(String newStatus) {
        car selectedCar = carsTable.getSelectionModel().getSelectedItem();
        if (selectedCar == null) {
            statusLabel.setText("Please select a car to update status");
            return;
        }
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("UPDATE cars SET status = ? WHERE id = ?")) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, selectedCar.getCarId());
            
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                statusLabel.setText("Car status updated to: " + newStatus);
                // Update the local object to reflect the change
                selectedCar.setStatus(newStatus);
                carsTable.refresh();
            } else {
                statusLabel.setText("Failed to update car status");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error updating status: " + e.getMessage());
            e.printStackTrace();
        }
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
        try {
            // Load the manageUsers.fxml window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/carrental/manageUsers.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Manage Users");
            stage.show();
            statusLabel.setText("Manage Users window opened");
        } catch (IOException e) {
            statusLabel.setText("Error opening Manage Users window: " + e.getMessage());
            e.printStackTrace();
        }
    }
}