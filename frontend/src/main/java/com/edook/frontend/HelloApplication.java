package com.edook.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(getClass().getResourceAsStream("/com/edook/frontend/Fonts/Poppins/Poppins-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/com/edook/frontend/Fonts/Poppins/Poppins-Bold.ttf"), 14);

        Font.loadFont(getClass().getResourceAsStream("/com/edook/frontend/Fonts/Inter/Inter_28pt-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/com/edook/frontend/Fonts/Inter/Inter_28pt-SemiBold.ttf"), 14);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 1024);

        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        stage.setTitle("edook - Login");
        stage.setScene(scene);
        stage.show();
    }
}
