package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

// Controlador do modal Confirmar Cadastro de Reserva. Semelhante aos outros controladores de confirmação.
// ConfirmacaoCadastroEquipamentoControlle comentado detalhadamente.
public class ConfirmacaoCadastroReservaController {

    @FXML
    private Label lblData, lblHora, lblEquipamento, lblSala;

    private Runnable onConfirmar;

    public void setDados(String data, String hora, String equipamento, String sala) {
        lblData.setText(data);
        lblHora.setText(hora);
        lblEquipamento.setText(equipamento);
        lblSala.setText(sala);
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
            onConfirmar.run();
        }
        fecharJanela(event);
    }

    private void fecharJanela(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}