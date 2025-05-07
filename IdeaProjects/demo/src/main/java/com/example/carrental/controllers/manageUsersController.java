package com.example.carrental.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.carrental.DBConnection;
import com.example.carrental.models.customer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class manageUsersController {
    @FXML private TableView<customer> usersTable;
    @FXML private TableColumn<customer, Integer> idColumn;
    @FXML private TableColumn<customer, String> nameColumn;
    @FXML private TableColumn<customer, String> contactColumn;
    @FXML private TableColumn<customer, String> licenseColumn;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        // Initialize table columns
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCustomerId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        contactColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getContact()));
        licenseColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLicenseNo()));

        // Load users on initialization
        loadUsers();
    }

    @FXML
    private void handleAddUser(ActionEvent event) {
        // This would open a new window to add a user, similar to the car example
        // For now, we'll just show a status message
        statusLabel.setText("Add User functionality to be implemented");
    }

    @FXML
    private void handleDeleteUser(ActionEvent event) {
        customer selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            statusLabel.setText("Please select a user to delete");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM customers WHERE id = ?")) {
            pstmt.setInt(1, selectedUser.getCustomerId());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                statusLabel.setText("User deleted successfully");
                loadUsers(); // Refresh the table
            } else {
                statusLabel.setText("User not found or could not be deleted");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadUsers();
    }

    private void loadUsers() {
        ObservableList<customer> userList = FXCollections.observableArrayList();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM customers");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                customer user = new customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("license_no"),
                        rs.getString("password")
                );
                userList.add(user);
            }
            usersTable.setItems(userList);
            usersTable.refresh(); // Ensure the TableView refreshes
            statusLabel.setText("Loaded " + userList.size() + " users successfully");
        } catch (SQLException e) {
            statusLabel.setText("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 