package com.edook.frontend.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

// Controlador do modal Confirmar Cadastro de Equipamento
public class ConfirmacaoCadastroEquipamentoController {
    // Rótulos de texto da interface onde as mensagens serão exibidas
    @FXML
    private Label lblDescricao, lblTipo;

    // Variável que guarda uma ação recebida de outra tela
    private Runnable onConfirmar;

    // Recebe as mensagens da tela principal e preenche os textos
    public void setDados(String descricao, String tipo) {
        lblDescricao.setText(descricao);
        lblTipo.setText(tipo);
    }

    // Define qual será a ação executada caso o botão Confirmar seja clicado. Geralmente atualização de tabela, mas podendo ser
    // atualização da UserSession ou limpeza de campos em algumas outras situações.
    public void setOnConfirmar(Runnable onConfirmar) {
        this.onConfirmar = onConfirmar;
    }

    // Fecha o popup sem executar nenhuma ação no banco de dados.
    @FXML
    void onClickVoltar(ActionEvent event) {
        fecharJanela(event);
    }

    // Executa a rotina de salvamento
    @FXML
    void onClickConfirmar(ActionEvent event) {
        if (onConfirmar != null) {
            onConfirmar.run();
        }
        fecharJanela(event); // Fecha o popup após o envio
    }

    private void fecharJanela(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}