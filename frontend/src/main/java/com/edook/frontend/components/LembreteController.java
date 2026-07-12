package com.edook.frontend.components;

import com.edook.frontend.models.ReservaResponseDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import java.io.IOException;


// Controlador responsável por definir a aparência de cada item na lista de lembretes.
// Usamos herança de ListCell para conseguir injetar FXML dentro da lista.
public class LembreteController extends ListCell<ReservaResponseDTO>{

    // Função chamado pelo JavaFX sempre que a tela precisa exibir ou atualizar um lembrete na lista.
    @Override
    protected void updateItem(ReservaResponseDTO reserva, boolean empty) {
        super.updateItem(reserva, empty);
        // Limpa a célula se não houver dados.
        if (empty || reserva == null) {
            setText(null);
            setGraphic(null);
        } else {
            try {
                // Carrega o arquivo FXML que contém o layout visual de um único lembrete
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/Lembrete-view.fxml"));
                Parent root = loader.load();

                // Busca os labels dentro do layout usando os IDs
                Label lblHorario = (Label) root.lookup("#lblHorario");
                Label lblLocal = (Label) root.lookup("#lblLocal");
                Label lblTitulo = (Label) root.lookup("#lblTitulo");

                // Preenche os campos com os dados da reserva.
                if (lblHorario != null) lblHorario.setText(reserva.getHorarioFormatado());
                if (lblLocal != null) lblLocal.setText(reserva.getLocalidade());
                if (lblTitulo != null) lblTitulo.setText(reserva.getNome());

                setGraphic(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
