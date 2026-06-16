package com.edook.frontend.controllers;

import com.edook.frontend.models.Equipamento;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class CadastroController implements Initializable {
    @FXML
    private VBox vboxBotoes;

    @FXML
    private VBox vboxCadastro;

    @FXML
    private VBox vboxGerenciarEquipamentos;

    @FXML
    private TextField campoNome;

    @FXML
    private TextField campoCPF;

    @FXML
    private TextField campoMatricula;

    @FXML
    private ComboBox<String> campoCargo;

    @FXML
    private TextField campoTelefone;

    @FXML
    private TextField campoEmail;

    @FXML
    private TextField campoSenha;

    @FXML
    private TextField campoConfirmacaoSenha;

    @FXML
    private Label lblErro;

    @FXML
    private TableView<Equipamento> tabelaEquipamentos;

    @FXML
    private TableColumn<Equipamento, String> colPrefixo;

    @FXML
    private TableColumn<Equipamento, String> colID;

    @FXML
    private TableColumn<Equipamento, String> colNome;

    @FXML
    private TableColumn<Equipamento, String> colTipo;

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

        inicializaTabela();
    }

    private void inicializaTabela(){
        tabelaEquipamentos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        colPrefixo.setCellValueFactory(new PropertyValueFactory<>("prefixo"));
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        Equipamento teste1 = new Equipamento("NT", "002", "Notebook Dell Inspiron", "Informática");
        Equipamento teste2 = new Equipamento("CX", "003", "Caixa de Som JBL", "Áudio");
        Equipamento teste3 = new Equipamento("PR", "004", "Projetor BenQ", "Vídeo");
        Equipamento teste4 = new Equipamento("TL", "005", "Smart TV LG 55'", "Vídeo");
        Equipamento teste5 = new Equipamento("NT", "006", "MacBook Air M2", "Informática");

        if (tabelaEquipamentos != null) {
            tabelaEquipamentos.getItems().addAll(teste1, teste2, teste3, teste4, teste5);
        }
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
    private void onClickCancelar(ActionEvent event) {
        vboxCadastro.setVisible(false);
        vboxCadastro.setManaged(false);
        vboxBotoes.setVisible(true);
        vboxBotoes.setManaged(true);
    }

    @FXML
    private void onClickCadastrar(ActionEvent event) { validarFormulario(); }
}
