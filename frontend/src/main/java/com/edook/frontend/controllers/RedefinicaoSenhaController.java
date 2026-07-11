package com.edook.frontend.controllers;

import com.edook.frontend.session.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RedefinicaoSenhaController {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    private PasswordField campoSenhaRedefinicao, campoConfirmacaoSenhaRedefinicao;

    @FXML
    private Label lblErro;

    @FXML
    private void onClickRedefinirSenha(ActionEvent event) {
        String novaSenha = campoSenhaRedefinicao.getText();
        String confirmacao = campoConfirmacaoSenhaRedefinicao.getText();

        if (novaSenha.isEmpty() || !novaSenha.equals(confirmacao)) {
            lblErro.setText("As senhas não coincidem ou estão vazias.");
            lblErro.setStyle("-fx-text-fill: red;");
            return;
        }

        String emailLogado = UserSession.getInstance().getEmail();
        String jsonBody = String.format("{\"email\": \"%s\", \"novaSenha\": \"%s\"}", emailLogado, novaSenha);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/funcionarios/redefinir-senha"))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            try {
                                // Fecha o pop-up atual de inserção da nova senha
                                Stage stageAtual = (Stage) campoSenhaRedefinicao.getScene().getWindow();
                                stageAtual.close();

                                // Abre o pop-up final: SucessoRedefinicaoSenha-view.fxml
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/SucessoRedefinicaoSenha-view.fxml"));
                                Parent root = loader.load();

                                Stage popupStage = new Stage();
                                popupStage.initModality(Modality.APPLICATION_MODAL);
                                popupStage.initStyle(StageStyle.TRANSPARENT);

                                Stage donoDaJanela = (Stage) stageAtual.getOwner();
                                Parent rootPrincipal = donoDaJanela.getScene().getRoot();

                                GaussianBlur blur = new GaussianBlur(15);
                                rootPrincipal.setEffect(blur);

                                popupStage.initOwner(donoDaJanela);

                                Scene scene = new Scene(root);
                                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

                                if (getClass().getResource("/com/edook/frontend/style.css") != null) {
                                    scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());
                                }

                                popupStage.setScene(scene);
                                popupStage.centerOnScreen();
                                popupStage.showAndWait();

                                rootPrincipal.setEffect(null);

                            } catch (IOException e) {
                                System.err.println("Erro ao abrir pop-up de sucesso da redefinição.");
                                e.printStackTrace();
                            }
                        } else {
                            lblErro.setText("Erro ao redefinir a senha.");
                            lblErro.setStyle("-fx-text-fill: red;");
                        }
                    });
                });
    }
}
