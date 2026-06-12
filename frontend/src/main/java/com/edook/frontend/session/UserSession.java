package com.edook.frontend.session;

public class UserSession {
    private static UserSession instance;

    private String email;
    private String cpf;
    private String cargo;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void limparUserSession() {
        this.email = null;
        this.cpf = null;
        this.cargo = null;
    }
}