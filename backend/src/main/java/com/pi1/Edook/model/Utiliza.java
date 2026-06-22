package com.pi1.Edook.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "utiliza")
@Getter
@Setter
@NoArgsConstructor
public class Utiliza {

    @EmbeddedId
    private UtilizaId id;

    @ManyToOne
    @MapsId("idReserva")
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(
            name = "prefixo_equipamento",
            referencedColumnName = "prefixo",
            insertable = false,
            updatable = false
        ),
        @JoinColumn(
            name = "numero_equipamento",
            referencedColumnName = "numero",
            insertable = false,
            updatable = false
        )
    })
    private Equipamento equipamento;
}
