package com.edook.frontend.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CodigoValidacaoEmailController {
    @FXML
    private TextField campoCodigo;

    private String emailValidacao;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void setEmailValidacao(String email) {
        this.emailValidacao = email;
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void onClickValidar(ActionEvent event) {
        String codigo = campoCodigo.getText().trim();
        if (codigo.isEmpty()) return;

        Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage telaPrincipal = (Stage) stageAtual.getOwner();

        // 1. Monta o JSON esperado pelo CodigoVerificacaoDto no Backend
        String jsonBody = String.format("{\"email\": \"%s\", \"codigo\": \"%s\"}", emailValidacao, codigo);

        // 2. Chama a rota PUT correta do seu Controller
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/funcionarios/confirmar-email"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        // Se o back retornar OK (200), o e-mail foi verificado com sucesso
                        if (response.statusCode() == 200) {
                            stageAtual.close();
                            abrirPopupSucesso(telaPrincipal);
                        } else {
                            System.err.println("Erro na validação. Status: " + response.statusCode() + " Resposta: " + response.body());
                            // Opcional: exibir na interface que o código está inválido ou expirado
                        }
                    });
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    private void abrirPopupSucesso(Stage telaPrincipal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/SucessoCadastroUsuario-view.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
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
            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}