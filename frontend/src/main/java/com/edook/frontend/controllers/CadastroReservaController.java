package com.edook.frontend.controllers;

import com.edook.frontend.models.EquipamentoResponseDTO;
import com.edook.frontend.session.UserSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// Controlador do modal de cadastro de reservas, semelhante aos outros controladores de cadastro
// CadastroEquipamentoController com comentários mais detalhados
public class CadastroReservaController {

    @FXML
    private TextField txtTitulo, txtHoraInicio, txtHoraFim;

    @FXML
    private ComboBox<String> cbLocal;

    @FXML
    private DatePicker dpData;

    @FXML
    private MenuButton mbEquipamentos;

    @FXML
    private Label lblErro;

    private Runnable onCadastroSucesso;

    @FXML
    public void initialize() {
        configurarComportamentoDatePicker(dpData);
        aplicarMascaraHorario(txtHoraInicio);
        aplicarMascaraHorario(txtHoraFim);
        buscarEquipamentos();

        cbLocal.setItems(javafx.collections.FXCollections.observableArrayList(
                "Sala 1",
                "Sala 2",
                "Sala 3",
                "Sala 4",
                "Sala 5",
                "Sala 6",
                "Sala 7",
                "Sala 8",
                "Sala 9",
                "Giroteca"
        ));

    }

    private void configurarComportamentoDatePicker(DatePicker datePicker) {
        if (datePicker != null) {
            datePicker.setEditable(false);

            datePicker.getEditor().setOnMouseClicked(event -> {
                if (!datePicker.isShowing()) {
                    datePicker.show();
                }
            });
        }
    }

    private void aplicarMascaraHorario(TextField textField) {
        if (textField == null) {
            return;
        }

        textField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.isDeleted()) {
                return change;
            }

            String textoFuturo = change.getControlNewText();

            if (textoFuturo.length() > 5) {
                return null;
            }

            if (textoFuturo.length() == 3 && !textoFuturo.contains(":")) {
                String caractereDigitado = change.getText();
                change.setText(":" + caractereDigitado);
                change.setCaretPosition(change.getCaretPosition() + 1);
                change.setAnchor(change.getAnchor() + 1);
                textoFuturo = change.getControlNewText();
            }

            if (textoFuturo.matches("^([0-1]?[0-9]|2[0-3])?:?[0-5]?[0-9]?$")) {
                return change;
            }

