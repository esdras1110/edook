package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmacaoEdicaoUsuarioController {

    @FXML
    private Label lblNomeAntigo, lblTelefoneAntigo, lblNomeNovo, lblTelefoneNovo;

    private Runnable onConfirmarAction;

    public void setDados(String nomeAntigo, String telefoneAntigo, String nomeNovo, String telefoneNovo, Runnable onConfirmarAction) {
        this.lblNomeAntigo.setText("Nome antigo: " + nomeAntigo);
        this.lblTelefoneAntigo.setText("Tel antigo: " + telefoneAntigo);
        this.lblNomeNovo.setText("Nome novo: " + nomeNovo);
        this.lblTelefoneNovo.setText("Tel novo: " + telefoneNovo);
        this.onConfirmarAction = onConfirmarAction;
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void onClickConfirmar(ActionEvent event) {
        if (onConfirmarAction != null) {
            onConfirmarAction.run(); // Executa a requisição HTTP PUT
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}