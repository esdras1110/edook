package com.pi1.Edook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pi1.Edook.model.Equipamento;
import com.pi1.Edook.model.EquipamentoId;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, EquipamentoId>{
    boolean existsById(EquipamentoId id);

    @Query("""
            SELECT MAX(e.id.numero)
            FROM Equipamento e 
            WHERE e.id.prefixo = :numero
            """)
    Short buscaMaximoId(String numero);
}
