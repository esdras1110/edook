package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class SucessoRedefinicaoSenhaController {

    @FXML
    private void onClickFinalizar(ActionEvent event) {
        // Encerra o pop-up de sucesso liberando a tela principal do sistema
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}