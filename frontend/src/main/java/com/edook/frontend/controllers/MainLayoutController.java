package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

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
        destacarBotao(btnInicio);
        // carregarPagina("/views/Inicio.fxml");
    }

    @FXML
    private void onClickReservas(ActionEvent event) {
        destacarBotao(btnReservas);
        // carregarPagina("/views/Reservas.fxml");
    }

    @FXML
    private void onClickCadastro(ActionEvent event) {
        destacarBotao(btnCadastro);
        // carregarPagina("/views/Cadastro.fxml");
    }

    @FXML
    private void onClickUsuario(ActionEvent event) {
        destacarBotao(btnUsuario);
        // carregarPagina("/views/Usuario.fxml");
    }
}
