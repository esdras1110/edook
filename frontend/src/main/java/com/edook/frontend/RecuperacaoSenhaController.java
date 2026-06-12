package com.edook.frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class RecuperacaoSenhaController {
    @FXML
    private TextField campoTextRecuperacaoSenha;

    @FXML
    private Label labelErroEmail;

    @FXML
    private Label labelErroCodigo;

    @FXML
    private VBox vboxFormulario;

    @FXML
    private VBox vboxCodigo;

    @FXML
    private VBox vboxNovaSenha;

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

        vboxFormulario.setVisible(false);
        vboxFormulario.setManaged(false);
        vboxCodigo.setVisible(true);
        vboxCodigo.setManaged(true);
    }

    @FXML
    void onClickVoltar1(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = new Stage();
            stage.setTitle("edook - Login");
            stage.setScene(scene);
            stage.show();

            Stage stageRecuperacaoSenha = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageRecuperacaoSenha.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onClickVoltar2(ActionEvent event) {
        vboxFormulario.setVisible(true);
        vboxFormulario.setManaged(true);
        vboxCodigo.setVisible(false);
        vboxCodigo.setManaged(false);
    }
}
