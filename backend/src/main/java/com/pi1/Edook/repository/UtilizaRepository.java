package com.pi1.Edook.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pi1.Edook.model.Utiliza;
import com.pi1.Edook.model.UtilizaId;

@Repository
public interface UtilizaRepository
        extends JpaRepository<Utiliza, UtilizaId> {

    @Query("""
        SELECT COUNT(u) > 0
        FROM Utiliza u
        WHERE
            u.equipamento.id.prefixo = :prefixo
            AND u.equipamento.id.numero = :numero
            AND u.reserva.dia = :dia
            AND u.reserva.status = 'Pendente'
            AND :inicio < u.reserva.horarioFim
            AND :fim > u.reserva.horarioInicio
    """)
    boolean existeConflito(
            @Param("prefixo") String prefixo,
            @Param("numero") Short numero,
            @Param("dia") LocalDate dia,
            @Param("inicio") LocalTime inicio,
            @Param("fim") LocalTime fim
    );


    // busca se o equipamento está sendo utilizando naquele intervalo
    @Query("""
        SELECT COUNT(u) > 0
        FROM Utiliza u
        WHERE
            u.equipamento.id.prefixo = :prefixo
            AND u.equipamento.id.numero = :numero
            AND u.reserva.status = 'Pendente'
    """)
    boolean existeReservaPendente(
            @Param("prefixo") String prefixo,
            @Param("numero") Short numero
    );

    List<Utiliza> findByReservaId(Integer reservaId);
}