            return null;
        }));
    }

    @FXML
    public void onClickEnviar(ActionEvent event) {
        if (txtTitulo.getText() == null || txtTitulo.getText().trim().isEmpty() || cbLocal.getValue() == null || dpData.getValue() == null || txtHoraInicio.getText() == null || txtHoraInicio.getText().trim().isEmpty() || txtHoraFim.getText() == null || txtHoraFim.getText().trim().isEmpty()) {
            lblErro.setText("Todos os campos devem ser preenchidos.");
            lblErro.setStyle("-fx-text-fill: red;");
            return;
        }

        List<EquipamentoResponseDTO> equipamentosParaEnviar = new ArrayList<>();
        for (MenuItem item : mbEquipamentos.getItems()) {
            if (item instanceof CustomMenuItem customItem && customItem.getContent() instanceof CheckBox cb) {
                if (cb.isSelected()) {
                    EquipamentoResponseDTO eq = (EquipamentoResponseDTO) cb.getUserData();
                    equipamentosParaEnviar.add(eq);
                }
            }
        }

        if (equipamentosParaEnviar.isEmpty()) {
            lblErro.setText("Pelo menos 1 equipamento deve ser selecionado.");
            lblErro.setStyle("-fx-text-fill: red;");
            return;
        }

        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("nome", txtTitulo.getText().trim());
        payload.put("localidade", cbLocal.getValue());
        payload.put("dia", dpData.getValue().toString());
        payload.put("horarioInicio", formatarHorario(txtHoraInicio.getText().trim()));
        payload.put("horarioFim", formatarHorario(txtHoraFim.getText().trim()));

        String cpfUsuarioLogado = UserSession.getInstance().getCpf();
        payload.put("cpfFuncionario", cpfUsuarioLogado);
        payload.put("equipamentos", equipamentosParaEnviar);

        String dataStr = dpData.getValue().toString();
        String horaStr = txtHoraInicio.getText().trim();
        String salaStr = cbLocal.getValue();

        String equipStr = equipamentosParaEnviar.stream()
                        .map(EquipamentoResponseDTO::getDescricao)
                        .collect(java.util.stream.Collectors.joining(", "));

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/edook/frontend/ConfirmacaoCadastroReserva-view.fxml"));
            javafx.scene.Parent root = loader.load();

            ConfirmacaoCadastroReservaController controller = loader.getController();
            controller.setDados(dataStr, horaStr, equipStr, salaStr);

            controller.setOnConfirmar(() -> enviarReserva(payload));

            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            Stage donoDaJanela = (Stage) mbEquipamentos.getScene().getWindow();
            javafx.scene.Parent rootCadastro = donoDaJanela.getScene().getRoot();

            javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(15);
            rootCadastro.setEffect(blur);

            popupStage.initOwner(donoDaJanela);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.centerOnScreen();

            popupStage.showAndWait();

            rootCadastro.setEffect(null);

        } catch (Exception e) {
            e.printStackTrace();
            lblErro.setText("Erro ao abrir confirmação.");
        }
    }

    private void enviarReserva(java.util.Map<String, Object> payload) {
        CompletableFuture.runAsync(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonPayload = mapper.writeValueAsString(payload);

                System.out.println("Enviando JSON: " + jsonPayload);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/reservas"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                Platform.runLater(() -> {
                    if (response.statusCode() == 201) {
                        Platform.runLater(() -> {
                            try {
                                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/edook/frontend/SucessoCadastroReserva-view.fxml"));
                                javafx.scene.Parent root = loader.load();

                                SucessoCadastroReservaController controller = loader.getController();

                                controller.setOnFinalizar(() -> {
                                    Stage stage = (Stage) mbEquipamentos.getScene().getWindow();
                                    stage.close();

                                    if (onCadastroSucesso != null) {
                                        onCadastroSucesso.run();
                                    }
                                });

                                Stage popupStage = new Stage();
                                popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                                popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

                                Stage donoDaJanela = (Stage) mbEquipamentos.getScene().getWindow();
                                javafx.scene.Parent rootCadastro = donoDaJanela.getScene().getRoot();

                                javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(15);
                                rootCadastro.setEffect(blur);

                                popupStage.initOwner(donoDaJanela);

                                javafx.scene.Scene scene = new javafx.scene.Scene(root);
                                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                                popupStage.setScene(scene);
                                popupStage.centerOnScreen();

                                popupStage.showAndWait();

                                rootCadastro.setEffect(null);
                            } catch (Exception e) {
                                e.printStackTrace();
                                lblErro.setText("Reserva criada, mas erro ao abrir pop-up de sucesso.");
                            }
                        });
                    } else {
                        System.out.println("Erro ao enviar reserva: " + response.statusCode() + " - " + response.body());

                        String mensagemErro = response.body();
                        if (mensagemErro == null || mensagemErro.trim().isEmpty()) {
                            mensagemErro = "O servidor retornou o status " + response.statusCode() + " sem uma descrição.";
                        }

                        String finalMensagemErro = mensagemErro;

                        Platform.runLater(() -> {
                            try {
                                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/edook/frontend/OperacaoInvalida-view.fxml"));
                                javafx.scene.Parent root = loader.load();

                                OperacaoInvalidaController popupController = loader.getController();
                                popupController.setMensagem("Cadastro Inválido", finalMensagemErro);

                                Stage popupStage = new Stage();
                                popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                                popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

                                Stage donoDaJanela = (Stage) mbEquipamentos.getScene().getWindow();
                                javafx.scene.Parent rootCadastro = donoDaJanela.getScene().getRoot();

                                javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(15);
                                rootCadastro.setEffect(blur);

                                popupStage.initOwner(donoDaJanela);

                                javafx.scene.Scene scene = new javafx.scene.Scene(root);
                                scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                                if (getClass().getResource("/com/edook/frontend/style.css") != null) {
                                    scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());
                                }

                                popupStage.setScene(scene);
                                popupStage.centerOnScreen();

                                popupStage.showAndWait();

                                rootCadastro.setEffect(null);
                            } catch (Exception e) {
                                System.err.println("Erro crítico ao tentar inflar o pop-up de erro.");
                                e.printStackTrace();
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    try {
                        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/edook/frontend/OperacaoInvalida-view.fxml"));
                        javafx.scene.Parent root = loader.load();

                        OperacaoInvalidaController popupController = loader.getController();
                        popupController.setMensagem("Falha de Conexão", "Tente novamente mais tarde.");

                        Stage popupStage = new Stage();
                        popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                        popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

                        Stage donoDaJanela = (Stage) mbEquipamentos.getScene().getWindow();
                        javafx.scene.Parent rootCadastro = donoDaJanela.getScene().getRoot();

                        javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(15);
                        rootCadastro.setEffect(blur);

                        popupStage.initOwner(donoDaJanela);

                        javafx.scene.Scene scene = new javafx.scene.Scene(root);
                        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                        if (getClass().getResource("/com/edook/frontend/style.css") != null) {
                            scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());
                        }

                        popupStage.setScene(scene);
                        popupStage.centerOnScreen();

                        popupStage.showAndWait();

                        rootCadastro.setEffect(null);
                    } catch (Exception ex) {
                        System.err.println("Erro crítico ao tentar inflar o pop-up de erro.");
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private String formatarHorario(String horario) {
        if (horario != null && horario.length() == 5) {
            return horario + ":00";
        }
        return horario;
    }

    private void buscarEquipamentos() {
        mbEquipamentos.getItems().clear();

        CompletableFuture.runAsync(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/equipamentos"))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    List<EquipamentoResponseDTO> equipamentos = mapper.readValue(
                            response.body(),
                            new TypeReference<List<EquipamentoResponseDTO>>() {}
                    );

                    Platform.runLater(() -> {
                        mbEquipamentos.setText("Selecione...");

                        for (EquipamentoResponseDTO eq : equipamentos) {
                            String textoMenu = eq.getPrefixo() + eq.getNumero() + " - " + eq.getDescricao();
                            CheckBox cb = new CheckBox(textoMenu);

                            cb.setUserData(eq);

                            CustomMenuItem item = new CustomMenuItem(cb);
                            item.setHideOnClick(false);
                            mbEquipamentos.getItems().add(item);
                        }
                    });
                } else {
                    Platform.runLater(() -> mbEquipamentos.setText("Erro ao carregar (Status: " + response.statusCode() + ")"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> mbEquipamentos.setText("Falha na conexão"));
            }
        });
    }

    public void setOnCadastroSucesso(Runnable onCadastroSucesso) {
        this.onCadastroSucesso = onCadastroSucesso;
    }

    @FXML
    private void onClickCancelar(ActionEvent event) {
        javafx.scene.Node source = (javafx.scene.Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}