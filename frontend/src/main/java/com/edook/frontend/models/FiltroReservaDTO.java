package com.edook.frontend.models;

import java.time.LocalDate;

// DTO (Data Transfer Object) para os dados de Filtro de Reservas
// EquipamentoResponseDTO detalhado
public class FiltroReservaDTO {
    public String equipamento;
    public String status;
    public LocalDate dataInicio;
    public LocalDate dataFim;
    public String horarioInicio;
    public String horarioFim;
    public String local;
}