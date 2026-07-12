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

// Controlador do modal de Filtros Avançados de Reservas.
public class FiltroReservasController implements Initializable {
    @FXML
    private ComboBox<String> cbEquipamento, cbLocal, cbEstado;

    @FXML
    private DatePicker dpDataInicio, dpDataFim;

    @FXML
    private TextField txtHoraInicio, txtHoraFim;

    // Referência a tela principal que implementa a interface Filtravel.
    private Filtravel telaPai;

    // Função executado ao abrir a janela.
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComponentes();
    }

    // Preenche as listas suspensas e aplica máscaras e comportamentos aos campos de data e hora para evitar dados inválidos.
    private void configurarComponentes() {
        configurarComportamentoDatePicker(dpDataInicio);
        configurarComportamentoDatePicker(dpDataFim);
        // Aplica o filtro de formatação HH:mm nos campos de texto
        aplicarMascaraHorario(txtHoraInicio);
        aplicarMascaraHorario(txtHoraFim);

        // Preenchimento estático das opções de filtro
        cbEquipamento.setItems(javafx.collections.FXCollections.observableArrayList(
                "Projetor",
                "Notebook",
                "Televisão",
                "Caixa de Som"
        ));

        cbLocal.setItems(javafx.collections.FXCollections.observableArrayList(
                "Sala 1",
                "Sala 2",
                "Sala 3",
                "Sala 4",
                "Sala 5",
                "Sala 6",
                "Sala 7",
                "Sala 8",
                "Sala 9",
                "Giroteca"
        ));

        cbEstado.setItems(javafx.collections.FXCollections.observableArrayList(
                "Pendente",
                "Cancelada"
        ));
    }

    // Define quem é o ecrã pai que vai receber os filtros no final.
    public void setTelaPai(Filtravel telaPai) {
        this.telaPai = telaPai;
    }

    // Recolhe todos os valores preenchidos, agrupa-os num objeto DTO e devolve-os a tela pai antes de fechar a janela.
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

    // Limpa todos os filtros.
    @FXML
    private void onClickLimpar(ActionEvent event) {
        telaPai.setFiltrosAvancados(null);
        ((Stage) cbEquipamento.getScene().getWindow()).close();
    }

    // Impede que o utilizador escreva datas manualmente e força a abertura do calendário ao clicar no campo de texto.
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

    // Aplica uma máscara que formata a hora automaticamente e valida os caracteres introduzidos através de Regex
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

    // Se o usuário já tiver filtrado algo antes, este Função preenche os campos com as escolhas antigas
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