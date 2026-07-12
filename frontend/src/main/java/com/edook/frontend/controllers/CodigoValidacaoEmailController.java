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

// Controlador do modal de Validação de E-mail.
public class CodigoValidacaoEmailController {
    @FXML
    private TextField campoCodigo;

    @FXML
    private Label lblErro;

    // Variável para guardar o e-mail que está sendo validado
    private String emailValidacao;
    // Cliente HTTP reutilizável para fazer as chamadas para a API
    private final HttpClient httpClient = HttpClient.newHttpClient();

    // Ação a ser executada quando a validação der certo, limpar os campos
    private Runnable onSucesso;

    public void setOnSucesso(Runnable onSucesso) {
        this.onSucesso = onSucesso;
    }

    public void setEmailValidacao(String email) {
        this.emailValidacao = email;
    }

    // Pega o código digitado, envia para o servidor verificar se está correto e decide se abre a tela de sucesso ou exibe um erro.
    @FXML
    void onClickValidar(ActionEvent event) {
        String codigo = campoCodigo.getText().trim();
        if (codigo.isEmpty()) return; // Não faz nada se o campo estiver vazio

        // Captura a janela atual e a janela principal que está por trás dela
        Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage telaPrincipal = (Stage) stageAtual.getOwner();

        // Deixa a janela atual invisível enquanto aguarda a resposta do servidor
        stageAtual.setOpacity(0);

        // Monta o corpo da requisição no formato JSON
        String jsonBody = String.format("{\"email\": \"%s\", \"codigo\": \"%s\"}", emailValidacao, codigo);

        // Configura a requisição do tipo PUT para confirmar o e-mail
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/funcionarios/confirmar-email"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // Envia a requisição de forma assíncrona para não travar a interface
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    // O Platform.runLater garante que a atualização da tela seja feita na thread principal do JavaFX
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) { // Sucesso
                            if (onSucesso != null) {
                                onSucesso.run();
                            }
                            abrirPopupSucesso(telaPrincipal); // Abre o Popup de sucesso
                            stageAtual.close(); // Fecha a tela de validação definitivamente
                        } else {
                            // Se der erro, a tela volta a aparecer e mostra o erro
                            stageAtual.setOpacity(1);
                            lblErro.setText("Código Inválido.");
                            lblErro.setTextFill(Color.RED);
                            System.err.println("Erro na validação. Status: " + response.statusCode() + " Resposta: " + response.body());
                        }
                    });
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    // Em caso de falha de conexão, garante que a tela volte a aparecer
                    Platform.runLater(() -> stageAtual.setOpacity(1));
                    return null;
                });
    }

    // Gera um novo código aleatório e pede para a API enviar novamente por e-mail.
    @FXML
    void onClickReenviarCodigo(ActionEvent event) {
        // Gera um novo código numérico de 4 dígitos
        String novoCodigo = String.format("%04d", new Random().nextInt(10000));

        String jsonBody = String.format("{\"email\": \"%s\", \"codigo\": \"%s\"}", emailValidacao, novoCodigo);

        // Configura a requisição do tipo POST para solicitar o reenvio
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/funcionarios/reenviar-confirmacao"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // Dispara a requisição assíncrona
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            // Dá um feedback visual positivo se o servidor aceitou o reenvio
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

    // Abre o modal avisando que o cadastro/validação foi um sucesso. Lógica muito semelhante a outras aberturas de popup
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

            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}