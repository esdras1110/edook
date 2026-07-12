package com.edook.frontend.controllers;

import com.edook.frontend.session.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.edook.frontend.models.LoginResponseDTO;
import javafx.stage.StageStyle;

import java.util.Random;

// Controlador principal da tela de Login
// Em vez de abrir várias janelas, alterna a visibilidade de painéis, como na tela de Cadastro
public class LoginController {
    // Cliente HTTP reutilizável
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    private TextField campoTextLogin, campoEmail, campoTextCodigo;

    @FXML
    private PasswordField campoSenhaLogin, campoSenhaRedefinicao, campoConfirmacaoSenhaRedefinicao;

    @FXML
    private Label labelErroLogin, labelErroEmail, labelErroCodigo, labelErroSenha;

    // Múltiplos painéis (VBox) sobrepostos na mesma interface.
    @FXML
    private VBox vboxLogin, vboxEsqueceuSenha, vboxCodigo, vboxNovaSenha;

    // Guarda o e-mail durante as várias etapas de redefinição da senha
    private String emailTemporario;

    // Função de autenticação e login
    @FXML
    private void onClickEntrar(ActionEvent event) {
        String emailCPF = campoTextLogin.getText().trim();
        String senha = campoSenhaLogin.getText().trim();

        labelErroLogin.setText("");

        // Validações básicas de campos
        if (emailCPF.isEmpty() || senha.isEmpty()) {
            labelErroLogin.setText("Por favor, preencha todos os campos obrigatórios.");
            labelErroLogin.setStyle("-fx-text-fill: red;");
            return;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        String cpfRegex = "^\\d{11}$";

        if (!emailCPF.matches(emailRegex) && !emailCPF.matches(cpfRegex)) {
            labelErroLogin.setText("Por favor, insira um e-mail válido ou CPF.");
            labelErroLogin.setStyle("-fx-text-fill: red;");
            return;
        }

        if (senha.length() < 6) {
            labelErroLogin.setText("A senha deve conter pelo menos 6 caracteres.");
            labelErroLogin.setStyle("-fx-text-fill: red;");
            return;
        }

        labelErroLogin.setText("Autenticando...");
        labelErroLogin.setStyle("-fx-text-fill: #1a56db;");

        String jsonBody = String.format("{\"identificador\":\"%s\",\"senha\":\"%s\"}", emailCPF, senha);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        // Requisição assíncrona padrão
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        String respostaJson = response.body();

                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            LoginResponseDTO dados = objectMapper.readValue(respostaJson, LoginResponseDTO.class);

                            // Guarda os dados do utilizador numa instância única global
                            UserSession session = UserSession.getInstance();
                            session.setNome(dados.getNome());
                            session.setEmail(dados.getEmail());
                            session.setCpf(dados.getCpf());
                            session.setCargo(dados.getCargo());
                            session.setToken(dados.getToken());

                            // Avança para a tela inicial do sistema, abrindo uma nova Scene
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/MainLayout-view.fxml"));
                                    Parent root = loader.load();
                                    Stage mainStage = new Stage();
                                    mainStage.setTitle("edook - Sistema de Reserva de Equipamentos");
                                    Scene scene = new Scene(root, 1440, 1024);
                                    scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());
                                    mainStage.setScene(scene);
                                    mainStage.show();

                                    Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                    loginStage.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    labelErroLogin.setText("Erro ao carregar o layout principal.");
                                    labelErroLogin.setStyle("-fx-text-fill: red;");
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                            Platform.runLater(() -> {
                                labelErroLogin.setText("Erro ao processar dados do servidor.");
                                labelErroLogin.setStyle("-fx-text-fill: red;");
                            });
                        }

                    } else if (response.statusCode() == 400) {
                        Platform.runLater(() -> {
                            labelErroLogin.setText("Por favor, confirme seu e-mail antes de realizar o login.");
                            labelErroLogin.setStyle("-fx-text-fill: red;");
                        });
                    } else if (response.statusCode() == 401 || response.statusCode() == 403 || response.statusCode() == 404) {
                        Platform.runLater(() -> {
                            labelErroLogin.setText("CPF/E-mail ou senha incorretos.");
                            labelErroLogin.setStyle("-fx-text-fill: red;");
                        });
                    } else {
                        Platform.runLater(() -> {
                            labelErroLogin.setText("Erro no servidor. Tente novamente mais tarde.");
                            labelErroLogin.setStyle("-fx-text-fill: red;");
                        });
                    }
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        labelErroLogin.setText("Não foi possível conectar ao servidor.");
                        labelErroLogin.setStyle("-fx-text-fill: red;");
                    });
                    return null;
                });
    }

    // Mudança de painel visível
    @FXML
    private void onClickEsqueceuSenha(ActionEvent event) {
        vboxLogin.setVisible(false);
        vboxLogin.setManaged(false);
        vboxEsqueceuSenha.setVisible(true);
        vboxEsqueceuSenha.setManaged(true);
    }

    // Solicita o envio do código para o e-mail fornecido
    @FXML
    private void onClickEnviar(ActionEvent event) {
        String email = campoEmail.getText().trim();
        labelErroEmail.setText("");

        if (email.isEmpty()) {
            labelErroEmail.setText("Por favor, preencha o campo obrigatório.");
            labelErroEmail.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!email.contains("@")) {
            labelErroEmail.setText("Por favor, insira um e-mail válido.");
            labelErroEmail.setStyle("-fx-text-fill: red;");
            return;
        }

        this.emailTemporario = email; // Guarda o e-mail na memória para a próxima etapa

        String codigo = String.format("%04d", new Random().nextInt(10000));
        String jsonBody = String.format("{\"email\": \"%s\", \"codigo\": \"%s\"}", email, codigo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/funcionarios/enviar-codigo"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            // Avança para o painel do código
                            vboxEsqueceuSenha.setVisible(false);
                            vboxEsqueceuSenha.setManaged(false);
                            vboxCodigo.setVisible(true);
                            vboxCodigo.setManaged(true);
                        } else {
                            labelErroEmail.setText("Erro ao enviar e-mail. Verifique os dados.");
                            labelErroEmail.setStyle("-fx-text-fill: red;");
                        }
                    });
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        labelErroEmail.setText("Erro de conexão com o servidor.");
                        labelErroEmail.setStyle("-fx-text-fill: red;");
                    });
                    return null;
                });
    }

    // Mudança de painel visível
    @FXML
    private void onClickVoltarLogin(ActionEvent event) {
        vboxEsqueceuSenha.setVisible(false);
        vboxEsqueceuSenha.setManaged(false);
        vboxLogin.setVisible(true);
        vboxLogin.setManaged(true);
    }

    // Valida se o código introduzido corresponde ao que foi enviado para o e-mail
    @FXML
    private void onClickVerificar(ActionEvent event) {
        String codigoUsuario = campoTextCodigo.getText().trim();
        labelErroCodigo.setText("");

        // Junta o código introduzido com o email guardado
        String jsonBody = String.format("{\"email\": \"%s\", \"codigo\": \"%s\"}", emailTemporario, codigoUsuario);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/funcionarios/verificar-codigo"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            // Avança para a Redefinição de Senha
                            vboxCodigo.setVisible(false);
                            vboxCodigo.setManaged(false);
                            vboxNovaSenha.setVisible(true);
                            vboxNovaSenha.setManaged(true);
                        } else {
                            labelErroCodigo.setText("Código Inválido.");
                            labelErroCodigo.setStyle("-fx-text-fill: red;");
                        }
                    });
                });
    }

    // Submete a nova palavra-passe
    @FXML
    private void onClickRedefinirSenha(ActionEvent event) {
        String novaSenha = campoSenhaRedefinicao.getText();
        String confirmacao = campoConfirmacaoSenhaRedefinicao.getText();

        // Validações para campos vazios e senhas iguais
        if (novaSenha.isEmpty() || !novaSenha.equals(confirmacao)) {
            labelErroSenha.setText("As senhas não coincidem ou estão vazias.");
            labelErroSenha.setStyle("-fx-text-fill: red;");
            return;
        }

        String jsonBody = String.format("{\"email\": \"%s\", \"novaSenha\": \"%s\"}", emailTemporario, novaSenha);

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
                                // Carrega modal de sucesso
                                Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                                GaussianBlur blur = new GaussianBlur(15);
                                loginStage.getScene().getRoot().setEffect(blur);

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/SucessoEsqueceuSenha-view.fxml"));
                                Parent root = loader.load();

                                Stage popupStage = new Stage();
                                popupStage.initModality(Modality.APPLICATION_MODAL);
                                popupStage.initOwner(loginStage);
                                popupStage.initStyle(StageStyle.UNDECORATED);
                                popupStage.setScene(new Scene(root));

                                popupStage.setOnHidden(e -> {
                                    // Retorna ao login após sucesso
                                    loginStage.getScene().getRoot().setEffect(null);
                                    vboxNovaSenha.setVisible(false);
                                    vboxNovaSenha.setManaged(false);
                                    vboxLogin.setVisible(true);
                                    vboxLogin.setManaged(true);

                                    // Limpa os campos
                                    campoSenhaRedefinicao.clear();
                                    campoConfirmacaoSenhaRedefinicao.clear();
                                    campoTextCodigo.clear();
                                    campoEmail.clear();
                                });

                                popupStage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            labelErroSenha.setText("Erro ao redefinir a senha.");
                            labelErroSenha.setStyle("-fx-text-fill: red;");
                        }
                    });
                });
    }

    // Reenvia um novo código para o backend, mas não valida o email, endpoint apenas para reenvio do código
    @FXML
    private void onClickReenviarCodigo(ActionEvent event) {
        String novoCodigo = String.format("%04d", new Random().nextInt(10000));

        String jsonBody = String.format("{\"email\": \"%s\", \"codigo\": \"%s\"}", emailTemporario, novoCodigo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/funcionarios/enviar-codigo"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // Requisição assíncrona padrão
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            labelErroCodigo.setText("Um novo código foi enviado para o seu e-mail.");
                            labelErroCodigo.setTextFill(Color.GREEN);
                        } else {
                            System.err.println("Erro ao reenviar. Status: " + response.statusCode() + " | Body: " + response.body());

                            labelErroCodigo.setText("Erro ao reenviar o código.");
                            labelErroCodigo.setTextFill(Color.RED);
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

    // Mudança de painel visível
    @FXML
    private void onClickVoltarEsqueceuSenha(ActionEvent event) {
        vboxCodigo.setVisible(false);
        vboxCodigo.setManaged(false);
        vboxEsqueceuSenha.setVisible(true);
        vboxEsqueceuSenha.setManaged(true);
    }

}
