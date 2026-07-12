package com.edook.frontend.session;

// Classe responsável por gerir a sessão do usuário autenticado
// Utiliza o Padrão de Projeto "Singleton", garante que a classe tenha apenas uma única instância durante o ciclo de vida da aplicação
public class UserSession {
    // A única instância da classe, guardada como estática (pertence à classe e não ao objeto)
    private static UserSession instance;

    // Atributos privados
    private String nome;
    private String email;
    private String cpf;
    private String cargo;
    private String token;

    // Construtor privado
    private UserSession() {}

    // Função principal do Singleton, verifica se a sessão já existe, se não existir, cria a primeira, se já existir, devolve a mesma instância para quem pediu
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Getters and Setters
    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }

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

    // Limpa todos os dados armazenados na memória, chamado durante o processo de "Logout" para evitar vulnerabilidades
    public void limparUserSession() {
        this.nome = null;
        this.email = null;
        this.cpf = null;
        this.cargo = null;
        this.token = null;
    }
}