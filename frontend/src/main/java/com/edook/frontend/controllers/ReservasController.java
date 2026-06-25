package com.edook.frontend.controllers;

import com.edook.frontend.models.FiltroReservaDTO;
import com.edook.frontend.models.Reserva;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ReservasController implements Initializable, Filtravel {
    @FXML
    private TextField campoBusca;

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

    private final ObservableList<Reserva> dadosOriginais = FXCollections.observableArrayList();
    private FilteredList<Reserva> dadosFiltrados;
    private final String USUARIO_LOGADO = "João Silva";
    private boolean filtrarApenasMinhas = false;
    private FiltroReservaDTO filtrosAvancados = null;

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
        dadosOriginais.addAll(teste1, teste2, teste3, teste4, teste5);
        dadosFiltrados = new FilteredList<>(dadosOriginais, p -> true);

        if (tabelaReservas != null) {
            tabelaReservas.setItems(dadosFiltrados);
        }

        if (campoBusca != null) {
            campoBusca.textProperty().addListener((observable, oldValue, newValue) -> {
                aplicarFiltros();
            });
        }
    }

    public void setFiltrosAvancados(FiltroReservaDTO filtros) {
        this.filtrosAvancados = filtros;
        aplicarFiltros();
    }

    @FXML
    private void onClickMinhasReservas(ActionEvent event) {
        filtrarApenasMinhas = !filtrarApenasMinhas;

        if (filtrarApenasMinhas) {
            btnMinhasReservas.setText("Mostrar Todas");
            btnMinhasReservas.setStyle("-fx-background-color: #6B7280;");
        } else {
            btnMinhasReservas.setText("Minhas Reservas");
            btnMinhasReservas.setStyle("");
        }

        aplicarFiltros();
    }

    @FXML
    private void onClickFiltros(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/FiltroReservas-view.fxml"));
            Parent root = loader.load();

            FiltroReservasController popupController = loader.getController();
            popupController.setTelaPai(this);

            if (this.filtrosAvancados != null) {
                popupController.carregarFiltrosSalvos(this.filtrosAvancados);
            }

            Stage popupStage = new Stage();
            Stage donoDaJanela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();

            popupStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    popupStage.close();
                }
            });

            GaussianBlur blur = new GaussianBlur(15);
            rootPrincipal.setEffect(blur);

            popupStage.initOwner(donoDaJanela);
            popupStage.initModality(javafx.stage.Modality.NONE);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());

            popupStage.setScene(scene);
            popupStage.centerOnScreen();

            popupStage.showAndWait();

            rootPrincipal.setEffect(null);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar o pop-up de filtros: " + e.getMessage());
        }
    }

    private void aplicarFiltros() {
        String termoBusca = (campoBusca.getText() == null) ? "" : campoBusca.getText().toLowerCase().trim();

        dadosFiltrados.setPredicate(reserva -> {
            if (!termoBusca.isEmpty()) {
                boolean bateComTexto = false;
                if (reserva.getEquipamento() != null && reserva.getEquipamento().toLowerCase().contains(termoBusca))
                    bateComTexto = true;
                if (reserva.getLocal() != null && reserva.getLocal().toLowerCase().contains(termoBusca))
                    bateComTexto = true;
                if (!bateComTexto) return false;
            }

            if (filtrosAvancados != null) {
                if (filtrosAvancados.equipamento != null && !filtrosAvancados.equipamento.isEmpty()) {
                    if (reserva.getEquipamento() == null || !reserva.getEquipamento().toLowerCase().contains(filtrosAvancados.equipamento.toLowerCase())) {
                        return false;
                    }
                }
                if (filtrosAvancados.status != null && !filtrosAvancados.status.isEmpty()) {
                    if (reserva.getStatus() == null || !reserva.getStatus().toLowerCase().contains(filtrosAvancados.status.toLowerCase())) {
                        return false;
                    }
                }
                if (filtrosAvancados.local != null && !filtrosAvancados.local.isEmpty()) {
                    if (reserva.getLocal() == null || !reserva.getLocal().equalsIgnoreCase(filtrosAvancados.local))
                        return false;
                }
                if (filtrosAvancados.horarioInicio != null && !filtrosAvancados.horarioInicio.trim().isEmpty()) {
                    if (reserva.getHorario() == null || reserva.getHorario().compareTo(filtrosAvancados.horarioInicio) < 0)
                        return false;
                }
                if (filtrosAvancados.horarioFim != null && !filtrosAvancados.horarioFim.trim().isEmpty()) {
                    if (reserva.getHorario() == null || reserva.getHorario().compareTo(filtrosAvancados.horarioFim) > 0)
                        return false;
                }
                if (filtrosAvancados.dataInicio != null || filtrosAvancados.dataFim != null) {
                    if (reserva.getData() == null) return false;
                    try {
                        String dataCompletaStr = reserva.getData() + "/" + java.time.Year.now().getValue();
                        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy");
                        java.time.LocalDate dataReserva = java.time.LocalDate.parse(dataCompletaStr, formatter);

                        if (filtrosAvancados.dataInicio != null && dataReserva.isBefore(filtrosAvancados.dataInicio))
                            return false;
                        if (filtrosAvancados.dataFim != null && dataReserva.isAfter(filtrosAvancados.dataFim))
                            return false;
                    } catch (Exception e) {
                        return false;
                    }
                }
            }
            return true;
        });
    }
}
