package com.edook.frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField campoTextLogin;

    @FXML
    private PasswordField campoSenhaLogin;

    @FXML
    private Label labelErro;

    @FXML
    void onClickEntrar(ActionEvent event) {
        String texto = campoTextLogin.getText().trim();
        String senha = campoSenhaLogin.getText().trim();

        labelErro.setText("");

        if (texto.isEmpty() || senha.isEmpty()) {
            labelErro.setText("Por favor, preencha todos os campos obrigatórios.");
            labelErro.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!texto.matches("\\d+") && !texto.contains("@")) {
            labelErro.setText("Por favor, insira um e-mail válido ou CPF.");
            labelErro.setStyle("-fx-text-fill: red;");
            return;
        }

        if (senha.length() < 6) {
            labelErro.setText("A senha deve conter pelo menos 6 caracteres.");
            labelErro.setStyle("-fx-text-fill: red;");
            return;
        }

        labelErro.setText("Enviando dados...");
        labelErro.setStyle("-fx-text-fill: green;");
    }

    @FXML
    void onClickEsqueceuSenha(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RecuperacaoSenha-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = new Stage();
            stage.setTitle("edook - Recuperar Senha");
            stage.setScene(scene);
            stage.show();

            Stage stageLogin = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageLogin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
