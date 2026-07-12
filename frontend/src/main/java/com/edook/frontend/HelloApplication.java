package com.edook.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.io.IOException;

// Classe principal do JavaFX, configura o ambiente gráfico inicial, carrega os recursos globais e exibe a primeiro tela, Login
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Carregamento de Fontes Customizadas
        Font.loadFont(getClass().getResourceAsStream("/com/edook/frontend/Fonts/Poppins/Poppins-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/com/edook/frontend/Fonts/Poppins/Poppins-Bold.ttf"), 14);

        Font.loadFont(getClass().getResourceAsStream("/com/edook/frontend/Fonts/Inter/Inter_28pt-Regular.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/com/edook/frontend/Fonts/Inter/Inter_28pt-SemiBold.ttf"), 14);

        // Carrega FXML da primeira tela, Login
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 1024);

        // Aplica a folha de estilos CSS global
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        // Configura o Stage e exibe a janela
        stage.setTitle("edook - Login");
        stage.setScene(scene);
        stage.show();
    }
}
