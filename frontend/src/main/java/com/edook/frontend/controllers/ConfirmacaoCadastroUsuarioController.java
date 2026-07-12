package com.edook.frontend.controllers;

import com.edook.frontend.models.FuncionarioResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Controlador do modal Confirmar Cadastro de Usuário. Semelhante aos outros controladores de confirmação.
// ConfirmacaoCadastroEquipamentoControlle comentado detalhadamente.
public class ConfirmacaoCadastroUsuarioController {
    @FXML
    private Label lblCPF, lblNome, lblMatricula, lblCargo;

    private FuncionarioResponseDTO funcionario;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private Runnable onSucesso;

    public void setOnSucesso(Runnable onSucesso) {
        this.onSucesso = onSucesso;
    }

    public void setDados(FuncionarioResponseDTO f) {
        this.funcionario = f;
        lblNome.setText(f.getNome());
        lblCPF.setText(f.getCpf());
        lblCargo.setText(f.getCargo());
        lblMatricula.setText(String.valueOf(f.getMatricula()));
    }

    @FXML
    void onClickCancelar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void onClickConfirmar(ActionEvent event) {
        if (funcionario == null) return;

        Stage stageConfirmacao = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage telaPrincipal = (Stage) stageConfirmacao.getOwner();

        stageConfirmacao.setOpacity(0);

        try {
            String jsonBody = mapper.writeValueAsString(funcionario);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/funcionarios"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            if (response.statusCode() == 201 || response.statusCode() == 200) {
                                abrirPopupValidacaoEmail(telaPrincipal, funcionario.getEmail());

                                stageConfirmacao.close();
                            } else {
                                stageConfirmacao.setOpacity(1);
                                abrirPopupErro(telaPrincipal, "Erro no Cadastro",
                                        "Não foi possível salvar o registro. Código: " + response.statusCode());
                            }
                        });
                    })
                    .exceptionally(e -> {
                        stageConfirmacao.setOpacity(1);
                        Platform.runLater(() -> abrirPopupErro(telaPrincipal, "Falha de Conexão", "Não foi possível conectar ao servidor."));
                        return null;
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirPopupValidacaoEmail(Stage telaPrincipal, String email) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/CodigoValidacaoEmail-view.fxml"));
            Parent root = loader.load();

            CodigoValidacaoEmailController controller = loader.getController();
            controller.setEmailValidacao(email);
            controller.setOnSucesso(this.onSucesso);

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

    private void abrirPopupErro(Stage telaPrincipal, String titulo, String descricao) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/OperacaoInvalida-view.fxml"));
            Parent root = loader.load();

            OperacaoInvalidaController popupController = loader.getController();
            popupController.setMensagem(titulo, descricao);

            Stage popupStage = new Stage();
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
}