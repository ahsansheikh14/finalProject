module com.example.carrental {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    requires javafx.graphics;

    opens com.example.carrental to javafx.fxml;
    opens com.example.carrental.controllers to javafx.fxml;
    opens com.example.carrental.models to javafx.base;
    opens com.example.carrental.dsa to javafx.base;

    exports com.example.carrental;
    exports com.example.carrental.controllers;
    exports com.example.carrental.models;
    exports com.example.carrental.dsa;
}
