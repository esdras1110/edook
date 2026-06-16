package com.edook.frontend.controllers;

import com.edook.frontend.session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class LoginController {
    @FXML
    private TextField campoTextLogin;

    @FXML
    private PasswordField campoSenhaLogin;

    @FXML
    private TextField campoTextRecuperacaoSenha;

    @FXML
    private TextField campoTextCodigo;

    @FXML
    private TextField campoTextNovaSenha;

    @FXML
    private TextField campoTextConfirmarNovaSenha;

    @FXML
    private Label labelErroLogin;

    @FXML
    private Label labelErroEmail;

    @FXML
    private Label labelErroCodigo;

    @FXML
    private Label labelErroSenha;

    @FXML
    private VBox vboxLogin;

    @FXML
    private VBox vboxEsqueceuSenha;

    @FXML
    private VBox vboxCodigo;

    @FXML
    private VBox vboxNovaSenha;

    private String emailTemporario;

    private String codigo;

    @FXML
    void onClickEntrar(ActionEvent event) {
        String emailCPF = campoTextLogin.getText().trim();
        String senha = campoSenhaLogin.getText().trim();

        labelErroLogin.setText("");

        if (emailCPF.isEmpty() || senha.isEmpty()) {
            labelErroLogin.setText("Por favor, preencha todos os campos obrigatórios.");
            labelErroLogin.setStyle("-fx-text-fill: red;");
            return;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!emailCPF.matches(emailRegex)) {
            labelErroLogin.setText("Por favor, insira um e-mail válido ou CPF.");
            labelErroLogin.setStyle("-fx-text-fill: red;");
            return;
        }

        if (senha.length() < 6) {
            labelErroLogin.setText("A senha deve conter pelo menos 6 caracteres.");
            labelErroLogin.setStyle("-fx-text-fill: red;");
            return;
        }

        labelErroLogin.setText("Enviando dados...");
        labelErroLogin.setStyle("-fx-text-fill: green;");

//        UserSession.getInstance().setEmail(emailDoBanco);
//        UserSession.getInstance().setCpf(cpfDoBanco);
//        UserSession.getInstance().setCargo(cargoDoBanco);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/MainLayout-view.fxml"));
            Parent root = loader.load();
            Stage mainStage = new Stage();
            mainStage.setTitle("edook - Sistema de Reserva de Equipamentos");
            Scene scene = new Scene(root, 1440, 1024);
            scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());
            mainStage.setScene(scene);
            mainStage.show();
            Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar a tela principal: " + e.getMessage());
        }
    }

    @FXML
    void onClickEsqueceuSenha(ActionEvent event) {
        vboxLogin.setVisible(false);
        vboxLogin.setManaged(false);
        vboxEsqueceuSenha.setVisible(true);
        vboxEsqueceuSenha.setManaged(true);
    }

    @FXML
    void onClickEnviar(ActionEvent event) {
        String email = campoTextRecuperacaoSenha.getText().trim();

        labelErroEmail.setText("");

        if (email.isEmpty()) {
            labelErroEmail.setText("Por favor, preencha o campo obrigatório.");
            labelErroEmail.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!email.contains("@")) {
            labelErroEmail.setText("Por favor, insira um e-mail válido.");
            labelErroEmail.setStyle("-fx-text-fill: red;");
            return;
        }

        this.emailTemporario = email;

        Random gerador = new Random();
        this.codigo = String.valueOf(gerador.nextInt(900000) + 100000);

        //chamar função de enviar email

        vboxEsqueceuSenha.setVisible(false);
        vboxEsqueceuSenha.setManaged(false);
        vboxCodigo.setVisible(true);
        vboxCodigo.setManaged(true);
    }

    @FXML
    void onClickVoltarLogin(ActionEvent event) {
        vboxEsqueceuSenha.setVisible(false);
        vboxEsqueceuSenha.setManaged(false);
        vboxLogin.setVisible(true);
        vboxLogin.setManaged(true);
    }

    @FXML
    void onClickVerificar(ActionEvent event) {
        String codigoUsuario = campoTextCodigo.getText().trim();

        if(!codigo.equals(codigoUsuario)){
            labelErroCodigo.setText("Código Inválido.");
            labelErroCodigo.setStyle("-fx-text-fill: red;");
            return;
        }

        vboxCodigo.setVisible(false);
        vboxCodigo.setManaged(false);
        vboxNovaSenha.setVisible(true);
        vboxNovaSenha.setManaged(true);
    }

    @FXML
    void onClickVoltarEsqueceuSenha(ActionEvent event) {
        vboxCodigo.setVisible(false);
        vboxCodigo.setManaged(false);
        vboxEsqueceuSenha.setVisible(true);
        vboxEsqueceuSenha.setManaged(true);
    }

}
