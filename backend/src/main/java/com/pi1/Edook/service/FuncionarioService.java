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

        // se cargo for diferente de docente e admonistrativo eu gero uma exceção
        if (!dto.getCargo().equals("Docente")
                && !dto.getCargo().equals("Administrativo")) {
            throw new BusinessException(
                    "Cargo inválido",
                    HttpStatus.BAD_REQUEST
            );
        }

        // checo se esse email ja foi cadastrado
        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(
                    "Email já cadastrado",
                    HttpStatus.BAD_REQUEST
            );
        }

        // checo se a matricula ja foi cadastrada
        if (repository.existsByMatricula(dto.getMatricula())) {
            throw new BusinessException(
                    "Matrícula já cadastrada",
                    HttpStatus.BAD_REQUEST
            );
        }

        // checo se o cpf ja foi cadastrado
        if (repository.existsByCpf(dto.getCpf())) {
            throw new BusinessException(
                    "CPF já cadastrado",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public Funcionario criar(FuncionarioCreateDto dto){
        // chama a função para validar as regras de negocio
        validar(dto);

        Funcionario f = new Funcionario();

        f.setNome(dto.getNome());
        f.setCpf(dto.getCpf());
        f.setEmail(dto.getEmail());
        // passo a minha senha ja criptografada
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
