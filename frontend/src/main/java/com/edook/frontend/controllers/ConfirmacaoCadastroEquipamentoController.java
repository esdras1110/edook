package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmacaoCadastroEquipamentoController {
    @FXML
    private Label lblDescricao, lblTipo;

    private Runnable onConfirmar;

    public void setDados(String descricao, String tipo) {
        lblDescricao.setText(descricao);
        lblTipo.setText(tipo);
    }

    public void setOnConfirmar(Runnable onConfirmar) {
        this.onConfirmar = onConfirmar;
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        fecharJanela(event);
    }

    @FXML
    void onClickConfirmar(ActionEvent event) {
        if (onConfirmar != null) {
            onConfirmar.run(); // Executa o envio assíncrono programado no CadastroEquipamentoController
        }
        fecharJanela(event);
    }

    private void fecharJanela(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}