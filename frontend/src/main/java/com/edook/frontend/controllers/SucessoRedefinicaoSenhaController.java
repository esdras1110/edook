package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

// Controlador da tela de Confirmação de Sucesso Redefinição de Senha, modal genérico de sucesso.
// Mais detalhado em SucessoCadastroEquipamentoController
public class SucessoRedefinicaoSenhaController {

    @FXML
    private void onClickFinalizar(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}