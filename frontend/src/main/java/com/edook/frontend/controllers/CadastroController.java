package com.edook.frontend.controllers;

import com.edook.frontend.models.EquipamentoResponseDTO;
import com.edook.frontend.models.FuncionarioResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class CadastroController implements Initializable {
    @FXML
    private VBox vboxBotoes, vboxCadastro, vboxGerenciarEquipamentos;

    @FXML
    private TextField campoNome, campoCPF, campoMatricula, campoTelefone, campoEmail, campoBusca;

    @FXML
    private ComboBox<String> campoCargo;

    @FXML
    private PasswordField campoSenha, campoConfirmacaoSenha;

    @FXML
    private Label lblErro;

    @FXML
    private TableView<EquipamentoResponseDTO> tabelaEquipamentos;

    @FXML
    private TableColumn<EquipamentoResponseDTO, String> colPrefixo, colDescricao, colTipo;

    @FXML
    private TableColumn<EquipamentoResponseDTO, Integer> colNumero;

    private final ObservableList<EquipamentoResponseDTO> listaEquipamentos = FXCollections.observableArrayList();
    private FilteredList<EquipamentoResponseDTO> listaFiltrada;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aplicarMascaraTelefone(campoTelefone);
        aplicarMascaraCPF(campoCPF);
        aplicarMascaraMatricula(campoMatricula);
        campoCargo.getItems().addAll(
                "Docente",
                "Administrativo"
        );
        campoCargo.getSelectionModel().select("Docente");

        colPrefixo.setCellValueFactory(new PropertyValueFactory<>("prefixo"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        colDescricao.setCellFactory(tc -> {
            return new TableCell<EquipamentoResponseDTO, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setTooltip(null);
                    } else {
                        setText(item);
                        Tooltip tooltip = new Tooltip(item);
                        tooltip.setStyle("-fx-font-size: 13px; -fx-background-color: #1E1E1E; -fx-text-fill: #F9FAFB;");
                        tooltip.setWrapText(true);
                        tooltip.setPrefWidth(360);
                        setTooltip(tooltip);
                    }
                }
            };
        });

        listaFiltrada = new FilteredList<>(listaEquipamentos, b -> true);
        tabelaEquipamentos.setItems(listaFiltrada);

        carregarEquipamentos();

        campoBusca.textProperty().addListener((observable, oldValue, newValue) -> {
            atualizarBusca();
        });
    }

    protected void carregarEquipamentos() {
        String url = "http://localhost:8080/equipamentos";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        // Envia a requisição de forma assíncrona
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        List<EquipamentoResponseDTO> dtos = mapper.readValue(
                                response.body(),
                                new TypeReference<List<EquipamentoResponseDTO>>(){}
                        );

                        // 2. Atualiza a ObservableList na Thread do JavaFX
                        Platform.runLater(() -> {
                            listaEquipamentos.setAll(dtos);
                        });

                    } catch (Exception e) {
                        System.err.println("Erro ao converter os dados: " + e.getMessage());
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Erro de conexão com a API: " + e.getMessage());
                    return null;
                });
    }

    private void atualizarBusca() {
        String textoBusca = campoBusca.getText() == null ? "" : campoBusca.getText().toLowerCase();

        listaFiltrada.setPredicate(reserva -> {
            // 1. Regra da Barra de Pesquisa
            boolean passaBusca = true;
            if (!textoBusca.isEmpty()) {
                passaBusca = (reserva.getDescricao() != null && reserva.getDescricao().toLowerCase().contains(textoBusca)) ||
                        (reserva.getTipo() != null && reserva.getTipo().toLowerCase().contains(textoBusca));
            }

            return passaBusca;
        });
    }

    private boolean validarFormulario() {
        String nome = campoNome.getText().trim();
        String cpf = campoCPF.getText().replaceAll("[^0-9]", "");
        String telefone = campoTelefone.getText().replaceAll("[^0-9]", "");
        String matricula = campoMatricula.getText().trim();
        String cargo = campoCargo.getValue();
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText().trim();
        String confirmacaoSenha = campoConfirmacaoSenha.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty() || matricula.isEmpty() || cargo.isEmpty() || telefone.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmacaoSenha.isEmpty()) {
            lblErro.setText("Todos os campos devem ser preenchidos!");
            lblErro.setStyle("-fx-text-fill: red;");
            return false;
        }

        if (cpf.length() != 11) {
            lblErro.setText("CPF incompleto, deve ter 11 dígitos!");
            lblErro.setStyle("-fx-text-fill: red;");
            return false;
        }

        if (matricula.length() != 7) {
            lblErro.setText("Matrícula incompleta, deve ter 7 dígitos!");
            lblErro.setStyle("-fx-text-fill: red;");
            return false;
        }

        if (telefone.length() != 11) {
            lblErro.setText("Telefone incompleto, deve ter DDD com 2 dígitos, mais 9 dígitos!");
            lblErro.setStyle("-fx-text-fill: red;");
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            lblErro.setText("E-mail inválido!");
            lblErro.setStyle("-fx-text-fill: red;");
            return false;
        }

        String senhaRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.{6,}).+$";
        if (!senha.matches(senhaRegex)) {
            lblErro.setText("Senha inválida, deve conter no mínimo 6 caracteres, 1 letra maiúscula, 1 letra minúscula e 1 caractere especial!");
            lblErro.setStyle("-fx-text-fill: red;");
            return false;
        }

        if (!senha.equals(confirmacaoSenha)) {
            lblErro.setText("Senha e confirmação de senha não coincidem!");
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

            if (len > 0) {
                sb.append(textoLimpo.substring(0, Math.min(len, 3)));
            }
            if (len > 3) {
                sb.append(".");
                sb.append(textoLimpo.substring(3, Math.min(len, 6)));
            }
            if (len > 6) {
                sb.append(".");
                sb.append(textoLimpo.substring(6, Math.min(len, 9)));
            }
            if (len > 9) {
                sb.append("-");
                sb.append(textoLimpo.substring(9, Math.min(len, 11)));
            }

            change.setRange(0, change.getControlText().length());
            change.setText(sb.toString());
            change.setCaretPosition(sb.length());
            change.setAnchor(sb.length());

            return change;
        };

        textField.setTextFormatter(new TextFormatter<>(filter));
    }

    private void aplicarMascaraMatricula(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.isDeleted() || change.getText().isEmpty()) {
                return change;
            }

            if (!change.getText().matches("[0-9]*")) {
                return null;
            }

            if (change.getControlNewText().length() > 7) {
                return null;
            }

            return change;
        };

        textField.setTextFormatter(new TextFormatter<>(filter));
    }

    @FXML
    private void onClickCadastrarUsuario(ActionEvent event) {
        vboxBotoes.setVisible(false);
        vboxBotoes.setManaged(false);
        vboxCadastro.setVisible(true);
        vboxCadastro.setManaged(true);
    }

    @FXML
    private void onClickCadastrar(ActionEvent event) {
        if(!validarFormulario()){
            return;
        }

        FuncionarioResponseDTO novoFuncionario = new FuncionarioResponseDTO();
        novoFuncionario.setNome(campoNome.getText());
        novoFuncionario.setCpf(campoCPF.getText().replaceAll("[^0-9]", ""));
        novoFuncionario.setMatricula(Integer.valueOf(campoMatricula.getText()));
        novoFuncionario.setDdd(campoTelefone.getText().replaceAll("[^0-9]", "").substring(0,2));
        novoFuncionario.setNumero(campoTelefone.getText().replaceAll("[^0-9]", "").substring(2));
        novoFuncionario.setEmail(campoEmail.getText());
        novoFuncionario.setCargo(campoCargo.getValue());
        novoFuncionario.setSenha(campoSenha.getText());
        String codigoGerado = String.format("%04d", new Random().nextInt(10000));
        novoFuncionario.setCodigoVerificacao(codigoGerado);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/ConfirmacaoCadastroUsuario-view.fxml"));
            Parent root = loader.load();

            ConfirmacaoCadastroUsuarioController controller = loader.getController();
            controller.setDados(novoFuncionario);

            Node sourceNode = (Node) event.getSource();
            Stage popupStage = new Stage();
            Stage telaPrincipal = (Stage) sourceNode.getScene().getWindow();
            Parent rootPrincipal = telaPrincipal.getScene().getRoot();

            // Aplica o Blur na tela principal
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

            // Espera todo o fluxo terminar
            popupStage.showAndWait();

            // Remove o Blur quando o fluxo inteiro (ou cancelamento) finalizar
            rootPrincipal.setEffect(null);

        } catch (Exception e) {
            e.printStackTrace();
            exibirPopupErro("Erro de Tela", "Não foi possível abrir a tela de confirmação.");
        }
    }

    @FXML
    private void onClickExcluirUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/ExclusaoUsuario-view.fxml"));
            Parent root = loader.load();

            ExclusaoUsuarioController controller = loader.getController();

            // Define o comportamento ao finalizar com sucesso
            controller.setOnExclusaoSucesso(() -> {
                System.out.println("Fluxo de exclusão de usuário concluído com sucesso!");
            });

            Stage popupStage = new Stage();
            Stage donoDaJanela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();

            // Ativa o Blur na tela principal
            rootPrincipal.setEffect(new GaussianBlur(15));

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

            // Aguarda a finalização do fluxo
            popupStage.showAndWait();

            // Remove o efeito de desfoque ao retornar
            rootPrincipal.setEffect(null);

        } catch (Exception e) {
            exibirPopupErro("Erro de Carregamento", "Não foi possível abrir a tela de exclusão de usuários.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickCadastrarEquipamento(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/CadastroEquipamento-view.fxml"));
            Parent root = loader.load();

            CadastroEquipamentoController controller = loader.getController();

            // Passa o método da listagem como Runnable! Ao clicar em finalizar, a lista se auto-atualiza.
            controller.setOnCadastroSucesso(() -> carregarEquipamentos());

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            // Adiciona o efeito de Blur na tela de fundo principal
            Stage donoDaJanela = (Stage) tabelaEquipamentos.getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();
            rootPrincipal.setEffect(new GaussianBlur(15));

            popupStage.initOwner(donoDaJanela);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.centerOnScreen();

            popupStage.showAndWait();
            rootPrincipal.setEffect(null); // Desativa o Blur ao voltar

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickGerenciarEquipamento(ActionEvent event) {
        vboxBotoes.setVisible(false);
        vboxBotoes.setManaged(false);
        vboxGerenciarEquipamentos.setVisible(true);
        vboxGerenciarEquipamentos.setManaged(true);
    }

    @FXML
    private void onClickVoltar(ActionEvent event) {
        vboxGerenciarEquipamentos.setVisible(false);
        vboxGerenciarEquipamentos.setManaged(false);
        vboxBotoes.setVisible(true);
        vboxBotoes.setManaged(true);
    }

    @FXML
    private void onClickEditarEquipamento(ActionEvent event) {
        ObservableList<EquipamentoResponseDTO> selecionados = tabelaEquipamentos.getSelectionModel().getSelectedItems();

        if (selecionados.isEmpty()) {
            exibirPopupErro("Nenhum equipamento selecionado", "Por favor, selecione um equipamento na tabela para editar.");
            return;
        }

        EquipamentoResponseDTO selecionado = selecionados.get(0);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/EdicaoEquipamento-view.fxml"));
            Parent root = loader.load();

            EdicaoEquipamentoController controller = loader.getController();

            // Passa os dados e a ação de recarregar a tabela ao finalizar
            controller.setEquipamento(selecionado);
            controller.setOnEdicaoSucesso(() -> carregarEquipamentos());

            Stage popupStage = new Stage();
            Stage donoDaJanela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();

            // Ativa o Blur na tela principal
            rootPrincipal.setEffect(new GaussianBlur(15));

            popupStage.initOwner(donoDaJanela);
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            // Mantém a padronização do CSS se existir
            if (getClass().getResource("/com/edook/frontend/style.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());
            }

            popupStage.setScene(scene);
            popupStage.centerOnScreen();

            // Congela o código aqui até o popup fechar
            popupStage.showAndWait();

            // Garante que o blur seja removido ao fechar o popup
            rootPrincipal.setEffect(null);

        } catch (Exception e) {
            exibirPopupErro("Erro de Carregamento", "Não foi possível abrir a tela de edição.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onClickExcluirEquipamento(ActionEvent event) {
        ObservableList<EquipamentoResponseDTO> selecionados = tabelaEquipamentos.getSelectionModel().getSelectedItems();

        if (selecionados.isEmpty()) {
            exibirPopupErro("Nenhum equipamento selecionado", "Por favor, selecione um equipamento na tabela para excluir.");
            return;
        }

        EquipamentoResponseDTO selecionado = selecionados.get(0);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/ConfirmacaoExclusaoEquipamento-view.fxml"));
            Parent root = loader.load();

            ConfirmacaoExclusaoEquipamentoController controller = loader.getController();

            // Passa o equipamento selecionado para a tela de confirmação
            controller.setEquipamento(selecionado);

            // Passa a função para recarregar a tabela se a exclusão for bem-sucedida
            controller.setOnAtualizarTabela(() -> carregarEquipamentos());

            Stage popupStage = new Stage();
            Stage donoDaJanela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();

            // Ativa o blur
            rootPrincipal.setEffect(new GaussianBlur(15));

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

            // Aguarda a interação do usuário no pop-up de confirmação
            popupStage.showAndWait();

            // Remove o blur quando finalizar
            rootPrincipal.setEffect(null);

        } catch (Exception e) {
            exibirPopupErro("Erro de Carregamento", "Não foi possível abrir a tela de confirmação de exclusão.");
            e.printStackTrace();
        }
    }

    public void exibirPopupErro(String titulo, String descricao) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/OperacaoInvalida-view.fxml"));
            Parent root = loader.load();

            OperacaoInvalidaController controller = loader.getController();
            controller.setMensagem(titulo, descricao);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);

            if (vboxGerenciarEquipamentos != null && vboxGerenciarEquipamentos.getScene() != null) {
                Stage donoDaJanela = (Stage) vboxGerenciarEquipamentos.getScene().getWindow();
                Parent rootPrincipal = donoDaJanela.getScene().getRoot();

                // Ativa o blur
                rootPrincipal.setEffect(new GaussianBlur(15));

                popupStage.initOwner(donoDaJanela);
                popupStage.centerOnScreen();

                // Espera o popup fechar
                popupStage.showAndWait();

                // Remove o blur
                rootPrincipal.setEffect(null);
            } else {
                popupStage.showAndWait();
            }

        } catch (Exception e) {
            System.err.println("Erro crítico ao abrir popup de erro: " + e.getMessage());
        }
    }

    @FXML
    private void onClickCancelar(ActionEvent event) {
        vboxCadastro.setVisible(false);
        vboxCadastro.setManaged(false);
        vboxBotoes.setVisible(true);
        vboxBotoes.setManaged(true);
    }
}
