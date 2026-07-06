package com.edook.frontend.controllers;

import com.edook.frontend.components.ItemReservaController;
import com.edook.frontend.models.ReservaResponseDTO;
import com.edook.frontend.session.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ConfirmacaoCancelamentoReservaController {

    @FXML
    private VBox vboxListaReservas;

    private List<ReservaResponseDTO> reservasParaCancelar;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void setReservas(List<ReservaResponseDTO> reservas) {
        this.reservasParaCancelar = reservas;
        vboxListaReservas.getChildren().clear();

        for (ReservaResponseDTO reserva : reservas) {
            try {
                // Carrega o FXML do componente que acabamos de criar
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/ItemReserva-view.fxml"));
                HBox itemReserva = loader.load();

                // Pega o controller do componente e passa os dados
                ItemReservaController controller = loader.getController();
                controller.setDadosReserva(reserva);

                // Adiciona o componente visualizado na lista do Pop-up
                vboxListaReservas.getChildren().add(itemReserva);

            } catch (IOException e) {
                System.err.println("Erro ao carregar o item da reserva: " + reserva.getId());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void onClickConfirmar(ActionEvent event) {
        String cpfUsuario = UserSession.getInstance().getCpf();

        Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage telaPrincipal = (Stage) stageAtual.getOwner();
        stageAtual.close();

        List<ReservaResponseDTO> reservasNegadas = java.util.Collections.synchronizedList(new ArrayList<>());
        List<java.util.concurrent.CompletableFuture<Void>> futures = new ArrayList<>();

        for (ReservaResponseDTO reserva : reservasParaCancelar) {
            String url = "http://localhost:8080/reservas/" + reserva.getId() + "/cancelar?cpf=" + cpfUsuario;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build();

            java.util.concurrent.CompletableFuture<Void> future = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 204 || response.statusCode() == 200) {
                            System.out.println("Reserva " + reserva.getId() + " cancelada com sucesso.");
                        } else {
                            System.err.println("Erro ao cancelar ID " + reserva.getId() + " - Status: " + response.statusCode());
                            reservasNegadas.add(reserva);
                        }
                    })
                    .exceptionally(e -> {
                        System.err.println("Erro de conexão ao tentar cancelar a reserva " + reserva.getId());
                        e.printStackTrace();
                        reservasNegadas.add(reserva);
                        return null;
                    });

            futures.add(future);
        }

        java.util.concurrent.CompletableFuture.allOf(futures.toArray(new java.util.concurrent.CompletableFuture[0]))
                .thenRun(() -> {
                    Platform.runLater(() -> {

                        if (!reservasNegadas.isEmpty()) {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/OperacaoInvalida-view.fxml"));
                                Parent root = loader.load();

                                OperacaoInvalidaController popupController = loader.getController();

                                if (reservasNegadas.size() == 1) {
                                    popupController.setMensagem(
                                            "Cancelamento Inválido",
                                            "Você não tem permissão para cancelar a reserva selecionada porque ela pertence a outro docente ou já foi concluída."
                                    );
                                } else {
                                    popupController.setMensagem(
                                            "Cancelamento Inválido",
                                            "Algumas das reservas selecionadas pertencem a outros docentes ou já foram concluídas e não puderam ser canceladas."
                                    );
                                }

                                Stage popupStage = new Stage();

                                Parent rootPrincipal = telaPrincipal.getScene().getRoot();
                                javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(15);
                                rootPrincipal.setEffect(blur);

                                popupStage.initOwner(telaPrincipal);
                                popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                                popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

                                Scene scene = new Scene(root);
                                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                                scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());

                                popupStage.setScene(scene);
                                popupStage.centerOnScreen();

                                popupStage.showAndWait();
                                rootPrincipal.setEffect(null);

                            } catch (IOException e) {
                                System.err.println("Erro ao abrir o pop-up de erro de permissão.");
                                e.printStackTrace();
                            }

                        } else {
                            // BLOCO DE SUCESSO TOTAL
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/SucessoCancelamentoReserva-view.fxml"));
                                Parent root = loader.load();

                                Stage popupStage = new Stage();

                                Parent rootPrincipal = telaPrincipal.getScene().getRoot();
                                javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(15);
                                rootPrincipal.setEffect(blur);

                                popupStage.initOwner(telaPrincipal);
                                popupStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
                                popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

                                Scene scene = new Scene(root);
                                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                                scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());

                                popupStage.setScene(scene);
                                popupStage.centerOnScreen();

                                popupStage.showAndWait();
                                rootPrincipal.setEffect(null);

                            } catch (IOException e) {
                                System.err.println("Erro ao abrir o pop-up de sucesso do cancelamento.");
                                e.printStackTrace();
                            }
                        }
                    });
                });
    }
}