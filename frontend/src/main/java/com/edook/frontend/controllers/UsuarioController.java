package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class UsuarioController implements Initializable {

    @FXML
    private VBox vboxDados;

    @FXML
    private VBox vboxEdicao;

    @FXML
    private Label lblErro;

    @FXML
    private TextField campoNome;

    @FXML
    private TextField campoCPF;

    @FXML
    private TextField campoMatricula;

    @FXML
    private TextField campoTelefone;

    @FXML
    private TextField campoEmail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aplicarMascaraTelefone(campoTelefone);
        aplicarMascaraCPF(campoCPF);
        aplicarMascaraMatricula(campoMatricula);

    }

    private boolean validarFormulario() {
        String nome = campoNome.getText().trim();
        String cpf = campoCPF.getText().replaceAll("[^0-9]", "");
        String telefone = campoTelefone.getText().replaceAll("[^0-9]", "");
        String matricula = campoMatricula.getText().trim();
        String email = campoEmail.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty() || matricula.isEmpty() || telefone.isEmpty() || email.isEmpty()) {
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
    private void onClickAtualizar(ActionEvent event) {
        validarFormulario();
    }
}
