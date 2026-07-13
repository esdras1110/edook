package com.edook.frontend.controllers;

import com.edook.frontend.components.LembreteController;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

// Controlador da tela de Início, semelhante ao controlador de outras telas, como CadastroController
public class InicioController implements Initializable, Filtravel {
    // Cliente HTTP reaproveitado
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    // Registro do JavaTimeModule no ObjectMapper, para que a biblioteca Jackson consiga converter atributos LocalDate/LocalTime
    // do JSON para as classes do Java 8+.
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

    // Objeto que guardará os filtros recebidos do popup
    private FiltroReservaDTO filtrosAvancados = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Permite que o usuário selecione várias linhas da tabela ao mesmo tempo
        tabelaInicio.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Configuração das colunas da tabela referenciando o ReservaResponseDTO
        colData.setCellValueFactory(new PropertyValueFactory<>("dataFormatada"));
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horarioFormatado"));
        colEquipamento.setCellValueFactory(new PropertyValueFactory<>("equipamentosFormatados"));
        colLocal.setCellValueFactory(new PropertyValueFactory<>("localidade"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Lógica de tooltip para coluna de equipamentos
        colEquipamento.setCellFactory(tc -> {
            return new TableCell<ReservaResponseDTO, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setTooltip(null);
                    } else {
                        setText(item);
                        Tooltip tooltip = new Tooltip(item);
                        tooltip.setStyle("-fx-font-size: 13px; -fx-background-color: #1E1E1E; -fx-text-fill: #F9FAFB;");
                        tooltip.setWrapText(true);
                        tooltip.setPrefWidth(360);
                        setTooltip(tooltip);
                    }
                }
            };
        });

        listaFiltrada = new FilteredList<>(listaReservas, p -> true);
        tabelaInicio.setItems(listaFiltrada);

        campoBusca.textProperty().addListener((observable, oldValue, newValue) -> {
            atualizarFiltros();
        });

        // Aplicação do LembreteController, células customizadas
        listaLembretes.setCellFactory(lv -> new LembreteController());
        listaLembretes.setItems(listaLembretesUsuario);

        buscarReservas();
    }

    // Requisita todas as reservas ao servidor de forma assíncrona
    private void buscarReservas() {
        // Lógica padrão de requisição GET
        String url = "http://localhost:8080/reservas";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            String json = response.body();
                            List<ReservaResponseDTO> todasReservas = objectMapper.readValue(
                                    json,
                                    new TypeReference<List<ReservaResponseDTO>>() {}
                            );

                            Platform.runLater(() -> {
                                listaReservas.setAll(todasReservas);

                                // Uso do StreamAPI para filtrar dados. Pegar apenas reservas do cpf logado e que não estejam
                                // concluídas ou canceladas para preencher a lista de lembretes.
                                String cpfUsuarioLogado = UserSession.getInstance().getCpf();
                                if (cpfUsuarioLogado != null) {
                                    List<ReservaResponseDTO> lembretesDoUsuario = todasReservas.stream()
                                            .filter(r -> r.getCpfFuncionario() != null &&
                                                    r.getCpfFuncionario().equals(cpfUsuarioLogado) &&
                                                    !r.getStatus().equalsIgnoreCase("Cancelada") &&
                                                    !r.getStatus().equalsIgnoreCase("Concluída"))
                                            .toList();

                                    // Alimenta a lista lateral de lembretes
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

    // Implementação obrigatória da interface Filtravel
    // Recebe os filtros escolhidos na tela de popup e aciona a atualização da tabela
    @Override
    public void setFiltrosAvancados(FiltroReservaDTO filtros) {
        this.filtrosAvancados = filtros;
        atualizarFiltros();
    }

    // Lógica combinada de filtragem, une a pesquisa do campo de busca com os filtros avançados do FiltroReservaDTO.
    private void atualizarFiltros() {
        String textoBusca = campoBusca.getText() == null ? "" : campoBusca.getText().toLowerCase();

        listaFiltrada.setPredicate(reserva -> {
            // Passa pelo filtro de texto simples
            boolean passaBusca = true;
            if (!textoBusca.isEmpty()) {
                passaBusca = (reserva.getEquipamentosFormatados() != null && reserva.getEquipamentosFormatados().toLowerCase().contains(textoBusca)) ||
                        (reserva.getLocalidade() != null && reserva.getLocalidade().toLowerCase().contains(textoBusca)) ||
                        (reserva.getStatus() != null && reserva.getStatus().toLowerCase().contains(textoBusca));
            }

            if (!passaBusca) return false;

            // Passa pelos filtros avançados, caso existam
            if (filtrosAvancados != null) {
                // Filtro por local
                if (filtrosAvancados.local != null && !filtrosAvancados.local.isEmpty()) {
                    if (reserva.getLocalidade() == null || !reserva.getLocalidade().equalsIgnoreCase(filtrosAvancados.local)) return false;
                }
                // Filtro por status
                if (filtrosAvancados.status != null && !filtrosAvancados.status.isEmpty()) {
                    if (reserva.getStatus() == null || !reserva.getStatus().equalsIgnoreCase(filtrosAvancados.status)) return false;
                }
                // Filtro por equipamento
                if (filtrosAvancados.equipamento != null && !filtrosAvancados.equipamento.isEmpty()) {
                    if (reserva.getEquipamentos() == null) return false;

                    String filtroEquipamento = filtrosAvancados.equipamento.toLowerCase();

                    boolean contemEquipamento = reserva.getEquipamentos().stream()
                            .anyMatch(e -> e.getTipo() != null && e.getTipo().toLowerCase().contains(filtroEquipamento));

                    if (!contemEquipamento) return false;
                }
                // Filtro de limite de datas
                if (reserva.getDia() != null) {
                    if (filtrosAvancados.dataInicio != null && reserva.getDia().isBefore(filtrosAvancados.dataInicio)) return false;
                    if (filtrosAvancados.dataFim != null && reserva.getDia().isAfter(filtrosAvancados.dataFim)) return false;
                }
            }

            return true;
        });
    }

    // Abre a janela de Filtros Avançados.
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

            // Se o usuário clicar fora do popup, a janela de filtros se fecha sozinha.
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

    // Ações dos botões principais (Adicionar e Cancelar). Ambos seguem a lógica padrão já vista em CadastroController,
    // tabela de equipamentos: validar seleção, abrir popup com Blur no fundo e configurar callback para atualizar a tabela depois.
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

    @FXML
    void onClickCancelarReservas(ActionEvent event) {
        // Pega uma lista de itens selecionados e não apenas um.
        ObservableList<ReservaResponseDTO> selecionadas = tabelaInicio.getSelectionModel().getSelectedItems();

        if (selecionadas.isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/OperacaoInvalida-view.fxml"));
                Parent root = loader.load();

                OperacaoInvalidaController popupController = loader.getController();
                popupController.setMensagem(
                        "Cancelamento Inválido",
                        "Você deve selecionar pelo menos uma reserva para efetuar o cancelamento."
                );

                Stage popupStage = new Stage();
                Stage donoDaJanela = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                Parent rootPrincipal = donoDaJanela.getScene().getRoot();

                javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(15);
                rootPrincipal.setEffect(blur);

                popupStage.initOwner(donoDaJanela);
                popupStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
                popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

                Scene scene = new Scene(root);
                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());

                popupStage.setScene(scene);
                popupStage.centerOnScreen();

                popupStage.showAndWait();
                rootPrincipal.setEffect(null);

            } catch (java.io.IOException e) {
                System.err.println("Erro ao abrir o pop-up de Operação Inválida.");
                e.printStackTrace();
            }
            return;
        }

        // Converte a lista do JavaFX para uma lista normal e envia para a tela de confirmação de cancelamento
        List<ReservaResponseDTO> reservasParaCancelar = new ArrayList<>(selecionadas);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/ConfirmacaoCancelamentoReserva-view.fxml"));
            Parent root = loader.load();

            ConfirmacaoCancelamentoReservaController popupController = loader.getController();
            popupController.setReservas(reservasParaCancelar);
            popupController.setOnAtualizarTabela(() -> buscarReservas());

            Stage popupStage = new Stage();
            Stage donoDaJanela = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();

            javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(15);
            rootPrincipal.setEffect(blur);

            popupStage.initOwner(donoDaJanela);
            popupStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());

            popupStage.setScene(scene);
            popupStage.centerOnScreen();
            popupStage.showAndWait();

            rootPrincipal.setEffect(null);
        } catch (java.io.IOException e) {
            System.err.println("Erro ao abrir o pop-up de Confirmação.");
            e.printStackTrace();
        }
    }
}