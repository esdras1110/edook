package com.edook.frontend.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
import java.util.Random;

public class CodigoValidacaoEmailController {
    @FXML
    private TextField campoCodigo;

    @FXML
    private Label lblErro;

    private String emailValidacao;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private Runnable onSucesso;

    public void setOnSucesso(Runnable onSucesso) {
        this.onSucesso = onSucesso;
    }

    public void setEmailValidacao(String email) {
        this.emailValidacao = email;
    }

    @FXML
    void onClickValidar(ActionEvent event) {
        String codigo = campoCodigo.getText().trim();
        if (codigo.isEmpty()) return;

        Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage telaPrincipal = (Stage) stageAtual.getOwner();
        stageAtual.setOpacity(0);

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
                            if (onSucesso != null) {
                                onSucesso.run();
                            }
                            abrirPopupSucesso(telaPrincipal);
                            stageAtual.close();
                        } else {
                            stageAtual.setOpacity(1);
                            lblErro.setText("Código Inválido.");
                            lblErro.setTextFill(Color.RED);
                            System.err.println("Erro na validação. Status: " + response.statusCode() + " Resposta: " + response.body());
                            // Opcional: exibir na interface que o código está inválido ou expirado
                        }
                    });
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    Platform.runLater(() -> stageAtual.setOpacity(1));
                    return null;
                });
    }

    @FXML
    void onClickReenviarCodigo(ActionEvent event) {
        String novoCodigo = String.format("%04d", new Random().nextInt(10000));

        // 3. Monta o JSON esperado pelo CodigoVerificacaoDto no Backend
        String jsonBody = String.format("{\"email\": \"%s\", \"codigo\": \"%s\"}", emailValidacao, novoCodigo);

        // 4. Monta a requisição POST apontando para a rota correta do seu Controller
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/funcionarios/reenviar-confirmacao"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // 5. Dispara a requisição assíncrona para não travar a interface
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            lblErro.setText("Um novo código foi enviado para o seu e-mail.");
                            lblErro.setTextFill(Color.GREEN);
                        } else {
                            System.err.println("Erro ao reenviar. Status: " + response.statusCode() + " | Body: " + response.body());

                            lblErro.setText("Erro ao reenviar o código.");
                            lblErro.setTextFill(Color.RED);
                        }
                    });
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        System.err.println("Falha de conexão ao reenviar código: " + e.getMessage());
                    });
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

            // 4. MUDANÇA IMPORTANTE: Usa showAndWait() em vez de show()
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}