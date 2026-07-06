package com.edook.frontend.components;

import com.edook.frontend.models.ReservaResponseDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ItemReservaController {

    @FXML
    private Label lblData;

    @FXML
    private Label lblHora;

    @FXML
    private Label lblEquipamentos;

    @FXML
    private Label lblLocal;

    public void setDadosReserva(ReservaResponseDTO reserva) {
        lblData.setText(reserva.getDataFormatada());
        lblHora.setText(reserva.getHorarioFormatado());
        lblEquipamentos.setText(reserva.getEquipamentosFormatados());
        lblLocal.setText(reserva.getLocalidade());
    }
}
