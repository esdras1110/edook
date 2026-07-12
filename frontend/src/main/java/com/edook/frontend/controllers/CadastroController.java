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

// Controlador da tela de cadastro e gerenciamentos gerais.
public class CadastroController implements Initializable {
    // Injeção dos elementos visuais mapeados no arquivo FXML
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

    // Listas para gerenciar os dados da tabela de equipamentos e o filtro de pesquisa
    private final ObservableList<EquipamentoResponseDTO> listaEquipamentos = FXCollections.observableArrayList();
    private FilteredList<EquipamentoResponseDTO> listaFiltrada;

    // Função inicial, prepara as configurações iniciais, como máscaras, colunas da tabela e a busca.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Aplica as restrições de digitação aos campos de cadastro de usuário
        aplicarMascaraTelefone(campoTelefone);
        aplicarMascaraCPF(campoCPF);
        aplicarMascaraMatricula(campoMatricula);
        campoCargo.getItems().addAll("Docente", "Administrativo");
        campoCargo.getSelectionModel().select("Docente");

        // Vincula as colunas da tabela aos atributos do DTO de Equipamento
        colPrefixo.setCellValueFactory(new PropertyValueFactory<>("prefixo"));
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        // Customiza a coluna "Descrição" para exibir um balãozinho caso o texto seja muito grande e acabe cortado na tabela.
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

        // Configura a lista filtrada conectada à lista original e a insere na tabela
        listaFiltrada = new FilteredList<>(listaEquipamentos, b -> true);
        tabelaEquipamentos.setItems(listaFiltrada);

        // Popula a tabela
        carregarEquipamentos();

