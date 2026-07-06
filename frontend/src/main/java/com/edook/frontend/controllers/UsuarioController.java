package com.edook.frontend.controllers;

import com.edook.frontend.session.UserSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class UsuarioController implements Initializable {

    @FXML
    private VBox vboxDados, vboxEdicao;

    @FXML
    private Label lblErro, lblNome, lblCPF, lblCargo, lblMatricula, lblEmail, lblTelefone;

    @FXML
    private TextField campoNome, campoTelefone;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aplicarMascaraTelefone(campoTelefone);

        carregarDadosUsuario();
    }

    private void carregarDadosUsuario() {
        String cpfUsuario = UserSession.getInstance().getCpf();

        String url = "http://localhost:8080/funcionarios/busca?identificador=" + cpfUsuario;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200 && response.body().trim().startsWith("{")) {
                        try {
                            JsonNode json = objectMapper.readTree(response.body());

                            Platform.runLater(() -> {
                                String nome = json.get("nome").asText();
                                String email = json.get("email").asText();
                                String cpf = json.get("cpf").asText();
                                String cargo = json.get("cargo").asText();
                                String matricula = json.get("matricula").asText();

                                String ddd = json.has("ddd") && !json.get("ddd").isNull() ? json.get("ddd").asText() : "";
                                String numero = json.has("numero") && !json.get("numero").isNull() ? json.get("numero").asText() : "";
                                String telefoneCompleto = ddd + numero;

                                lblNome.setText(nome);
                                lblEmail.setText(email);
                                lblCPF.setText(formatarCpfExibicao(cpf));
                                lblCargo.setText(cargo);
                                lblMatricula.setText(matricula);
                                lblTelefone.setText(telefoneCompleto.isEmpty() ? "Não informado" : formatarTelefoneExibicao(telefoneCompleto));

                                campoNome.setText(nome);
                                campoTelefone.setText(telefoneCompleto);
                            });

                        } catch (Exception e) {
                            System.err.println("Erro ao processar o JSON recebido do servidor: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        String mensagemBackend = response.body();
                        if (mensagemBackend == null || mensagemBackend.isEmpty() || mensagemBackend.startsWith("{")) {
                            mensagemBackend = "Status " + response.statusCode();
                        }
                        System.err.println("Falha ao buscar os dados do usuário. Resposta do servidor: " + mensagemBackend);
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Erro de conexão com o servidor na busca de dados do usuário: " + ex.getMessage());
                    return null;
                });
    }

    private String formatarTelefoneExibicao(String telefone) {
        if (telefone.length() == 11) {
            return "(" + telefone.substring(0, 2) + ") " + telefone.substring(2, 7) + "-" + telefone.substring(7);
        } else if (telefone.length() == 10) {
            return "(" + telefone.substring(0, 2) + ") " + telefone.substring(2, 6) + "-" + telefone.substring(6);
        }
        return telefone;
    }

    private String formatarCpfExibicao(String cpf) {
        if (cpf != null && cpf.length() == 11) {
            return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
        }
        return cpf;
    }

    private boolean validarFormulario() {
        String nome = campoNome.getText().trim();
        String telefone = campoTelefone.getText().replaceAll("[^0-9]", "");

        if (nome.isEmpty() || telefone.isEmpty()) {
            lblErro.setText("Todos os campos devem ser preenchidos!");
            lblErro.setStyle("-fx-text-fill: red;");
            return false;
        }

        if (telefone.length() != 11) {
            lblErro.setText("Telefone incompleto, deve ter DDD com 2 dígitos, mais 9 dígitos!");
            lblErro.setStyle("-fx-text-fill: red;");
            return false;
        }

        return true;
    }

    private void aplicarMascaraTelefone(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.isDeleted() || change.getText().isEmpty()) {
                return change;
            }

            if (!change.getText().matches("[0-9]*")) {
                return null;
            }

            String novoTexto = change.getControlNewText().replaceAll("[^0-9]", "");

            if (novoTexto.length() > 11) {
                return null;
            }

            StringBuilder sb = new StringBuilder();
            if (novoTexto.length() > 0) {
                sb.append("(");
                sb.append(novoTexto.substring(0, Math.min(novoTexto.length(), 2)));
            }
            if (novoTexto.length() > 2) {
                sb.append(") ");
                sb.append(novoTexto.substring(2, Math.min(novoTexto.length(), 7)));
            }
            if (novoTexto.length() > 7) {
                sb.append("-");
                sb.append(novoTexto.substring(7, Math.min(novoTexto.length(), 11)));
            }

            change.setRange(0, change.getControlText().length());
            change.setText(sb.toString());
            change.setCaretPosition(sb.length());
            change.setAnchor(sb.length());

            return change;
        };

        textField.setTextFormatter(new TextFormatter<>(filter));
    }

    @FXML
    private void onClickEditar(ActionEvent event) {
        vboxDados.setVisible(false);
        vboxDados.setManaged(false);
        vboxEdicao.setVisible(true);
        vboxEdicao.setManaged(true);
    }

    @FXML
    private void onClickCancelar(ActionEvent event) {
        vboxEdicao.setVisible(false);
        vboxEdicao.setManaged(false);
        vboxDados.setVisible(true);
        vboxDados.setManaged(true);
    }

    @FXML
    private void onClickSalvar(ActionEvent event) {
        if (!validarFormulario()) {
            return;
        }

        String nomeAntigo = lblNome.getText();
        String telefoneAntigo = lblTelefone.getText();
        String nomeNovo = campoNome.getText().trim();
        String telefoneNovo = campoTelefone.getText().trim();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/ConfirmacaoEdicaoUsuario-view.fxml"));
            Parent root = loader.load();

            ConfirmacaoEdicaoUsuarioController popupController = loader.getController();

            // Configura o pop-up passando os dados e a ação que será disparada ao confirmar
            popupController.setDados(nomeAntigo, telefoneAntigo, nomeNovo, telefoneNovo, () -> carregarEdicao());

            Stage popupStage = new Stage();
            Stage donoJanela = (Stage) vboxEdicao.getScene().getWindow();
            Parent rootPrincipal = donoJanela.getScene().getRoot();

            // Aplica efeito de desfoque (Blur) na tela de fundo
            rootPrincipal.setEffect(new GaussianBlur(15));

            popupStage.initOwner(donoJanela);
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.centerOnScreen();

            popupStage.showAndWait();

            // Remove o efeito quando fechar
            rootPrincipal.setEffect(null);

        } catch (IOException e) {
            System.err.println("Erro ao abrir pop-up de confirmação de edição.");
            e.printStackTrace();
        }
    }

    private void carregarEdicao() {
        String cpfLogado = UserSession.getInstance().getCpf();
        String url = "http://localhost:8080/funcionarios/" + cpfLogado;

        String apenasNumeros = campoTelefone.getText().replaceAll("[^0-9]", "");
        String ddd = apenasNumeros.substring(0, 2);
        String numero = apenasNumeros.substring(2);

        // Monta o corpo DTO compatível com o FuncionarioUpdateDto do Backend
        Map<String, String> dadosAtualizados = new HashMap<>();
        dadosAtualizados.put("nome", campoNome.getText().trim());
        dadosAtualizados.put("ddd", ddd);
        dadosAtualizados.put("numero", numero);

        try {
            String jsonBody = objectMapper.writeValueAsString(dadosAtualizados);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/SucessoEdicaoUsuario-view.fxml"));
                                    Parent root = loader.load();

                                    SucessoEdicaoUsuarioController popupController = loader.getController();
                                    popupController.setOnFinalizar(() -> {
                                        carregarDadosUsuario();
                                        onClickCancelar(null);
                                    });

                                    Stage popupStage = new Stage();
                                    Stage donoJanela = (Stage) vboxEdicao.getScene().getWindow();
                                    Parent rootPrincipal = donoJanela.getScene().getRoot();

                                    rootPrincipal.setEffect(new GaussianBlur(15));

                                    popupStage.initOwner(donoJanela);
                                    popupStage.initModality(Modality.WINDOW_MODAL);
                                    popupStage.initStyle(StageStyle.TRANSPARENT);

                                    Scene scene = new Scene(root);
                                    scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                                    popupStage.setScene(scene);
                                    popupStage.centerOnScreen();

                                    popupStage.showAndWait();
                                    rootPrincipal.setEffect(null);

                                } catch (IOException e) {
                                    System.err.println("Erro ao abrir pop-up de sucesso da edição.");
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            Platform.runLater(() -> {
                                lblErro.setText("Erro do servidor ao salvar alterações.");
                                lblErro.setStyle("-fx-text-fill: red;");
                            });
                        }
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            lblErro.setText("Erro de conexão ao tentar atualizar os dados.");
                            lblErro.setStyle("-fx-text-fill: red;");
                        });
                        return null;
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
