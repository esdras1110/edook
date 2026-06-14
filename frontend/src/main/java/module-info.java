module com.edook.frontend {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.edook.frontend to javafx.fxml;
    exports com.edook.frontend;
    exports com.edook.frontend.controllers;
    opens com.edook.frontend.controllers to javafx.fxml;
    opens com.edook.frontend.models to javafx.base, javafx.fxml;
}