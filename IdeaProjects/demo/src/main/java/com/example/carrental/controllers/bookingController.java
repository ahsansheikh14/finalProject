package com.example.carrental.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import com.example.carrental.SceneSwitcher;
import com.example.carrental.models.car;
import com.example.carrental.models.booking;

public class bookingController {
    @FXML private TableView<car> carTable;
    @FXML private TableView<booking> bookingTable;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button bookButton;
    @FXML private Button backButton;

    @FXML
    private void handleBook(ActionEvent event) {
        // TODO: Book selected car for selected dates
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/com/example/carrental/dashboard.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}