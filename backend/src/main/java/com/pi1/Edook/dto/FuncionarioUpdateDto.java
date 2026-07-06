package com.pi1.Edook.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioUpdateDto {

    private String nome;

    @Pattern(regexp = "\\d{2}", message = "DDD deve ter 2 dígitos")
    private String ddd;
    @Pattern(regexp = "\\d{8,9}", message = "Número inválido")
    private String numero;
}
