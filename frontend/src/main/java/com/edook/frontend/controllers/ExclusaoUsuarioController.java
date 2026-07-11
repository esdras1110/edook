package com.edook.frontend.controllers;

import com.edook.frontend.models.FuncionarioResponseDTO;
import com.edook.frontend.session.UserSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public class ExclusaoUsuarioController implements Initializable {
    @FXML
    private Label lblErro;

    @FXML
    private TextField txtCPF;

    private Runnable onExclusaoSucesso;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aplicarMascaraCPF(txtCPF);
    }

    private void aplicarMascaraCPF(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.isDeleted() || change.getText().isEmpty()) {
                return change;
            }
            if (!change.getText().matches("[0-9]*")) {
                return null;
            }
            String textoLimpo = change.getControlNewText().replaceAll("[^0-9]", "");
            if (textoLimpo.length() > 11) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            int len = textoLimpo.length();
            if (len > 0) sb.append(textoLimpo.substring(0, Math.min(len, 3)));
            if (len > 3) sb.append(".").append(textoLimpo.substring(3, Math.min(len, 6)));
            if (len > 6) sb.append(".").append(textoLimpo.substring(6, Math.min(len, 9)));
            if (len > 9) sb.append("-").append(textoLimpo.substring(9, Math.min(len, 11)));

            change.setRange(0, change.getControlText().length());
            change.setText(sb.toString());
            change.setCaretPosition(sb.length());
            change.setAnchor(sb.length());
            return change;
        };
        textField.setTextFormatter(new TextFormatter<>(filter));
    }

    public void setOnExclusaoSucesso(Runnable onExclusaoSucesso) {
        this.onExclusaoSucesso = onExclusaoSucesso;
    }

    @FXML
    void onClickCancelar(ActionEvent event) {
        fecharJanela(event);
    }

    @FXML
    void onClickEnviar(ActionEvent event) {
        String cpf = txtCPF.getText().replaceAll("[^0-9]", "");

        if (cpf.isEmpty()) {
            lblErro.setText("Digite um CPF ou Matrícula válido.");
            return;
        }

        String cpfLogado = UserSession.getInstance().getCpf().replaceAll("[^0-9]", "");
        if (cpf.equals(cpfLogado)) {
            abrirPopupErro("Operação Inválida", "Você não pode excluir a sua própria conta do sistema!");
            return;
        }

        lblErro.setText("Buscando funcionário...");

        HttpClient client = HttpClient.newHttpClient();
        String url = "http://localhost:8080/funcionarios/busca?identificador=" +
                URLEncoder.encode(cpf, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        lblErro.setText("");
                        if (response.statusCode() == 200) {
                            try {
                                ObjectMapper mapper = new ObjectMapper();
                                FuncionarioResponseDTO funcionario = mapper.readValue(response.body(), FuncionarioResponseDTO.class);

                                if (funcionario.getCpf().replaceAll("[^0-9]", "").equals(cpfLogado)) {
                                    abrirPopupErro("Operação Inválida", "Você não pode excluir a sua própria conta do sistema!");
                                    return;
                                }

                                abrirPopupConfirmacao(funcionario);

                            } catch (Exception e) {
                                abrirPopupErro("Erro de Desserialização", "Falha ao processar a resposta do servidor.");
                            }
                        } else if (response.statusCode() == 404) {
                            abrirPopupErro("Operação Inválida", "Nenhum funcionário foi encontrado com a identificação fornecida.");
                        } else {
                            abrirPopupErro("Erro no Banco de Dados", "Falha na busca. Código do Servidor: " + response.statusCode());
                        }
                    });
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        lblErro.setText("");
                        abrirPopupErro("Falha de Rede", "Não foi possível conectar ao servidor do backend.");
                    });
                    return null;
                });
    }

    private void abrirPopupConfirmacao(FuncionarioResponseDTO funcionario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/ConfirmacaoExclusaoUsuario-view.fxml"));
            Parent root = loader.load();

            ConfirmacaoExclusaoUsuarioController controller = loader.getController();
            controller.setDados(funcionario);
            controller.setOnExclusaoSucesso(onExclusaoSucesso);

            Stage popupStage = new Stage();
            Stage donoDaJanela = (Stage) txtCPF.getScene().getWindow();
            Parent rootAtual = donoDaJanela.getScene().getRoot();

            rootAtual.setEffect(new GaussianBlur(15));

            popupStage.initOwner(donoDaJanela);
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
            rootAtual.setEffect(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirPopupErro(String titulo, String descricao) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/OperacaoInvalida-view.fxml"));
            Parent root = loader.load();

            OperacaoInvalidaController popupController = loader.getController();
            popupController.setMensagem(titulo, descricao);

            Stage popupStage = new Stage();
            Stage telaPrincipal = (Stage) txtCPF.getScene().getWindow();
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

    private void fecharJanela(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}