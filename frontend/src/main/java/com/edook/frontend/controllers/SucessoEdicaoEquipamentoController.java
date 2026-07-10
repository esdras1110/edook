package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class SucessoEdicaoEquipamentoController {

    private Runnable onFinalizar;

    public void setOnFinalizar(Runnable onFinalizar) {
        this.onFinalizar = onFinalizar;
    }

    @FXML
    void onClickFinalizar(ActionEvent event) {
        if (onFinalizar != null) {
            onFinalizar.run();
        }
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}