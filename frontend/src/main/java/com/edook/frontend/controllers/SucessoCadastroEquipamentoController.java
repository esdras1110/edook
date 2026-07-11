package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class SucessoCadastroEquipamentoController {

    private Runnable onFinalizar;

    public void setOnFinalizar(Runnable onFinalizar) {
        this.onFinalizar = onFinalizar;
    }

    @FXML
    void onClickFinalizar(ActionEvent event) {
        if (onFinalizar != null) {
            onFinalizar.run(); // Fecha a tela de formulário anterior e executa o refresh da tabela
        }
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}