package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

// Controlador do modal Confirmar Ediçao de Equipamento. Semelhante aos outros controladores de confirmação.
// ConfirmacaoCadastroEquipamentoControlle comentado detalhadamente.
public class ConfirmacaoEdicaoEquipamentoController {

    @FXML private Label lblDescricaoAntiga;
    @FXML private Label lblDescricaoNova;

    private Runnable onConfirmar;

    public void setData(String descricaoAntiga, String novaDescricao) {
        lblDescricaoAntiga.setText(descricaoAntiga);
        lblDescricaoNova.setText(novaDescricao);
    }

    public void setOnConfirmar(Runnable onConfirmar) {
        this.onConfirmar = onConfirmar;
    }

    @FXML
    private void onClickConfirmar(ActionEvent event) {
        if (onConfirmar != null) {
            onConfirmar.run();
        }
        onClickVoltar(event);
    }

    @FXML
    private void onClickVoltar(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}