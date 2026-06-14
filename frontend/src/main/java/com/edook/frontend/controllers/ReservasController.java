package com.edook.frontend.controllers;

import com.edook.frontend.components.CelulasLembretes;
import com.edook.frontend.models.Reserva;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ReservasController implements Initializable {

    @FXML
    private TableView<Reserva> tabelaReservas;

    @FXML
    private TableColumn<Reserva, String> colFuncionario;

    @FXML
    private TableColumn<Reserva, String> colTitulo;

    @FXML
    private TableColumn<Reserva, String> colData;

    @FXML
    private TableColumn<Reserva, String> colHorario;

    @FXML
    private TableColumn<Reserva, String> colEquipamento;

    @FXML
    private TableColumn<Reserva, String> colLocal;

    @FXML
    private TableColumn<Reserva, String> colStatus;

    @FXML
    private Button btnMinhasReservas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabelaReservas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        colFuncionario.setCellValueFactory(new PropertyValueFactory<>("funcionario"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horario"));
        colEquipamento.setCellValueFactory(new PropertyValueFactory<>("equipamento"));
        colLocal.setCellValueFactory(new PropertyValueFactory<>("local"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        Reserva teste1 = new Reserva("João Silva", "Reunião", "14/06", "13:30", "Projetor Epson", "Sala 1 (9°)", "Disponivel");
        Reserva teste2 = new Reserva("Maria Souza", "Aula Prática", "15/06", "08:00", "Notebook Dell", "Lab 3", "Reservado");
        Reserva teste3 = new Reserva("Carlos Lima", "Apresentação", "15/06", "10:15", "Kit Arduino", "Sala de Robótica", "Disponivel");
        Reserva teste4 = new Reserva("Ana Costa", "Palestra", "16/06", "14:00", "Caixa de Som JBL", "Auditório", "Manutenção");
        Reserva teste5 = new Reserva("José Santos", "Workshop", "17/06", "19:00", "Microfone Sem Fio", "Mini Auditório", "Disponivel");

        if (tabelaReservas != null) {
            tabelaReservas.getItems().addAll(teste1, teste2, teste3, teste4, teste5);
        }

    }

//    @FXML
//    private void onClickMinhasReservas(ActionEvent event) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/Reservas-view.fxml"));
//            Parent minhasReservasView = loader.load();
//
//            mainLayout.setCenter(minhasReservasView);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
