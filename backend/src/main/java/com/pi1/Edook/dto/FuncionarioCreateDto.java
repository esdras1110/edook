package com.pi1.Edook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioCreateDto {
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private String ddd;
    private String numero;
    private String cargo;
    private String matricula;
}