        // Adiciona um "ouvinte" no campo de busca para filtrar a tabela a cada letra digitada
        campoBusca.textProperty().addListener((observable, oldValue, newValue) -> {
            atualizarBusca();
        });
    }

    // Busca os equipamentos do banco de dados chamando a API do backend.
    protected void carregarEquipamentos() {
        String url = "http://localhost:8080/equipamentos";

        // Cria o cliente e a requisição HTTP GET
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        // Envia a requisição de forma assíncrona para não travar a tela
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    try {
                        // Converte a resposta JSON em uma lista de objetos Java
                        ObjectMapper mapper = new ObjectMapper();
                        List<EquipamentoResponseDTO> dtos = mapper.readValue(
                                response.body(),
                                new TypeReference<List<EquipamentoResponseDTO>>(){}
                        );

                        // Atualiza a interface gráfica
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

    // Filtra a tabela verificando se o texto digitado bate com a descrição ou tipo do equipamento.
    private void atualizarBusca() {
        String textoBusca = campoBusca.getText() == null ? "" : campoBusca.getText().toLowerCase();

        listaFiltrada.setPredicate(reserva -> {
            boolean passaBusca = true;
            if (!textoBusca.isEmpty()) {
                passaBusca = (reserva.getDescricao() != null && reserva.getDescricao().toLowerCase().contains(textoBusca)) ||
                        (reserva.getTipo() != null && reserva.getTipo().toLowerCase().contains(textoBusca));
            }

            return passaBusca;
        });
    }

    // Verifica se os dados inseridos pelo usuário estão corretos antes de salvar.
    private boolean validarFormulario() {
        // Recebe todos os campos limpando as máscaras
        String nome = campoNome.getText().trim();
        String cpf = campoCPF.getText().replaceAll("[^0-9]", "");
        String telefone = campoTelefone.getText().replaceAll("[^0-9]", "");
        String matricula = campoMatricula.getText().trim();
        String cargo = campoCargo.getValue();
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText().trim();
        String confirmacaoSenha = campoConfirmacaoSenha.getText().trim();

        // Checa campos vazios
        if (nome.isEmpty() || cpf.isEmpty() || matricula.isEmpty() || cargo.isEmpty() || telefone.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmacaoSenha.isEmpty()) {
            lblErro.setText("Todos os campos devem ser preenchidos!");
            lblErro.setStyle("-fx-text-fill: red;");
            return false;
        }

        // Validações de tamanho específico
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

        // Validações com Regex para formato de email e senha
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

    // Impede digitação de letras e formata o telefone dinamicamente para o padrão (XX) XXXXX-XXXX
    private void aplicarMascaraTelefone(TextField textField) {
        // O TextFormatter analisa cada tecla pressionada e formata o texto em tempo real
        UnaryOperator<TextFormatter.Change> filter = change -> {
            // Permite apagar texto normalmente
            if (change.isDeleted() || change.getText().isEmpty()) {
                return change;
            }

            // Bloqueia qualquer caractere que não seja número
            if (!change.getText().matches("[0-9]*")) {
                return null;
            }
            String novoTexto = change.getControlNewText().replaceAll("[^0-9]", "");

            // Limite máximo de números
            if (novoTexto.length() > 11) {
                return null;
            }

            // Constrói a máscara gradualmente
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

            // Atualiza o texto final e reposiciona o cursor
            change.setRange(0, change.getControlText().length());
            change.setText(sb.toString());
            change.setCaretPosition(sb.length());
            change.setAnchor(sb.length());

            return change;
        };

        textField.setTextFormatter(new TextFormatter<>(filter));
    }

    // A máscara de CPF segue exatamente a mesma lógica de formatação de string do telefone
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

    // A máscara de matrícula apenas bloqueia letras e limita o tamanho para 7 dígitos
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

    // Controla a troca de abas escondendo/mostrando as VBox
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
            return; // Interrompe o fluxo se houver erro na validação de cadastro de usuário
        }

        // Monta o objeto que será enviado
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
            // Abre o popup de confirmação de cadastro
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/ConfirmacaoCadastroUsuario-view.fxml"));
            Parent root = loader.load();

            ConfirmacaoCadastroUsuarioController controller = loader.getController();
            controller.setDados(novoFuncionario);
            // Passa uma função para limpar os campos caso a confirmação dê certo
            controller.setOnSucesso(() -> limparCampos());

            Node sourceNode = (Node) event.getSource();
            Stage popupStage = new Stage();
            Stage telaPrincipal = (Stage) sourceNode.getScene().getWindow();
            Parent rootPrincipal = telaPrincipal.getScene().getRoot();

            // Aplica efeito de desfoque (Blur) no fundo
            rootPrincipal.setEffect(new GaussianBlur(15));

            popupStage.initOwner(telaPrincipal);
            popupStage.initModality(Modality.WINDOW_MODAL); // Impede o clique fora do popup
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            if (getClass().getResource("/com/edook/frontend/style.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());
            }

            popupStage.setScene(scene);
            popupStage.centerOnScreen();

            // showAndWait pausa o código aqui até que a janela do popup seja fechada
            popupStage.showAndWait();

            // Remove o desfoque ao fechar o popup
            rootPrincipal.setEffect(null);
        } catch (Exception e) {
            e.printStackTrace();
            exibirPopupErro("Erro de Tela", "Não foi possível abrir a tela de confirmação.");
        }
    }
    // Os métodos de onClickExcluirUsuario, onClickCadastrarEquipamento onClickEditarEquipamento seguem exatamente
    // a mesma lógica de desfoque e popup descrita acima

    // Limpa os campos após cadastro de usuário
    private void limparCampos() {
        campoNome.clear();
        campoCPF.clear();
        campoMatricula.clear();
        campoTelefone.clear();
        campoEmail.clear();
        campoCargo.getSelectionModel().clearSelection();
        campoSenha.clear();
        campoConfirmacaoSenha.clear();
    }


    @FXML
    private void onClickExcluirUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/ExclusaoUsuario-view.fxml"));
            Parent root = loader.load();

            ExclusaoUsuarioController controller = loader.getController();

            controller.setOnExclusaoSucesso(() -> {
                System.out.println("Fluxo de exclusão de usuário concluído com sucesso!");
            });

            Stage popupStage = new Stage();
            Stage donoDaJanela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();

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

            popupStage.showAndWait();

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

            controller.setOnCadastroSucesso(() -> carregarEquipamentos());

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.TRANSPARENT);

            Stage donoDaJanela = (Stage) tabelaEquipamentos.getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();
            rootPrincipal.setEffect(new GaussianBlur(15));

            popupStage.initOwner(donoDaJanela);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            popupStage.setScene(scene);
            popupStage.centerOnScreen();

            popupStage.showAndWait();
            rootPrincipal.setEffect(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Controla a troca de abas escondendo/mostrando as VBox
    @FXML
    private void onClickGerenciarEquipamento(ActionEvent event) {
        vboxBotoes.setVisible(false);
        vboxBotoes.setManaged(false);
        vboxGerenciarEquipamentos.setVisible(true);
        vboxGerenciarEquipamentos.setManaged(true);
    }

    // Controla a troca de abas escondendo/mostrando as VBox
    @FXML
    private void onClickVoltar(ActionEvent event) {
        vboxGerenciarEquipamentos.setVisible(false);
        vboxGerenciarEquipamentos.setManaged(false);
        vboxBotoes.setVisible(true);
        vboxBotoes.setManaged(true);
    }

    @FXML
    private void onClickEditarEquipamento(ActionEvent event) {
        // Cria uma lista observável contendo os itens que o usuário selecionou
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

            controller.setEquipamento(selecionado);
            controller.setOnEdicaoSucesso(() -> carregarEquipamentos());

            Stage popupStage = new Stage();
            Stage donoDaJanela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();

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

            popupStage.showAndWait();

            rootPrincipal.setEffect(null);

        } catch (Exception e) {
            exibirPopupErro("Erro de Carregamento", "Não foi possível abrir a tela de edição.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onClickExcluirEquipamento(ActionEvent event) {
        // Cria uma lista observável contendo os itens que o usuário selecionou
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

            controller.setEquipamento(selecionado);

            controller.setOnAtualizarTabela(() -> carregarEquipamentos());

            Stage popupStage = new Stage();
            Stage donoDaJanela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();

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

            popupStage.showAndWait();

            rootPrincipal.setEffect(null);

        } catch (Exception e) {
            exibirPopupErro("Erro de Carregamento", "Não foi possível abrir a tela de confirmação de exclusão.");
            e.printStackTrace();
        }
    }

    // Função genérico para exibir mensagens de erro
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

                rootPrincipal.setEffect(new GaussianBlur(15));

                popupStage.initOwner(donoDaJanela);
                popupStage.centerOnScreen();

                popupStage.showAndWait();

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
