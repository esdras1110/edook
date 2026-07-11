package com.edook.frontend.controllers;

import com.edook.frontend.session.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.ResourceBundle;

public class CodigoValidacaoRedefinicaoSenhaController implements Initializable {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    private TextField campoCodigo;

    @FXML
    private Label lblErro;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        enviarCodigo();
    }

    private void enviarCodigo() {
        String novoCodigo = String.format("%04d", new Random().nextInt(10000));
        String emailLogado = UserSession.getInstance().getEmail();

        // 3. Monta o JSON esperado pelo CodigoVerificacaoDto no Backend
        String jsonBody = String.format("{\"email\": \"%s\", \"codigo\": \"%s\"}", emailLogado, novoCodigo);

        // 4. Monta a requisição POST apontando para a rota correta do seu Controller
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/funcionarios/enviar-codigo"))
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

    @FXML
    private void onClickVerificar(ActionEvent event) {
        String codigo = campoCodigo.getText().trim();

        if (codigo.isEmpty()) {
            lblErro.setText("O código não pode estar vazio.");
            lblErro.setStyle("-fx-text-fill: red;");
            return;
        }

        String emailLogado = UserSession.getInstance().getEmail();
        String jsonBody = String.format("{\"email\": \"%s\", \"codigo\": \"%s\"}", emailLogado, codigo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/funcionarios/verificar-codigo"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            // Captura o Stage atual e fecha
                            Stage stageAtual = (Stage) campoCodigo.getScene().getWindow();
                            stageAtual.close();

                            // Abre o próximo pop-up: RedefinicaoSenha-view.fxml
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/RedefinicaoSenha-view.fxml"));
                                Parent root = loader.load();

                                Stage popupStage = new Stage();
                                popupStage.initModality(Modality.APPLICATION_MODAL);
                                popupStage.initStyle(StageStyle.TRANSPARENT);

                                // Recupera o dono da janela anterior para aplicar o efeito Blur novamente
                                Stage donoDaJanela = (Stage) stageAtual.getOwner();
                                Parent rootPrincipal = donoDaJanela.getScene().getRoot();

                                GaussianBlur blur = new GaussianBlur(15);
                                rootPrincipal.setEffect(blur);

                                popupStage.initOwner(donoDaJanela);

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
                                System.err.println("Erro ao abrir pop-up de redefinição de senha.");
                                e.printStackTrace();
                            }
                        } else {
                            lblErro.setText("Código inválido ou expirado.");
                            lblErro.setStyle("-fx-text-fill: red;");
                        }
                    });
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        lblErro.setText("Falha de conexão com o servidor.");
                        lblErro.setStyle("-fx-text-fill: red;");
                    });
                    return null;
                });
    }

    @FXML
    private void onClickVoltar(ActionEvent event) {
        Stage stage = (Stage) campoCodigo.getScene().getWindow();
        stage.close(); // Ao fechar, o showAndWait do pai encerra e limpa o Blur automaticamente
    }

    @FXML
    private void onClickReenviarCodigo(ActionEvent event) {
        enviarCodigo();
    }
}
