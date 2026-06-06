package com.pi1.Edook.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pi1.Edook.model.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, String> {
    boolean existsByEmail(String email);   
    boolean existsByMatricula(String matricula);   
    boolean existsByCpf(String cpf);
    Funcionario findByTokenVerificacao(String token);
}
