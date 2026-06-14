package com.edook.frontend.models;

public class Reserva {
    private String funcionario;
    private String titulo;
    private String data;
    private String horario;
    private String equipamento;
    private String local;
    private String status;

    public Reserva(String funcionario, String titulo, String data, String horario, String equipamento, String local, String status) {
        this.funcionario = funcionario;
        this.titulo = titulo;
        this.data = data;
        this.horario = horario;
        this.equipamento = equipamento;
        this.local = local;
        this.status = status;
    }

    public String getFuncionario() { return funcionario; }
    public void setFuncionario(String funcionario) { this.funcionario = funcionario; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getEquipamento() { return equipamento; }
    public void setEquipamento(String equipamento) { this.equipamento = equipamento; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
