package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

// Controlador da tela de Confirmação de Sucesso Cadastro de Equipamento
public class SucessoCadastroEquipamentoController {

    // Variável que armazena uma função passada por outra tela
    private Runnable onFinalizar;

    public void setOnFinalizar(Runnable onFinalizar) {
        this.onFinalizar = onFinalizar;
    }

    // Ação do botão Finalizar, executa a rotina injetada e fecha o popup em seguida.
    @FXML
    void onClickFinalizar(ActionEvent event) {
        if (onFinalizar != null) {
            onFinalizar.run();
        }
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}