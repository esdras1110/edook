package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

// Controlador da tela de Confirmação de Sucesso Cancelamento de Reserva, modal genérico de sucesso.
// Mais detalhado em SucessoCadastroEquipamentoController
public class SucessoCancelamentoReservaController {

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