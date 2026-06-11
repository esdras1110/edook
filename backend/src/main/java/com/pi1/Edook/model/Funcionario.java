package com.pi1.Edook.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// indica que a classe representa uma tabela no banco
@Entity
// nome da tabela no banco
@Table(name = "funcionario")
// cria os metodos de getter para cada atributo
@Getter
// cria os metodos de setter para cada atributo
@Setter
// cria um construtor vazio
@NoArgsConstructor
// cria um construtor com todos os atributos
@AllArgsConstructor
public class Funcionario {

    // indica qual a chave primaria do banco
    @Id
    @Column(length = 11)
    private String cpf;

    // não permite valor nulo e precisa ser unico
    @Column(unique = true, nullable = false)
    private String matricula;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 2)
    private String ddd;

    @Column(length = 9)
    private String numero;

    @Column(nullable = false)
    private String cargo;

    @Column(nullable = false)
    private String senha;

    private boolean emailVerificado;

    private String tokenVerificacao;

    private LocalDateTime tokenExpiracao;
}
