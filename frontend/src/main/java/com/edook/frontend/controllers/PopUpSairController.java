package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

// Controlador do popup de confirmação de saída (Logout)
public class PopUpSairController {

    // Ação disparada caso o usuário desista de sair, fecha o popup e remove o desfoque da tela principal
    @FXML
    void onClickCancelar(ActionEvent event) {
        Stage popupStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Stage mainStage = (Stage) popupStage.getOwner();

        if (mainStage != null && mainStage.getScene() != null) {
            Parent rootPrincipal = mainStage.getScene().getRoot();
            rootPrincipal.setEffect(null);
        }

        popupStage.close();
    }

    // Ação disparada ao confirmar a saída
    @FXML
    void onClickSairAgora(ActionEvent event) {
        try {
            Stage popupStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage mainStage = (Stage) popupStage.getOwner();

            // Carrega e configura a nova janela de Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/Login-view.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("edook - Login");

            Scene scene = new Scene(root, 1440, 1024);
            scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());

            loginStage.setScene(scene);
            loginStage.show();

            popupStage.close();
            if (mainStage != null) {
                mainStage.close();
            }

            // Limpa a sessão do usuário
            com.edook.frontend.session.UserSession.getInstance().limparUserSession();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao redirecionar para a tela de login: " + e.getMessage());
        }
    }
}