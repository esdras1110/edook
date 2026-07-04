package com.edook.frontend.controllers;

import com.edook.frontend.models.FiltroReservaDTO;
import com.edook.frontend.models.ReservaResponseDTO;
import com.edook.frontend.session.UserSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ResourceBundle;

public class ReservasController implements Initializable, Filtravel {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @FXML
    private TextField campoBusca;

    @FXML
    private TableView<ReservaResponseDTO> tabelaReservas;

    @FXML
    private TableColumn<ReservaResponseDTO, String> colFuncionario;

    @FXML
    private TableColumn<ReservaResponseDTO, String> colTitulo;

    @FXML
    private TableColumn<ReservaResponseDTO, String> colData;

    @FXML
    private TableColumn<ReservaResponseDTO, String> colHorario;

    @FXML
    private TableColumn<ReservaResponseDTO, String> colEquipamento;

    @FXML
    private TableColumn<ReservaResponseDTO, String> colLocal;

    @FXML
    private TableColumn<ReservaResponseDTO, String> colStatus;

    private final ObservableList<ReservaResponseDTO> listaReservas = FXCollections.observableArrayList();
    private FilteredList<ReservaResponseDTO> listaFiltrada;

    @FXML
    private Button btnMinhasReservas;

    private boolean filtrarApenasMinhas = false;
    private FiltroReservaDTO filtrosAvancados = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabelaReservas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        colFuncionario.setCellValueFactory(new PropertyValueFactory<>("cpfFuncionario"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataFormatada"));
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horarioFormatado"));
        colEquipamento.setCellValueFactory(new PropertyValueFactory<>("equipamentosFormatados"));
        colLocal.setCellValueFactory(new PropertyValueFactory<>("localidade"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        listaFiltrada = new FilteredList<>(listaReservas, p -> true);
        tabelaReservas.setItems(listaFiltrada);

        campoBusca.textProperty().addListener((observable, oldValue, newValue) -> {
            atualizarFiltros();
        });

        buscarReservas();
    }

    private void buscarReservas() {
        String url = "http://localhost:8080/reservas"; // URL do seu ReservaController no Back-end

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            String json = response.body();

                            // O Jackson converte o JSON do Back para uma lista de ReservaResponseDto
                            List<ReservaResponseDTO> todasReservas = objectMapper.readValue(
                                    json,
                                    new TypeReference<List<ReservaResponseDTO>>() {}
                            );

                            Platform.runLater(() -> {
                                listaReservas.setAll(todasReservas);

                                String cpfUsuarioLogado = UserSession.getInstance().getCpf();
                                if (filtrarApenasMinhas) {
                                    List<ReservaResponseDTO> minhasReservas = todasReservas.stream()
                                            .filter(r -> r.getCpfFuncionario() != null &&
                                                    r.getCpfFuncionario().equals(cpfUsuarioLogado))
                                            .toList();

                                    listaReservas.setAll(minhasReservas);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                            System.err.println("Erro ao converter o JSON de reservas.");
                        }
                    } else {
                        System.err.println("Erro no servidor. Status: " + response.statusCode());
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    System.err.println("Falha na conexão com o servidor.");
                    return null;
                });
    }

    public void setFiltrosAvancados(FiltroReservaDTO filtros) {
        this.filtrosAvancados = filtros;
        atualizarFiltros();
    }

    private void atualizarFiltros() {
        String textoBusca = campoBusca.getText() == null ? "" : campoBusca.getText().toLowerCase();

        listaFiltrada.setPredicate(reserva -> {
            // 1. Regra da Barra de Pesquisa
            boolean passaBusca = true;
            if (!textoBusca.isEmpty()) {
                passaBusca = (reserva.getNome() != null && reserva.getNome().toLowerCase().contains(textoBusca)) ||
                        (reserva.getEquipamentosFormatados() != null && reserva.getEquipamentosFormatados().toLowerCase().contains(textoBusca)) ||
                        (reserva.getLocalidade() != null && reserva.getLocalidade().toLowerCase().contains(textoBusca)) ||
                        (reserva.getStatus() != null && reserva.getStatus().toLowerCase().contains(textoBusca));
            }

            // Se já reprovou na busca de texto, não precisa nem checar o avançado
            if (!passaBusca) return false;

            // 2. Regras do Filtro Avançado (Pop-up)
            if (filtrosAvancados != null) {
                if (filtrosAvancados.local != null && !filtrosAvancados.local.isEmpty()) {
                    if (reserva.getLocalidade() == null || !reserva.getLocalidade().equalsIgnoreCase(filtrosAvancados.local)) return false;
                }
                if (filtrosAvancados.status != null && !filtrosAvancados.status.isEmpty()) {
                    if (reserva.getStatus() == null || !reserva.getStatus().equalsIgnoreCase(filtrosAvancados.status)) return false;
                }
                if (filtrosAvancados.equipamento != null && !filtrosAvancados.equipamento.isEmpty()) {
                    if (reserva.getEquipamentos() == null) return false;

                    String filtroEquipamento = filtrosAvancados.equipamento.toLowerCase();

                    boolean contemEquipamento = reserva.getEquipamentos().stream()
                            .anyMatch(e -> e.getTipo() != null && e.getTipo().toLowerCase().contains(filtroEquipamento));

                    if (!contemEquipamento) return false;
                }
                if (reserva.getDia() != null) {
                    if (filtrosAvancados.dataInicio != null && reserva.getDia().isBefore(filtrosAvancados.dataInicio)) return false;
                    if (filtrosAvancados.dataFim != null && reserva.getDia().isAfter(filtrosAvancados.dataFim)) return false;
                }
            }

            // Se passou pelos dois testes, a reserva aparece na tabela!
            return true;
        });
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

        atualizarFiltros();
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
}
