package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

// Controlador da tela de Confirmação de Sucesso Exclusão de Equipamento, modal genérico de sucesso.
// Mais detalhado em SucessoCadastroEquipamentoController
public class SucessoExclusaoEquipamentoController {

    @FXML
    private void onClickFinalizar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}