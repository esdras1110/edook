package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainLayoutController {
    @FXML
    private Button btnInicio;

    @FXML
    private Button btnReservas;

    @FXML
    private Button btnCadastro;

    @FXML
    private Button btnUsuario;

    private List<Button> botoesMenu;

    @FXML
    private BorderPane mainLayout;

    @FXML
    public void initialize() {
        botoesMenu = Arrays.asList(btnInicio, btnReservas, btnCadastro, btnUsuario);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/Inicio-view.fxml"));
            Parent inicioView = loader.load();

            mainLayout.setCenter(inicioView);

            destacarBotao(btnInicio);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destacarBotao(Button botaoAtivo) {
        for (Button btn : botoesMenu) {
            if (btn == botaoAtivo) {
                if (!btn.getStyleClass().contains("botao-navbar-ativo")) {
                    btn.getStyleClass().add("botao-navbar-ativo");
                }
            } else {
                btn.getStyleClass().remove("botao-navbar-ativo");
            }
        }
    }

    @FXML
    private void onClickInicio(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/Inicio-view.fxml"));
            Parent inicioView = loader.load();

            mainLayout.setCenter(inicioView);

            destacarBotao(btnInicio);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickReservas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/Reservas-view.fxml"));
            Parent ReservasView = loader.load();

            mainLayout.setCenter(ReservasView);

            destacarBotao(btnReservas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickCadastro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/Cadastro-view.fxml"));
            Parent CadastroView = loader.load();

            mainLayout.setCenter(CadastroView);

            destacarBotao(btnCadastro);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClickSair(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/PopUpSair-view.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            Stage donoDaJanela = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent rootPrincipal = donoDaJanela.getScene().getRoot();

            GaussianBlur blur = new GaussianBlur(15);
            rootPrincipal.setEffect(blur);

            popupStage.initOwner(donoDaJanela);
            popupStage.initModality(Modality.WINDOW_MODAL);

            popupStage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root);

            scene.setFill(Color.TRANSPARENT);

            scene.getStylesheets().add(getClass().getResource("/com/edook/frontend/style.css").toExternalForm());

            popupStage.setScene(scene);
            popupStage.centerOnScreen();
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar o pop-up de saída: " + e.getMessage());
        }
    }

    @FXML
    private void onClickUsuario(ActionEvent event) {
        destacarBotao(btnUsuario);
        // carregarPagina("/views/Usuario.fxml");
    }
}
