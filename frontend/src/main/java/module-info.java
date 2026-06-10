module com.edook.frontend {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.edook.frontend to javafx.fxml;
    exports com.edook.frontend;
}