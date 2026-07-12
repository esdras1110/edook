package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

// Controlador de modal genérico para exibir mensagens de erro
public class OperacaoInvalidaController {
    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblDescricao;

    //Recebe os dados da tela que causou o erro e atualiza a interface do popup
    public void setMensagem(String titulo, String descricao) {
        lblTitulo.setText(titulo);
        lblDescricao.setText(descricao);
    }

    // Fecha o popup de erro e devolve o controle para a tela que estava por baixo
    @FXML
    void onClickVoltar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
