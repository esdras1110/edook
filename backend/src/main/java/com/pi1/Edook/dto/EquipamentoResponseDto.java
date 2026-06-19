package com.pi1.Edook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EquipamentoResponseDto {

    private String prefixo;
    private Short numero;
    private String descricao;
    private String tipo;
    private String cpfCadastro;
}
