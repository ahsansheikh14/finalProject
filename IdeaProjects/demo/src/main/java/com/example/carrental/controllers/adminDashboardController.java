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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.util.Callback;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javafx.scene.control.ListCell;

public class adminDashboardController {
    @FXML private TableView<car> carsTable;
    @FXML private TableColumn<car, String> makeColumn;
    @FXML private TableColumn<car, String> modelColumn;
    @FXML private TableColumn<car, Integer> yearColumn;
    @FXML private TableColumn<car, Double> priceColumn;
    @FXML private TableColumn<car, Integer> seatsColumn;
    @FXML private TableColumn<car, Boolean> specialColumn;
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
        seatsColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getSeats()).asObject());
        specialColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isSpecial()));
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

        MenuItem bookCarItem = new MenuItem("Book Car for User");
        bookCarItem.setOnAction(e -> bookCarForUser());

        MenuItem deleteItem = new MenuItem("Delete Car");
        deleteItem.setOnAction(e -> deleteCar());

        contextMenu.getItems().addAll(availableItem, rentedItem, maintenanceItem, bookCarItem, deleteItem);

        // Add context menu to table on right-click
        carsTable.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) { // Right click
                car selectedCar = carsTable.getSelectionModel().getSelectedItem();

                // Only enable booking for Available cars
                bookCarItem.setDisable(selectedCar == null || !"Available".equals(selectedCar.getStatus()));

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
                        rs.getString("status"),
                        rs.getInt("seats"),
                        rs.getBoolean("is_special")
                );
                carList.add(car);
                System.out.println("Loaded car: " + car.getBrand() + " " + car.getModel() + " (Seats: " + car.getSeats() + ", Special: " + car.isSpecial() + ")");
            }
            carsTable.setItems(carList);
            System.out.println("Set " + carList.size() + " items to TableView");
            carsTable.refresh(); // Ensure the TableView refreshes
            statusLabel.setText("Loaded " + carList.size() + " cars successfully");
        } catch (SQLException e) {
            statusLabel.setText("Error loading cars: " + e.getMessage());
            e.printStackTrace();
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

    private void bookCarForUser() {
        car selectedCar = carsTable.getSelectionModel().getSelectedItem();
        if (selectedCar == null) {
            statusLabel.setText("Please select a car to book");
            return;
        }

        if (!"Available".equals(selectedCar.getStatus())) {
            statusLabel.setText("Selected car is not available for booking");
            return;
        }

        // Create and configure the booking dialog
        Dialog<BookingData> dialog = new Dialog<>();
        dialog.setTitle("Book Car for User");
        dialog.setHeaderText("Book " + selectedCar.getBrand() + " " + selectedCar.getModel() + " for a User");

        // Set the button types
        ButtonType bookButtonType = new ButtonType("Book", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bookButtonType, ButtonType.CANCEL);

        // Create the booking form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Load users into ComboBox
        ComboBox<UserData> userComboBox = new ComboBox<>();
        loadUsersIntoComboBox(userComboBox);

        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusDays(1));

        grid.add(new Label("Select User:"), 0, 0);
        grid.add(userComboBox, 1, 0);
        grid.add(new Label("Start Date:"), 0, 1);
        grid.add(startDatePicker, 1, 1);
        grid.add(new Label("End Date:"), 0, 2);
        grid.add(endDatePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to BookingData when the book button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == bookButtonType) {
                if (userComboBox.getValue() == null) {
                    statusLabel.setText("Please select a user");
                    return null;
                }

                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();

                if (startDate == null || endDate == null) {
                    statusLabel.setText("Please select both start and end dates");
                    return null;
                }

                if (startDate.isAfter(endDate)) {
                    statusLabel.setText("Start date cannot be after end date");
                    return null;
                }

                return new BookingData(
                        userComboBox.getValue().id,
                        selectedCar.getCarId(),
                        startDate,
                        endDate,
                        selectedCar.getPricePerDay()
                );
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<BookingData> result = dialog.showAndWait();

        result.ifPresent(bookingData -> {
            // Calculate total price (days * price per day)
            long days = ChronoUnit.DAYS.between(bookingData.startDate, bookingData.endDate) + 1;
            double totalPrice = days * bookingData.pricePerDay;

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO bookings (car_id, customer_id, start_date, end_date, status, total_price) VALUES (?, ?, ?, ?, ?, ?)"
                 )) {
                pstmt.setInt(1, bookingData.carId);
                pstmt.setInt(2, bookingData.customerId);
                pstmt.setDate(3, java.sql.Date.valueOf(bookingData.startDate));
                pstmt.setDate(4, java.sql.Date.valueOf(bookingData.endDate));
                pstmt.setString(5, "Ongoing");
                pstmt.setDouble(6, totalPrice);

                int affected = pstmt.executeUpdate();

                if (affected > 0) {
                    // Update car status to Rented
                    updateCarStatus("Rented");
                    statusLabel.setText("Car booked successfully");
                } else {
                    statusLabel.setText("Failed to book car");
                }
            } catch (SQLException e) {
                statusLabel.setText("Error booking car: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void loadUsersIntoComboBox(ComboBox<UserData> comboBox) {
        ObservableList<UserData> users = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT id, name, license_number FROM customers");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(new UserData(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("license_number")
                ));
            }

            comboBox.setItems(users);

            // Set custom cell factory to display user name and license
            comboBox.setCellFactory(param -> new ListCell<UserData>() {
                @Override
                protected void updateItem(UserData item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.name + " (License: " + item.licenseNumber + ")");
                    }
                }
            });

            // Also set a custom string converter for the selected value
            comboBox.setConverter(new javafx.util.StringConverter<UserData>() {
                @Override
                public String toString(UserData user) {
                    return user == null ? "" : user.name + " (License: " + user.licenseNumber + ")";
                }

                @Override
                public UserData fromString(String string) {
                    return null; // Not needed for this use case
                }
            });

        } catch (SQLException e) {
            statusLabel.setText("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper class to store user data for the combo box
    private static class UserData {
        private final int id;
        private final String name;
        private final String licenseNumber;

        public UserData(int id, String name, String licenseNumber) {
            this.id = id;
            this.name = name;
            this.licenseNumber = licenseNumber;
        }
    }

    // Helper class to store booking form data
    private static class BookingData {
        private final int customerId;
        private final int carId;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final double pricePerDay;

        public BookingData(int customerId, int carId, LocalDate startDate, LocalDate endDate, double pricePerDay) {
            this.customerId = customerId;
            this.carId = carId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.pricePerDay = pricePerDay;
        }
    }
}