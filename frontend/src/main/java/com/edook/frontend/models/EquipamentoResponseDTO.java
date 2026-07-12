package com.edook.frontend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// DTO (Data Transfer Object) para os dados de Equipamento
// Notação da biblioteca Jackson. ignora silenciosamente qualquer campo extra que venha do backend e que não esteja declarado nesta classe
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipamentoResponseDTO {
    // Atributos privados
    private String prefixo;
    private Integer numero;
    private String descricao;
    private String tipo;

    // Construtor vazio padrão
    public EquipamentoResponseDTO() {}

    // Getters e Setters
    public String getPrefixo() { return prefixo; }
    public void setPrefixo(String prefixo) { this.prefixo = prefixo; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}