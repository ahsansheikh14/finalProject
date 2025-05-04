module com.example.carrental {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.carrental to javafx.fxml;
    exports com.example.carrental;
}