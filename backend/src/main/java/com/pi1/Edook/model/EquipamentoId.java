package com.pi1.Edook.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipamentoId implements Serializable{
    @Column(length = 2)
    private String prefixo;
    private Short numero;
}