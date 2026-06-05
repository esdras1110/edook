package com.pi1.Edook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "funcionario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {
    @Column(unique = true)
    private String matricula;
    @Id
    @Pattern(regexp = "\\d{11}")
    private String cpf;
    @NotBlank
    private String nome;
    @NotBlank
    @Email
    private String email;
    @Pattern(regexp = "\\d{2}")
    private String ddd;
    @Pattern(regexp = "\\d{8,9}")
    private String numero;
    @Column(nullable = false)
    private String cargo;
    @Column(nullable = false)
    private String senha;
    private String cpf_cadastro;
    private boolean email_verificado;
}
