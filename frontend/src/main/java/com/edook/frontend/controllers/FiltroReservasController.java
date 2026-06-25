package com.edook.frontend.controllers;

import com.edook.frontend.models.FiltroReservaDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class FiltroReservasController implements Initializable {
    @FXML
    private ComboBox<String> cbEquipamento;

    @FXML
    private ComboBox<String> cbLocal;

    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private DatePicker dpDataInicio;

    @FXML
    private DatePicker dpDataFim;

    @FXML
    private TextField txtHoraInicio;

    @FXML
    private TextField txtHoraFim;

    private Filtravel telaPai;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComponentes();
    }

    private void configurarComponentes() {
        configurarComportamentoDatePicker(dpDataInicio);
        configurarComportamentoDatePicker(dpDataFim);
        aplicarMascaraHorario(txtHoraInicio);
        aplicarMascaraHorario(txtHoraFim);

        cbEquipamento.setItems(javafx.collections.FXCollections.observableArrayList(
                "Projetor",
                "Notebook",
                "Televisão",
                "Caixa de Som"
        ));

        cbLocal.setItems(javafx.collections.FXCollections.observableArrayList(
                "Sala 1 (9°)",
                "Lab 3",
                "Sala de Robótica",
                "Auditório",
                "Mini Auditório"
        ));

        cbEstado.setItems(javafx.collections.FXCollections.observableArrayList(
                "Pendente",
                "Cancelada"
        ));
    }

    public void setTelaPai(Filtravel telaPai) {
        this.telaPai = telaPai;
    }

    @FXML
    private void onClickAplicar(ActionEvent event) {
        FiltroReservaDTO filtro = new FiltroReservaDTO();
        filtro.equipamento = cbEquipamento.getValue();
        filtro.local = cbLocal.getValue();
        filtro.dataInicio = dpDataInicio.getValue();
        filtro.dataFim = dpDataFim.getValue();
        filtro.horarioInicio = txtHoraInicio.getText();
        filtro.horarioFim = txtHoraFim.getText();
        filtro.status = cbEstado.getValue();

        telaPai.setFiltrosAvancados(filtro);

        ((Stage) cbEquipamento.getScene().getWindow()).close();
    }

    @FXML
    private void onClickLimpar(ActionEvent event) {
        telaPai.setFiltrosAvancados(null);
        ((Stage) cbEquipamento.getScene().getWindow()).close();
    }

    private void configurarComportamentoDatePicker(DatePicker datePicker) {
        if (datePicker != null) {
            datePicker.setEditable(false);

            datePicker.getEditor().setOnMouseClicked(event -> {
                if (!datePicker.isShowing()) {
                    datePicker.show();
                }
            });
        }
    }

    private void aplicarMascaraHorario(TextField textField) {
        if (textField == null) {
            return;
        }

        textField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.isDeleted()) {
                return change;
            }

            String textoFuturo = change.getControlNewText();

            if (textoFuturo.length() > 5) {
                return null;
            }

            if (textoFuturo.length() == 3 && !textoFuturo.contains(":")) {
                String caractereDigitado = change.getText();
                change.setText(":" + caractereDigitado);
                change.setCaretPosition(change.getCaretPosition() + 1);
                change.setAnchor(change.getAnchor() + 1);
                textoFuturo = change.getControlNewText();
            }

            if (textoFuturo.matches("^([0-1]?[0-9]|2[0-3])?:?[0-5]?[0-9]?$")) {
                return change;
            }

            return null;
        }));
    }

    public void carregarFiltrosSalvos(FiltroReservaDTO filtro) {
        if (filtro == null) return;

        cbEquipamento.setValue(filtro.equipamento);
        cbLocal.setValue(filtro.local);
        dpDataInicio.setValue(filtro.dataInicio);
        dpDataFim.setValue(filtro.dataFim);
        txtHoraInicio.setText(filtro.horarioInicio);
        txtHoraFim.setText(filtro.horarioFim);
        cbEstado.setValue(filtro.status);
    }
}