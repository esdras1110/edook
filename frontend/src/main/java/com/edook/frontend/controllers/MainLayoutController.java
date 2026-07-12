package com.edook.frontend.controllers;

import com.edook.frontend.session.UserSession;
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

// Controlador do Layout Principal, gerencia a janela principal da aplicação, mantendo a barra lateral fixa
public class MainLayoutController {
    @FXML
    private Button btnInicio, btnReservas, btnCadastro, btnUsuario;

    // Lista com todos os botões
    private List<Button> botoesMenu;

    // O contêiner principal que divide a tela em regiões
    @FXML
    private BorderPane mainLayout;

    @FXML
    public void initialize() {
        botoesMenu = Arrays.asList(btnInicio, btnReservas, btnCadastro, btnUsuario);

        // Busca qual é o cargo do usuário que está logado no momento
        String cargoUsuario = UserSession.getInstance().getCargo();

        // Se o usuário for um professor (Docente), ele não deve ter acesso ao menu de Cadastros
        if ("DOCENTE".equalsIgnoreCase(cargoUsuario)) {
            btnCadastro.setVisible(false);
            btnCadastro.setManaged(false);
            System.out.println("Acesso Docente: Botão de Cadastro ocultado com sucesso.");
        }

        // Ao abrir o MainLayout, define a tela de Início como a visualização padrão no centro do layout
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/Inicio-view.fxml"));
            Parent inicioView = loader.load();

            mainLayout.setCenter(inicioView);

            destacarBotao(btnInicio); // Marca o botão de início como selecionado
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Atualiza a interface visual para dar um feedback ao usuário de qual menu está ativo
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

    // MÉTODOS DE NAVEGAÇÃO DO MENU LATERAL
    // Todos seguem o mesmo padrão: carregam o arquivo FXML da respectiva tela, injetam no centro do BorderPane
    // e atualizam a cor do botão.
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
    private void onClickUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/Usuario-view.fxml"));
            Parent UsuarioView = loader.load();

            mainLayout.setCenter(UsuarioView);

            destacarBotao(btnUsuario);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ação de logout, abre um popup sobreposto à tela atual pedindo a confirmação do usuário
    @FXML
    private void onClickSair(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/edook/frontend/Sair-view.fxml"));
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
}
