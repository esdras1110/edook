package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

// Controlador da tela de Confirmação de Sucesso Exclusão de Usuário, modal genérico de sucesso.
// Mais detalhado em SucessoCadastroEquipamentoController
public class SucessoExclusaoUsuarioController {

    private Runnable onFinalizar;

    public void setOnFinalizar(Runnable onFinalizar) {
        this.onFinalizar = onFinalizar;
    }

    @FXML
    void onClickFinalizar(ActionEvent event) {
        if (onFinalizar != null) {
            onFinalizar.run();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}