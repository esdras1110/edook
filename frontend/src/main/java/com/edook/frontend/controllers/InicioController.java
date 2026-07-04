package com.edook.frontend.controllers;

import com.edook.frontend.components.CelulasLembretes;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ResourceBundle;

public class InicioController implements Initializable, Filtravel {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @FXML
    private TextField campoBusca;

    @FXML
    private TableView<ReservaResponseDTO> tabelaInicio;

    @FXML
    private TableColumn<ReservaResponseDTO, String> colData, colHorario, colEquipamento, colLocal, colStatus;

    @FXML
    private ListView<ReservaResponseDTO> listaLembretes;

    private final ObservableList<ReservaResponseDTO> listaReservas = FXCollections.observableArrayList();
    private FilteredList<ReservaResponseDTO> listaFiltrada;
    private final ObservableList<ReservaResponseDTO> listaLembretesUsuario = FXCollections.observableArrayList();

    private FiltroReservaDTO filtrosAvancados = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabelaInicio.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        colData.setCellValueFactory(new PropertyValueFactory<>("dataFormatada"));
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horarioFormatado"));
        colEquipamento.setCellValueFactory(new PropertyValueFactory<>("equipamentosFormatados"));
        colLocal.setCellValueFactory(new PropertyValueFactory<>("localidade"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        listaFiltrada = new FilteredList<>(listaReservas, p -> true);
        tabelaInicio.setItems(listaFiltrada);

        campoBusca.textProperty().addListener((observable, oldValue, newValue) -> {
            atualizarFiltros();
        });

        listaLembretes.setCellFactory(lv -> new CelulasLembretes());
        listaLembretes.setItems(listaLembretesUsuario);

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
                                if (cpfUsuarioLogado != null) {
                                    List<ReservaResponseDTO> lembretesDoUsuario = todasReservas.stream()
                                            .filter(r -> r.getCpfFuncionario() != null &&
                                                    r.getCpfFuncionario().equals(cpfUsuarioLogado))
                                            .toList();

                                    listaLembretesUsuario.setAll(lembretesDoUsuario);
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

    @Override
    public void setFiltrosAvancados(FiltroReservaDTO filtros) {
        this.filtrosAvancados = filtros;
        atualizarFiltros(); // Chama a unificação
    }

    private void atualizarFiltros() {
        String textoBusca = campoBusca.getText() == null ? "" : campoBusca.getText().toLowerCase();

        listaFiltrada.setPredicate(reserva -> {
            // 1. Regra da Barra de Pesquisa
            boolean passaBusca = true;
            if (!textoBusca.isEmpty()) {
                passaBusca = (reserva.getEquipamentosFormatados() != null && reserva.getEquipamentosFormatados().toLowerCase().contains(textoBusca)) ||
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
    void onClickFiltros(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/FiltroReservas-view.fxml"));
            Parent root = loader.load();

            FiltroReservasController popupController = loader.getController();
            popupController.setTelaPai(this);

            if (this.filtrosAvancados != null) {
                popupController.carregarFiltrosSalvos(this.filtrosAvancados);
            }

            Stage popupStage = new Stage();
            Stage donoDaJanela = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();

            popupStage.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    popupStage.close();
                }
            });

            javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(15);
            rootPrincipal.setEffect(blur);

            popupStage.initOwner(donoDaJanela);
            popupStage.initModality(javafx.stage.Modality.NONE);
            popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());

            popupStage.setScene(scene);
            popupStage.centerOnScreen();
            popupStage.showAndWait();

            rootPrincipal.setEffect(null);

        } catch (java.io.IOException e) {
            System.err.println("Erro ao abrir o pop-up de filtros.");
            e.printStackTrace();
        }
    }

    @FXML
    void onClickAdicionarReserva(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/CadastroReserva-view.fxml"));
            Parent root = loader.load();

            CadastroReservaController popupController = loader.getController();

            popupController.setOnCadastroSucesso(() -> {
                buscarReservas();
            });

            Stage popupStage = new Stage();
            Stage donoDaJanela = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();

            javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(15);
            rootPrincipal.setEffect(blur);

            popupStage.initOwner(donoDaJanela);
            popupStage.initModality(javafx.stage.Modality.NONE);
            popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());

            popupStage.setScene(scene);
            popupStage.centerOnScreen();
            popupStage.showAndWait();

            rootPrincipal.setEffect(null);

        } catch (java.io.IOException e) {
            System.err.println("Erro ao abrir o pop-up de Adicionar Reserva.");
            e.printStackTrace();
        }
    }
}
