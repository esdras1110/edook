package com.edook.frontend.components;

import com.edook.frontend.models.ReservaResponseDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

// Controller de célula demonstrativa de uma reserva com data, hora, equipamentos e local.
// Usada para adicionar dinamicamente representação de reservas em popups de confirmação de operação.
public class ItemReservaController {

    @FXML
    private Label lblData, lblHora, lblEquipamentos, lblLocal;

    public void setDadosReserva(ReservaResponseDTO reserva) {
        lblData.setText(reserva.getDataFormatada());
        lblHora.setText(reserva.getHorarioFormatado());
        lblEquipamentos.setText(reserva.getEquipamentosFormatados());
        lblLocal.setText(reserva.getLocalidade());
    }
}
