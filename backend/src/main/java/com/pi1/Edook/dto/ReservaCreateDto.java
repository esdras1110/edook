package com.pi1.Edook.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservaCreateDto {

    @NotBlank
    private String nome;
    @NotBlank
    private String localidade;

    @NotNull
    private LocalDate dia;

    @NotNull
    private LocalTime horarioInicio;
    @NotNull
    private LocalTime horarioFim;

    @NotBlank
    private String cpfFuncionario;

    @NotEmpty(message = "A reserva deve possuir ao menos um equipamento")
    @Valid
    private List<EquipamentoReservaDto> equipamentos;
}
