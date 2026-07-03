package com.pi1.Edook.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import com.pi1.Edook.model.Equipamento;
import com.pi1.Edook.model.EquipamentoId;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, EquipamentoId>{
    boolean existsById(EquipamentoId id);

    @Query("""
            SELECT MAX(e.id.numero)
            FROM Equipamento e 
            WHERE e.id.prefixo = :prefixo
            """)
    Short buscaMaximoId(@Param("prefixo") String prefixo);

    @Query("""
        SELECT e
        FROM Equipamento e
        ORDER BY e.id.prefixo ASC, e.id.numero ASC
    """)
    List<Equipamento> listarOrdenados();
}
