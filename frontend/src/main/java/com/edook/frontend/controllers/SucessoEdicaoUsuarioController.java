package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class SucessoEdicaoUsuarioController {

    private Runnable onFinalizarAction;

    public void setOnFinalizar(Runnable onFinalizarAction) {
        this.onFinalizarAction = onFinalizarAction;
    }

    @FXML
    void onClickFinalizar(ActionEvent event) {
        if (onFinalizarAction != null) {
            onFinalizarAction.run(); // Executa o retorno visual para a tela de visualização
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}