package com.pi1.Edook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#_\\-])[A-Za-z\\d@$!%*?&.#_\\-]{6,}$",
        message = "A senha deve ter no mínimo 6 caracteres, uma letra maiúscula, uma minúscula, um número e um caractere especial"
    )
    private String senha;
    private String ddd;
    private String numero;
    private String cargo;
    private String matricula;
}
