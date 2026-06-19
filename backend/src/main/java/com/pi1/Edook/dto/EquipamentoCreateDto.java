package com.pi1.Edook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipamentoCreateDto {
    private String prefixo;
    private String descricao;
    private String tipo;
    private String cpfCadastro;
}
