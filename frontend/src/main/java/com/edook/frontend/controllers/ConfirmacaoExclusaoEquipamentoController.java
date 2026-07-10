package com.edook.frontend.controllers;

import com.edook.frontend.models.EquipamentoResponseDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConfirmacaoExclusaoEquipamentoController {

    @FXML
    private Label lblPrefixo, lblNumero, lblDescricao, lblTipo;

    private EquipamentoResponseDTO equipamento;
    private Runnable onAtualizarTabela;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void setEquipamento(EquipamentoResponseDTO equipamento) {
        this.equipamento = equipamento;
        if (equipamento != null) {
            lblPrefixo.setText(equipamento.getPrefixo());
            lblNumero.setText(String.valueOf(equipamento.getNumero()));
            lblDescricao.setText(equipamento.getDescricao());
            lblTipo.setText(equipamento.getTipo());
        }
    }

    public void setOnAtualizarTabela(Runnable onAtualizarTabela) {
        this.onAtualizarTabela = onAtualizarTabela;
    }

    @FXML
    void onClickConfirmar(ActionEvent event) {
        if (equipamento == null) return;

        // Pega a referência da tela principal antes de fechar este pop-up atual
        Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage telaPrincipal = (Stage) stageAtual.getOwner();

        // Fecha o pop-up de confirmação imediatamente
        stageAtual.close();

        String url = "http://localhost:8080/equipamentos/" + equipamento.getPrefixo() + "/" + equipamento.getNumero();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 204) {
                            // Sucesso na Exclusão
                            if (onAtualizarTabela != null) {
                                onAtualizarTabela.run(); // Recarrega a tabela no background
                            }

                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/SucessoExclusaoEquipamento-view.fxml"));
                                Parent root = loader.load();

                                Stage popupStage = new Stage();
                                Parent rootPrincipal = telaPrincipal.getScene().getRoot();
                                rootPrincipal.setEffect(new GaussianBlur(15)); // Aplica blur novamente na main window

                                popupStage.initOwner(telaPrincipal);
                                popupStage.initModality(Modality.WINDOW_MODAL);
                                popupStage.initStyle(StageStyle.TRANSPARENT);

                                Scene scene = new Scene(root);
                                scene.setFill(Color.TRANSPARENT);
                                if (getClass().getResource("/com/edook/frontend/style.css") != null) {
                                    scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());
                                }

                                popupStage.setScene(scene);
                                popupStage.centerOnScreen();

                                popupStage.showAndWait();
                                rootPrincipal.setEffect(null); // Remove o blur ao finalizar
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else if (response.statusCode() == 400) {
                            // Erro de Regra de Negócio (Equipamento em uso)
                            abrirPopupErro(telaPrincipal, "Exclusão Inválida", "Não é possível excluir um equipamento com reservas pendentes.");

                        } else {
                            // Outros Erros (500, 404, etc)
                            abrirPopupErro(telaPrincipal, "Erro na Exclusão", "Ocorreu um erro inesperado ao tentar excluir o equipamento.");
                        }
                    });
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        abrirPopupErro(telaPrincipal, "Erro de Conexão", "Não foi possível conectar ao servidor.");
                    });
                    return null;
                });
    }

    @FXML
    void onClickCancelar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void abrirPopupErro(Stage telaPrincipal, String titulo, String descricao) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/OperacaoInvalida-view.fxml"));
            Parent root = loader.load();

            OperacaoInvalidaController popupController = loader.getController();
            popupController.setMensagem(titulo, descricao);

            Stage popupStage = new Stage();
            Parent rootPrincipal = telaPrincipal.getScene().getRoot();
            rootPrincipal.setEffect(new GaussianBlur(15));

            popupStage.initOwner(telaPrincipal);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            if (getClass().getResource("/com/edook/frontend/style.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());
            }

            popupStage.setScene(scene);
            popupStage.centerOnScreen();

            popupStage.showAndWait();
            rootPrincipal.setEffect(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}