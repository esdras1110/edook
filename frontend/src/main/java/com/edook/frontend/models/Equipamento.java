package com.edook.frontend.models;

public class Equipamento {
    private String prefixo;
    private String id;
    private String nome;
    private String tipo;

    public Equipamento(String prefixo, String id, String nome, String tipo) {
        this.prefixo = prefixo;
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
    }

    public String getPrefixo() { return prefixo; }
    public void setPrefixo(String prefixo) { this.prefixo = prefixo; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
