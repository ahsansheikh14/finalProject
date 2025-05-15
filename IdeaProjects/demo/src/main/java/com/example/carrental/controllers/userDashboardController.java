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
import java.util.Optional;
import com.example.carrental.dsa.customerLinkedList;
import com.example.carrental.models.customer;
import com.example.carrental.models.car;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.application.Platform;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class userDashboardController {
    @FXML private TableView<car> availableCarsTable;
    @FXML private TableColumn<car, String> makeColumn;
    @FXML private TableColumn<car, String> modelColumn;
    @FXML private TableColumn<car, Integer> yearColumn;
    @FXML private TableColumn<car, Double> priceColumn;
    @FXML private TableColumn<car, Integer> seatsColumn;
    @FXML private TableColumn<car, Boolean> specialColumn;
    @FXML private TableColumn<car, String> statusColumn;
    @FXML private TableColumn<car, Button> actionColumn;
    @FXML private Label statusLabel;
    @FXML private Label welcomeLabel;
    
    @FXML private Button allCarsButton;
    @FXML private Button fourSeaterButton;
    @FXML private Button fivePlusSeaterButton;

    private int currentUserId = 1; // This will be set from loginController
    private customerLinkedList customerListDSA = new customerLinkedList(); // DSA for in-memory customer management

    @FXML private VBox rootVBox;

    @FXML
    public void initialize() {
        // Set up table columns with the correct property names from car.java
        makeColumn.setCellValueFactory(cellData -> cellData.getValue().brandProperty());
        modelColumn.setCellValueFactory(cellData -> cellData.getValue().modelProperty());
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().yearProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().pricePerDayProperty().asObject());
        if (seatsColumn != null) {
            seatsColumn.setCellValueFactory(cellData -> cellData.getValue().seatsProperty().asObject());
        }
        if (specialColumn != null) {
            specialColumn.setCellValueFactory(cellData -> cellData.getValue().specialProperty().asObject());
        }
        if (statusColumn != null) {
            statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        }

        // Set up action column with booking button
        actionColumn.setCellFactory(col -> new TableCell<car, Button>() {
            private final Button bookButton = new Button("Book Now");

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    car car = getTableView().getItems().get(getIndex());
                    bookButton.setDisable(!"Available".equalsIgnoreCase(car.getStatus()));
                    bookButton.setOnAction(event -> handleBookCar(car));
                    bookButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                    setGraphic(bookButton);
                }
            }
        });

        Platform.runLater(() -> {
            try {
                if (allCarsButton != null && fourSeaterButton != null && fivePlusSeaterButton != null) {
                    allCarsButton.setOnAction(e -> loadAllCars());
                    fourSeaterButton.setOnAction(e -> loadCarsBySeats(4, 4));
                    fivePlusSeaterButton.setOnAction(e -> loadCarsBySeats(5, 10));
                }
            } catch (Exception e) {
                System.out.println("Error initializing filter buttons: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Initial data will be loaded when setCurrentUser is called from loginController

        javafx.application.Platform.runLater(() -> {
            if (rootVBox.getScene() != null && rootVBox.getScene().getStylesheets() != null) {
                rootVBox.getScene().getStylesheets().add(getClass().getResource("/com/example/carrental/dashboard.css").toExternalForm());
            }
        });
    }

    private void loadAllCars() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM cars";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            List<car> cars = new ArrayList<>();
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
                cars.add(car);
            }

            ObservableList<car> carData = FXCollections.observableArrayList(cars);
            availableCarsTable.setItems(carData);

            if (cars.isEmpty()) {
                statusLabel.setText("No cars found");
            } else {
                statusLabel.setText("Found " + cars.size() + " cars");
            }

        } catch (SQLException e) {
            statusLabel.setText("Error loading cars: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCarsBySeats(int minSeats, int maxSeats) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM cars WHERE seats >= ? AND seats <= ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, minSeats);
            pstmt.setInt(2, maxSeats);
            ResultSet rs = pstmt.executeQuery();

            List<car> cars = new ArrayList<>();
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
                cars.add(car);
            }

            ObservableList<car> carData = FXCollections.observableArrayList(cars);
            availableCarsTable.setItems(carData);

            if (minSeats == maxSeats) {
                statusLabel.setText("Found " + cars.size() + " cars with " + minSeats + " seats");
            } else {
                statusLabel.setText("Found " + cars.size() + " cars with " + minSeats + "+ seats");
            }

        } catch (SQLException e) {
            statusLabel.setText("Error loading cars: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAvailableCars(ActionEvent event) {
        loadAllCars();
    }

    @FXML
    private void handleMyBookings(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/carrental/myBookings.fxml"));
            Parent root = loader.load();
            myBookingsController controller = loader.getController();
            controller.setCurrentUser(currentUserId);
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.setTitle("My Bookings");
            stage.show();
        } catch (Exception e) {
            statusLabel.setText("Error loading bookings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleBookCar(car selectedCar) {
        // Create and configure the booking dialog
        Dialog<LocalDate[]> dialog = new Dialog<>();
        dialog.setTitle("Book Car");
        dialog.setHeaderText("Book " + selectedCar.getBrand() + " " + selectedCar.getModel());
        
        // Set the button types
        ButtonType bookButtonType = new ButtonType("Book", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bookButtonType, ButtonType.CANCEL);
        
        // Create the booking form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        
        // Add Label for estimated price
        Label estimatedPriceLabel = new Label("Estimated Price: $" + selectedCar.getPricePerDay());
        Label bookingStatusLabel = new Label("");
        bookingStatusLabel.setStyle("-fx-text-fill: red;");
        
        // Add listeners to update price when dates change and check for conflicts
        startDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            updateEstimatedPrice(startDatePicker, endDatePicker, selectedCar, estimatedPriceLabel);
            checkBookingConflicts(selectedCar.getCarId(), startDatePicker.getValue(), endDatePicker.getValue(), bookingStatusLabel);
        });
        
        endDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            updateEstimatedPrice(startDatePicker, endDatePicker, selectedCar, estimatedPriceLabel);
            checkBookingConflicts(selectedCar.getCarId(), startDatePicker.getValue(), endDatePicker.getValue(), bookingStatusLabel);
        });
        
        // Initialize with current selection
        updateEstimatedPrice(startDatePicker, endDatePicker, selectedCar, estimatedPriceLabel);
        
        // Add fields to grid
        grid.add(new Label("Car:"), 0, 0);
        grid.add(new Label(selectedCar.getBrand() + " " + selectedCar.getModel()), 1, 0);
        grid.add(new Label("Price per day:"), 0, 1);
        grid.add(new Label("$" + selectedCar.getPricePerDay()), 1, 1);
        grid.add(new Label("Start Date:"), 0, 2);
        grid.add(startDatePicker, 1, 2);
        grid.add(new Label("End Date:"), 0, 3);
        grid.add(endDatePicker, 1, 3);
        grid.add(new Label("Total Payment:"), 0, 4);
        grid.add(estimatedPriceLabel, 1, 4);
        grid.add(bookingStatusLabel, 0, 5, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Check for conflicts initially
        checkBookingConflicts(selectedCar.getCarId(), startDatePicker.getValue(), endDatePicker.getValue(), bookingStatusLabel);
        
        // Disable the book button if there's a conflict
        final Button bookButton = (Button) dialog.getDialogPane().lookupButton(bookButtonType);
        bookButton.disableProperty().bind(bookingStatusLabel.textProperty().isNotEmpty());
        
        // Convert the result when the book button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == bookButtonType) {
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
                
                return new LocalDate[]{startDate, endDate};
            }
            return null;
        });
        
        // Show the dialog and process the result
        Optional<LocalDate[]> result = dialog.showAndWait();
        
        result.ifPresent(dates -> {
            // Calculate total price (days * price per day)
            LocalDate startDate = dates[0];
            LocalDate endDate = dates[1];
            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            double totalPrice = days * selectedCar.getPricePerDay();
            
            try (Connection conn = DBConnection.getConnection()) {
                // Begin transaction
                conn.setAutoCommit(false);
                
                try {
                    // Insert booking record
                    PreparedStatement bookingStmt = conn.prepareStatement(
                            "INSERT INTO bookings (car_id, customer_id, start_date, end_date, status, total_price) " +
                                    "VALUES (?, ?, ?, ?, 'Ongoing', ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    bookingStmt.setInt(1, selectedCar.getCarId());
                    bookingStmt.setInt(2, currentUserId);
                    bookingStmt.setDate(3, java.sql.Date.valueOf(startDate));
                    bookingStmt.setDate(4, java.sql.Date.valueOf(endDate));
                    bookingStmt.setDouble(5, totalPrice);
                    
                    int bookingSuccess = bookingStmt.executeUpdate();
                    ResultSet generatedKeys = bookingStmt.getGeneratedKeys();
                    
                    if (generatedKeys.next()) {
                        int bookingId = generatedKeys.getInt(1);
                        
                        // Create an initial payment record as "Pending"
                        PreparedStatement paymentStmt = conn.prepareStatement(
                                "INSERT INTO payments (booking_id, amount, method, status) " +
                                        "VALUES (?, ?, 'Credit Card', 'Pending')"
                        );
                        paymentStmt.setInt(1, bookingId);
                        paymentStmt.setDouble(2, totalPrice);
                        paymentStmt.executeUpdate();
                    }
                    
                    // Update car status
                    PreparedStatement carStmt = conn.prepareStatement(
                            "UPDATE cars SET status = 'Rented' WHERE id = ?"
                    );
                    carStmt.setInt(1, selectedCar.getCarId());
                    
                    int carSuccess = carStmt.executeUpdate();
                    
                    if (bookingSuccess > 0 && carSuccess > 0) {
                        // Commit the transaction
                        conn.commit();
                        
                        // Show success message
                        showBookingConfirmation(selectedCar, startDate, endDate, totalPrice);
                        
                        // Refresh data
                        loadAllCars();
                        
                        statusLabel.setText("Car booked successfully! Total payment: $" + String.format("%.2f", totalPrice));
                    } else {
                        // Rollback if something went wrong
                        conn.rollback();
                        statusLabel.setText("Failed to book car. Please try again.");
                    }
                } catch (SQLException e) {
                    // Rollback on error
                    conn.rollback();
                    throw e;
                } finally {
                    // Restore auto-commit mode
                    conn.setAutoCommit(true);
                }
                
            } catch (SQLException e) {
                statusLabel.setText("Error booking car: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void updateEstimatedPrice(DatePicker startDatePicker, DatePicker endDatePicker,
                                      car selectedCar, Label priceLabel) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                priceLabel.setText("Invalid date range");
            } else {
                long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                double totalPrice = days * selectedCar.getPricePerDay();
                priceLabel.setText(String.format("$%.2f (%d days × $%.2f)",
                        totalPrice, days, selectedCar.getPricePerDay()));
            }
        }
    }
    
    private void checkBookingConflicts(int carId, LocalDate startDate, LocalDate endDate, Label statusLabel) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            statusLabel.setText("Invalid date range");
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT * FROM bookings " +
                            "WHERE car_id = ? " +
                            "AND status NOT IN ('Cancelled', 'Completed') " +
                            "AND ((start_date BETWEEN ? AND ?) " +
                            "OR (end_date BETWEEN ? AND ?) " +
                            "OR (start_date <= ? AND end_date >= ?))"
            );
            
            pstmt.setInt(1, carId);
            pstmt.setDate(2, java.sql.Date.valueOf(startDate));
            pstmt.setDate(3, java.sql.Date.valueOf(endDate));
            pstmt.setDate(4, java.sql.Date.valueOf(startDate));
            pstmt.setDate(5, java.sql.Date.valueOf(endDate));
            pstmt.setDate(6, java.sql.Date.valueOf(startDate));
            pstmt.setDate(7, java.sql.Date.valueOf(endDate));
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Found a conflict
                LocalDate conflictStart = rs.getDate("start_date").toLocalDate();
                LocalDate conflictEnd = rs.getDate("end_date").toLocalDate();
                
                statusLabel.setText("Booking conflict: Car already booked from " +
                        conflictStart + " to " + conflictEnd);
            } else {
                // No conflict
                statusLabel.setText("");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error checking booking conflicts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showBookingConfirmation(car car, LocalDate startDate, LocalDate endDate, double totalPrice) {
        // Create confirmation dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking Confirmation");
        alert.setHeaderText("Your car has been booked successfully!");
        
        // Create content grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));
        
        grid.add(new Label("Car:"), 0, 0);
        grid.add(new Label(car.getBrand() + " " + car.getModel()), 1, 0);
        grid.add(new Label("Start Date:"), 0, 1);
        grid.add(new Label(startDate.toString()), 1, 1);
        grid.add(new Label("End Date:"), 0, 2);
        grid.add(new Label(endDate.toString()), 1, 2);
        grid.add(new Label("Total Price:"), 0, 3);
        grid.add(new Label(String.format("$%.2f", totalPrice)), 1, 3);
        grid.add(new Label("Payment Status:"), 0, 4);
        grid.add(new Label("Pending"), 1, 4);
        
        alert.getDialogPane().setContent(grid);
        alert.showAndWait();
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
                        rs.getString("email"),
                        rs.getString("password_hash")
                );
                customerListDSA.add(c);
            }
        } catch (SQLException e) {
            statusLabel.setText("Error loading customers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCurrentUser(int userId) {
        this.currentUserId = userId;
        loadCustomersToDSA();
        customer currentCustomer = customerListDSA.findById(userId);
        if (currentCustomer != null && welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + currentCustomer.getName());
        } else {
            System.out.println("Could not find customer with ID: " + userId);
        }
        loadAllCars();
    }

    // Static variable to pass car info between controllers
    public static car selectedCarForBooking;

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/carrental/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 400, 400);
            stage.setScene(scene);
            stage.setTitle("Car Rental System - Login");
            stage.show();
        } catch (Exception e) {
            statusLabel.setText("Error logging out: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 