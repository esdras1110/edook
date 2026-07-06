package com.pi1.Edook.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pi1.Edook.model.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, String> {
    boolean existsByEmail(String email);   
    boolean existsByMatricula(Integer matricula);   
    boolean existsByCpf(String cpf);
    boolean existsByEmailAndEmailVerificadoTrue(String email);
    Funcionario findByCodigoVerificacao(String token);
    Funcionario findByEmail(String email);
    Funcionario findByCpf(String cpf);
    Funcionario findByMatricula(Integer matricula);

    @Query("""
        SELECT COUNT(r) > 0
        FROM Reserva r
        WHERE r.funcionario.cpf = :cpf
        AND r.status = "Pendente"
    """)
    boolean existeReservaPendente(
        @Param("cpf") String cpf
    );
}
