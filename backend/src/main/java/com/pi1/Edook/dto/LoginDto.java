package com.pi1.Edook.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
    // identificador é o que o usuario usar para se logar, cpf ou email, la no LoginService eu procuro tanto por cpf quanto email
    @NotBlank(message = "O CPF ou Email é obrigatório")
    private String identificador;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;
}