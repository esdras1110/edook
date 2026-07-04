package com.pi1.Edook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FuncionarioResponseDto {
    private String nome;
    private String cpf;
    private String email;
    private String cargo;
    private Integer matricula;
}
