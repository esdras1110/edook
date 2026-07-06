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
    private VBox vboxDados, vboxEdicao;

    @FXML
    private Label lblErro, lblNome, lblCPF, lblCargo, lblMatricula, lblEmail, lblTelefone;

    @FXML
    private TextField campoNome, campoTelefone, campoEmail;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        aplicarMascaraTelefone(campoTelefone);
    }

    private boolean validarFormulario() {
        String nome = campoNome.getText().trim();
        String telefone = campoTelefone.getText().replaceAll("[^0-9]", "");
        String email = campoEmail.getText().trim();

        if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty()) {
            lblErro.setText("Todos os campos devem ser preenchidos!");
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
