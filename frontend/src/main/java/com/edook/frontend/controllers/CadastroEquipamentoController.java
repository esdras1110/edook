package com.edook.frontend.controllers;

import com.edook.frontend.session.UserSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CadastroEquipamentoController {
    @FXML
    private TextField txtDescricao;

    @FXML
    private ComboBox<String> cbTipo;

    @FXML
    private Label lblErro; // Adicionado para dar feedback visual de erros

    private Runnable onCadastroSucesso;

    @FXML
    public void initialize() {
        cbTipo.setItems(javafx.collections.FXCollections.observableArrayList(
                "Projetor",
                "Notebook",
                "Televisão",
                "Caixa de Som"
        ));
    }

    public void setOnCadastroSucesso(Runnable onCadastroSucesso) {
        this.onCadastroSucesso = onCadastroSucesso;
    }

    @FXML
    public void onClickEnviar(ActionEvent event) {
        String descricao = txtDescricao.getText();
        String tipo = cbTipo.getValue();

        // 1. Validação simples de campos vazios
        if (descricao == null || descricao.trim().isEmpty() || tipo == null) {
            if (lblErro != null) {
                lblErro.setText("Todos os campos devem ser preenchidos.");
                lblErro.setStyle("-fx-text-fill: red;");
            }
            return;
        }

        // 2. Derivação do prefixo conforme solicitado
        String prefixo;
        switch (tipo) {
            case "Projetor":
                prefixo = "PR";
                break;
            case "Notebook":
                prefixo = "NT";
                break;
            case "Caixa de Som":
                prefixo = "CS";
                break;
            case "Televisão":
                prefixo = "TV";
                break;
            default:
                prefixo = "EQ"; // Fallback genérico de segurança
                break;
        }

        // 3. Montagem do payload esperado pelo EquipamentoCreateDto do Spring Boot
        Map<String, Object> payload = new HashMap<>();
        payload.put("cpfCadastro", UserSession.getInstance().getCpf()); // Puxa da sessão logada
        payload.put("prefixo", prefixo);
        payload.put("descricao", descricao.trim());
        payload.put("tipo", tipo);

        // 4. Abertura do Popup de Confirmação com efeito de Blur no fundo
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/ConfirmacaoCadastroEquipamento-view.fxml"));
            Parent root = loader.load();

            ConfirmacaoCadastroEquipamentoController controller = loader.getController();
            controller.setDados(descricao.trim(), tipo);

            // Passa a ação de envio como Runnable para disparar após o usuário clicar em Confirmar
            controller.setOnConfirmar(() -> enviarEquipamento(payload));

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Stage donoDaJanela = (Stage) txtDescricao.getScene().getWindow();
            Parent rootCadastro = donoDaJanela.getScene().getRoot();

            // Ativa o Blur
            GaussianBlur blur = new GaussianBlur(15);
            rootCadastro.setEffect(blur);

            popupStage.initOwner(donoDaJanela);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.centerOnScreen();

            // Aguarda o fechamento do popup de confirmação
            popupStage.showAndWait();

            // Desativa o Blur
            rootCadastro.setEffect(null);

        } catch (Exception e) {
            e.printStackTrace();
            if (lblErro != null) {
                lblErro.setText("Erro ao abrir a tela de confirmação.");
            }
        }
    }

    private void enviarEquipamento(Map<String, Object> payload) {
        // Envio assíncrono para não travar a interface JavaFX
        CompletableFuture.runAsync(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonPayload = mapper.writeValueAsString(payload);

                System.out.println("Enviando JSON Equipamento: " + jsonPayload);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/equipamentos"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Retorna para a Thread principal da UI para manipular as telas
                Platform.runLater(() -> {
                    if (response.statusCode() == 201 || response.statusCode() == 200) {
                        abrirPopupSucesso();
                    } else {
                        System.err.println("Erro do backend: " + response.statusCode() + " - " + response.body());
                        if (lblErro != null) {
                            lblErro.setText("Erro ao salvar equipamento. Status: " + response.statusCode());
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    if (lblErro != null) {
                        lblErro.setText("Falha na conexão com o servidor.");
                    }
                });
            }
        });
    }

    private void abrirPopupSucesso() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/SucessoCadastroEquipamento-view.fxml"));
            Parent root = loader.load();

            SucessoCadastroEquipamentoController controller = loader.getController();
            controller.setOnFinalizar(() -> {
                // Fecha a janela atual de formulário de cadastro
                Stage stage = (Stage) txtDescricao.getScene().getWindow();
                stage.close();

                // Dispara o callback para atualizar a tabela na tela de listagem principal
                if (onCadastroSucesso != null) {
                    onCadastroSucesso.run();
                }
            });

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Stage donoDaJanela = (Stage) txtDescricao.getScene().getWindow();
            Parent rootCadastro = donoDaJanela.getScene().getRoot();

            GaussianBlur blur = new GaussianBlur(15);
            rootCadastro.setEffect(blur);

            popupStage.initOwner(donoDaJanela);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.centerOnScreen();

            popupStage.showAndWait();

            rootCadastro.setEffect(null);

        } catch (Exception e) {
            e.printStackTrace();
            if (lblErro != null) {
                lblErro.setText("Equipamento salvo, mas houve um erro ao exibir confirmação de sucesso.");
            }
        }
    }

    @FXML
    private void onClickCancelar(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}