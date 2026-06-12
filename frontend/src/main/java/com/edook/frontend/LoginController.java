package com.edook.frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField campoTextLogin;

    @FXML
    private PasswordField campoSenhaLogin;

    @FXML
    private TextField campoTextRecuperacaoSenha;

    @FXML
    private Label labelErroLogin;

    @FXML
    private Label labelErroEmail;

    @FXML
    private Label labelErroCodigo;

    @FXML
    private Label labelErroSenha;

    @FXML
    private VBox vboxLogin;

    @FXML
    private VBox vboxEsqueceuSenha;

    @FXML
    private VBox vboxCodigo;

    @FXML
    private VBox vboxNovaSenha;

    @FXML
    void onClickEntrar(ActionEvent event) {
        String texto = campoTextLogin.getText().trim();
        String senha = campoSenhaLogin.getText().trim();

        labelErroLogin.setText("");

        if (texto.isEmpty() || senha.isEmpty()) {
            labelErroLogin.setText("Por favor, preencha todos os campos obrigatórios.");
            labelErroLogin.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!texto.matches("\\d+") && !texto.contains("@")) {
            labelErroLogin.setText("Por favor, insira um e-mail válido ou CPF.");
            labelErroLogin.setStyle("-fx-text-fill: red;");
            return;
        }

        if (senha.length() < 6) {
            labelErroLogin.setText("A senha deve conter pelo menos 6 caracteres.");
            labelErroLogin.setStyle("-fx-text-fill: red;");
            return;
        }

        labelErroLogin.setText("Enviando dados...");
        labelErroLogin.setStyle("-fx-text-fill: green;");
    }

    @FXML
    void onClickEsqueceuSenha(ActionEvent event) {
        vboxLogin.setVisible(false);
        vboxLogin.setManaged(false);
        vboxEsqueceuSenha.setVisible(true);
        vboxEsqueceuSenha.setManaged(true);
    }

    @FXML
    void onClickEnviar(ActionEvent event) {
        String texto = campoTextRecuperacaoSenha.getText().trim();

        labelErroEmail.setText("");

        if (texto.isEmpty()) {
            labelErroEmail.setText("Por favor, preencha o campo obrigatório.");
            labelErroEmail.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!texto.contains("@")) {
            labelErroEmail.setText("Por favor, insira um e-mail válido.");
            labelErroEmail.setStyle("-fx-text-fill: red;");
            return;
        }

        vboxEsqueceuSenha.setVisible(false);
        vboxEsqueceuSenha.setManaged(false);
        vboxCodigo.setVisible(true);
        vboxCodigo.setManaged(true);
    }

    @FXML
    void onClickVoltarLogin(ActionEvent event) {
        vboxEsqueceuSenha.setVisible(false);
        vboxEsqueceuSenha.setManaged(false);
        vboxLogin.setVisible(true);
        vboxLogin.setManaged(true);
    }

    @FXML
    void onClickVoltarEsqueceuSenha(ActionEvent event) {
        vboxCodigo.setVisible(false);
        vboxCodigo.setManaged(false);
        vboxEsqueceuSenha.setVisible(true);
        vboxEsqueceuSenha.setManaged(true);
    }

}
