package com.pi1.Edook.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipamentoUpdateDto {

    @NotBlank
    private String descricao;

    @NotBlank
    private String tipo;
}
