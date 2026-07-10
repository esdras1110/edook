package com.edook.frontend.controllers;

import com.edook.frontend.models.EquipamentoResponseDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class EdicaoEquipamentoController {

    @FXML private TextField txtDesc;
    @FXML private Label lblErro;

    private EquipamentoResponseDTO equipamento;
    private Runnable onEdicaoSucesso; // Para atualizar a tabela no CadastroController

    public void setEquipamento(EquipamentoResponseDTO equipamento) {
        this.equipamento = equipamento;
        if (equipamento != null) {
            txtDesc.setText(equipamento.getDescricao());
        }
    }

    public void setOnEdicaoSucesso(Runnable onEdicaoSucesso) {
        this.onEdicaoSucesso = onEdicaoSucesso;
    }

    @FXML
    private void onClickAtualizar(ActionEvent event) {
        String novaDesc = txtDesc.getText().trim();

        if (novaDesc.isEmpty()) {
            lblErro.setText("A descrição não pode estar vazia!");
            lblErro.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/ConfirmacaoEdicaoEquipamento-view.fxml"));
            Parent root = loader.load();

            ConfirmacaoEdicaoEquipamentoController controller = loader.getController();
            controller.setData(equipamento.getDescricao(), novaDesc);
            controller.setOnConfirmar(() -> enviarEdicao(novaDesc, event));

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            // Aplica Blur nesta janela antes de abrir a Confirmação
            Stage donoDaJanela = (Stage) txtDesc.getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();
            rootPrincipal.setEffect(new GaussianBlur(15));

            popupStage.initOwner(donoDaJanela);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.centerOnScreen();

            // Pausa a execução aqui até a pessoa confirmar ou cancelar
            popupStage.showAndWait();

            // Remove o Blur garantido!
            rootPrincipal.setEffect(null);

        } catch (Exception e) {
            e.printStackTrace();
            lblErro.setText("Erro ao abrir a confirmação.");
        }
    }

    private void enviarEdicao(String novaDescricao, ActionEvent originalEvent) {
        CompletableFuture.runAsync(() -> {
            try {
                String descEscapada = novaDescricao.replace("\"", "\\\"");
                String tipoEscapado = equipamento.getTipo().replace("\"", "\\\"");
                String jsonBody = String.format("{\"descricao\":\"%s\",\"tipo\":\"%s\"}", descEscapada, tipoEscapado);

                String url = String.format("http://localhost:8080/equipamentos/%s/%d", equipamento.getPrefixo(), equipamento.getNumero());

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 200 || response.statusCode() == 204) {
                        abrirTelaSucesso();
                    } else {
                        lblErro.setText("Erro ao editar: " + response.statusCode());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> lblErro.setText("Falha na conexão."));
            }
        });
    }

    private void abrirTelaSucesso() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/SucessoEdicaoEquipamento-view.fxml"));
            Parent root = loader.load();

            SucessoEdicaoEquipamentoController controller = loader.getController();
            controller.setOnFinalizar(() -> {
                if (onEdicaoSucesso != null) onEdicaoSucesso.run(); // Atualiza a tabela na tela de trás
                Stage stage = (Stage) txtDesc.getScene().getWindow();
                stage.close(); // Fecha a tela de edição
            });

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Stage donoDaJanela = (Stage) txtDesc.getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();
            rootPrincipal.setEffect(new GaussianBlur(15));

            popupStage.initOwner(donoDaJanela);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.centerOnScreen();

            popupStage.showAndWait();

            rootPrincipal.setEffect(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickCancelar(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}