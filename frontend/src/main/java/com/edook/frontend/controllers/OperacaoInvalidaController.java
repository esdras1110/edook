package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class OperacaoInvalidaController {
    @FXML
    private Label lblTitulo;

    @FXML
    private Label lblDescricao;

    public void setMensagem(String titulo, String descricao) {
        lblTitulo.setText(titulo);
        lblDescricao.setText(descricao);
    }

    @FXML
    void onClickVoltar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
