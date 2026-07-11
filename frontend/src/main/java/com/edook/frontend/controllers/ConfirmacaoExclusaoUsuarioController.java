package com.edook.frontend.controllers;

import com.edook.frontend.models.FuncionarioResponseDTO;
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

public class ConfirmacaoExclusaoUsuarioController {
    @FXML
    private Label lblCPF, lblMatricula, lblNome, lblCargo;

    private FuncionarioResponseDTO funcionarioSelecionado;
    private Runnable onExclusaoSucesso;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void setDados(FuncionarioResponseDTO f) {
        this.funcionarioSelecionado = f;
        lblNome.setText(f.getNome());
        lblCPF.setText(f.getCpf());
        lblCargo.setText(f.getCargo());
        lblMatricula.setText(f.getMatricula() != null ? f.getMatricula().toString() : "");
    }

    public void setOnExclusaoSucesso(Runnable onExclusaoSucesso) {
        this.onExclusaoSucesso = onExclusaoSucesso;
    }

    @FXML
    void onClickCancelar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void onClickConfirmar(ActionEvent event) {
        if (funcionarioSelecionado == null) return;

        // Pega a referência da tela de Busca e da Janela Principal (CadastroController)
        Stage stageConfirmacao = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage stageBusca = (Stage) stageConfirmacao.getOwner();
        Stage telaPrincipal = (Stage) stageBusca.getOwner();

        // Fecha as janelas do fluxo de exclusão imediatamente
        stageConfirmacao.close();
        stageBusca.close();

        String url = "http://localhost:8080/funcionarios/" + funcionarioSelecionado.getCpf();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 204 || response.statusCode() == 200) {
                            if (onExclusaoSucesso != null) {
                                onExclusaoSucesso.run();
                            }

                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/SucessoExclusaoUsuario-view.fxml"));
                                Parent root = loader.load();

                                Stage popupStage = new Stage();
                                Parent rootPrincipal = telaPrincipal.getScene().getRoot();
                                rootPrincipal.setEffect(new GaussianBlur(15));

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
                                rootPrincipal.setEffect(null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else if (response.statusCode() == 400) {
                            abrirPopupErro(telaPrincipal, "Operação Inválida",
                                    "Não é possível excluir o funcionário, pois ele possui reservas ativas ou pendentes registradas no banco de dados.");
                        } else {
                            abrirPopupErro(telaPrincipal, "Erro no Banco de Dados",
                                    "Ocorreu uma falha inesperada ao tentar remover o registro. Status: " + response.statusCode());
                        }
                    });
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        abrirPopupErro(telaPrincipal, "Falha na Comunicação", "Não foi possível processar a deleção devido a um erro de rede.");
                    });
                    return null;
                });
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