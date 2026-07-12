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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Controlador do modal de Redefinição de Senha, para usuários já logados
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

        // Validação básica
        if (novaSenha.isEmpty() || !novaSenha.equals(confirmacao)) {
            lblErro.setText("As senhas não coincidem ou estão vazias.");
            lblErro.setStyle("-fx-text-fill: red;");
            return;
        }

        // Em vez de pedir o e-mail para o usuário, o sistema recupera automaticamente o e-mail da sessão global ativa
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
                                Stage stageAtual = (Stage) campoSenhaRedefinicao.getScene().getWindow();

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/SucessoRedefinicaoSenha-view.fxml"));
                                Parent root = loader.load();

                                Scene scene = new Scene(root);
                                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

                                if (getClass().getResource("/com/edook/frontend/style.css") != null) {
                                    scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());
                                }

                                stageAtual.setScene(scene);
                                stageAtual.centerOnScreen();
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
