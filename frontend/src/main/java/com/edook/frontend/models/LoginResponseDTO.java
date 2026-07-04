package com.edook.frontend.models;

public class LoginResponseDTO {
    private String nome;
    private String cpf;
    private String cargo;
    private String token;

    public LoginResponseDTO() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}