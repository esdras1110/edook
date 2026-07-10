package com.pi1.Edook.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pi1.Edook.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    List<Reserva> findByStatus(String status);

    @Query("""
        SELECT r
        FROM Reserva r
        WHERE
            r.dia >= :hoje
        
        ORDER BY r.dia, r.horarioInicio
    """)
    List<Reserva> buscarProximasReservas(
        @Param("hoje") LocalDate hoje
    );

    @Modifying
    @Query("""
        DELETE FROM Reserva r
        WHERE
            r.dia <= :limite
            AND (r.status = 'Concluída' OR r.status = 'Cancelada')
    """)
    void excluirReservasAntigas(@Param("limite") LocalDate limite);

    boolean existsByFuncionarioCpfAndStatus(String cpf, String status);
}