package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class SucessoEsqueceuSenhaController {

    @FXML
    void onClickFinalizar(ActionEvent event) {
        // Obtém o Stage (janela) do popup a partir do evento do botão e o fecha
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}