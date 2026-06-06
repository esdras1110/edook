package com.pi1.Edook.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.pi1.Edook.dto.FuncionarioCreateDto;
import com.pi1.Edook.exception.BusinessException;
import com.pi1.Edook.model.Funcionario;
import com.pi1.Edook.repository.FuncionarioRepository;


@Service
public class FuncionarioService {
    private final FuncionarioRepository repository;
    private final BCryptPasswordEncoder encoder;

    public FuncionarioService(FuncionarioRepository repository, BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    private void validar(FuncionarioCreateDto dto){

        if (!dto.getCargo().equals("Docente")
                && !dto.getCargo().equals("Administrativo")) {
            throw new BusinessException(
                    "Cargo inválido",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(
                    "Email já cadastrado",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (repository.existsByMatricula(dto.getMatricula())) {
            throw new BusinessException(
                    "Matrícula já cadastrada",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public Funcionario criar(FuncionarioCreateDto dto){
        
        validar(dto);

        Funcionario f = new Funcionario();

        f.setNome(dto.getNome());
        f.setCpf(dto.getCpf());
        f.setEmail(dto.getEmail());
        f.setSenha(encoder.encode(dto.getSenha()));
        f.setDdd(dto.getDdd());
        f.setNumero(dto.getNumero());
        f.setCargo(dto.getCargo());
        f.setMatricula(dto.getMatricula());
        f.setEmailVerificado(false);
        String token = UUID.randomUUID().toString();

        f.setTokenVerificacao(token);
        f.setTokenExpiracao(LocalDateTime.now().plusHours(24));
        return repository.save(f);
    }
}
