package com.pi1.Edook.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "equipamento")
@Getter
@Setter
@NoArgsConstructor
public class Equipamento {
    @EmbeddedId
    private EquipamentoId id;

    @Column(length = 100)
    private String descricao;

    @Column(length = 100)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "cpf_cadastro", referencedColumnName = "cpf")
    private Funcionario funcionario;
}
