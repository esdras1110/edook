package com.edook.frontend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipamentoResponseDTO {
    private String prefixo;
    private Integer numero;
    private String descricao;
    private String tipo;

    public EquipamentoResponseDTO() {}

    public String getPrefixo() { return prefixo; }
    public void setPrefixo(String prefixo) { this.prefixo = prefixo; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}