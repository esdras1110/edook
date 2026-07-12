package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

// Controlador da tela de Confirmação de Sucesso Edição de Usuário, modal genérico de sucesso.
// Mais detalhado em SucessoCadastroEquipamentoController
public class SucessoEdicaoUsuarioController {

    private Runnable onFinalizarAction;

    public void setOnFinalizar(Runnable onFinalizarAction) {
        this.onFinalizarAction = onFinalizarAction;
    }

    @FXML
    void onClickFinalizar(ActionEvent event) {
        if (onFinalizarAction != null) {
            onFinalizarAction.run();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}