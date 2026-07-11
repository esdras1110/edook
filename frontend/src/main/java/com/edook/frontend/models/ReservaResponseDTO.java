package com.edook.frontend.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservaResponseDTO {

    private Integer id;
    private String nome;
    private String localidade;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dia;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horarioInicio;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime horarioFim;

    private String status;
    private String nomeFuncionario;
    private List<EquipamentoResponseDTO> equipamentos;

    public ReservaResponseDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLocalidade() { return localidade; }
    public void setLocalidade(String localidade) { this.localidade = localidade; }

    public LocalDate getDia() { return dia; }
    public void setDia(LocalDate dia) { this.dia = dia; }

    public LocalTime getHorarioInicio() { return horarioInicio; }
    public void setHorarioInicio(LocalTime horarioInicio) { this.horarioInicio = horarioInicio; }

    public LocalTime getHorarioFim() { return horarioFim; }
    public void setHorarioFim(LocalTime horarioFim) { this.horarioFim = horarioFim; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNomeFuncionario() { return nomeFuncionario; }
    public void setNomeFuncionario(String nomeFuncionario) { this.nomeFuncionario = nomeFuncionario; }

    public List<EquipamentoResponseDTO> getEquipamentos() { return equipamentos; }
    public void setEquipamentos(List<EquipamentoResponseDTO> equipamentos) { this.equipamentos = equipamentos; }


    public String getDataFormatada() {
        return dia != null ? dia.toString() : "";
    }

    public String getHorarioFormatado() {
        if (horarioInicio != null && horarioFim != null) {
            String inicio = horarioInicio.toString().substring(0, 5);
            String fim = horarioFim.toString().substring(0, 5);
            return inicio + " - " + fim;
        }
        return "";
    }

    public String getEquipamentosFormatados() {
        if (equipamentos == null || equipamentos.isEmpty()) return "Nenhum";
        return equipamentos.stream()
                .map(EquipamentoResponseDTO::getDescricao)
                .collect(java.util.stream.Collectors.joining(", "));
    }
}