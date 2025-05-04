package com.example.carrental.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import com.example.carrental.SceneSwitcher;
import com.example.carrental.models.car;

public class adminController {
    @FXML private TableView<car> carTable;
    @FXML private TableColumn<car, Integer> idColumn;
    @FXML private TableColumn<car, String> modelColumn;
    @FXML private TableColumn<car, String> brandColumn;
    @FXML private TableColumn<car, Integer> yearColumn;
    @FXML private TableColumn<car, Double> priceColumn;
    @FXML private Button addCarButton;
    @FXML private Button removeCarButton;
    @FXML private Button backButton;

    @FXML
    private void handleAddCar(ActionEvent event) {
        // TODO: Show add car dialog
    }

    @FXML
    private void handleRemoveCar(ActionEvent event) {
        // TODO: Remove selected car
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