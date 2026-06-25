package com.pi1.Edook.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDto {

    private Integer id;
    private String nome;
    private String localidade;
    private LocalDate dia;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private String status;
    private String cpfFuncionario;

    private List<EquipamentoResponseDto> equipamentos;
}
