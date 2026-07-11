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
                                // 1. Obtém a janela (Stage) que já está aberta na tela
                                Stage stageAtual = (Stage) campoSenhaRedefinicao.getScene().getWindow();

                                // 2. Carrega o layout de sucesso (SucessoRedefinicaoSenha-view.fxml)
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/SucessoRedefinicaoSenha-view.fxml"));
                                Parent root = loader.load();

                                // 3. Monta a nova cena
                                Scene scene = new Scene(root);
                                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

                                if (getClass().getResource("/com/edook/frontend/style.css") != null) {
                                    scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());
                                }

                                // 4. Substitui o conteúdo da janela de forma segura, sem abrir outra por cima
                                stageAtual.setScene(scene);
                                stageAtual.centerOnScreen();

                                // Nota: O efeito de Blur (Desfocado) na tela principal por trás continuará ativo
                                // porque não fechamos a janela dona do efeito ainda!

                            } catch (IOException e) {
                                System.err.println("Erro ao transicionar para a tela de sucesso da redefinição.");
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
