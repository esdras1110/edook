package com.edook.frontend.components;

import com.edook.frontend.models.Reserva;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class CelulasLembretes extends ListCell<Reserva> {

    @Override
    protected void updateItem(Reserva reserva, boolean empty) {
        super.updateItem(reserva, empty);

        if (empty || reserva == null) {
            setText(null);
            setGraphic(null);
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/Lembrete-view.fxml"));
                Parent root = loader.load();

                Label lblHorario = (Label) root.lookup("#lblHorario");
                Label lblLocal = (Label) root.lookup("#lblLocal");
                Label lblTitulo = (Label) root.lookup("#lblTitulo");

                if (lblHorario != null) lblHorario.setText(reserva.getHorario());
                if (lblLocal != null) lblLocal.setText(reserva.getLocal());
                if (lblTitulo != null) lblTitulo.setText(reserva.getEquipamento());

                setGraphic(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
