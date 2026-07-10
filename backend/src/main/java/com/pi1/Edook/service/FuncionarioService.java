package com.pi1.Edook.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.pi1.Edook.dto.FuncionarioCreateDto;
import com.pi1.Edook.dto.FuncionarioUpdateDto;
import com.pi1.Edook.exception.BusinessException;
import com.pi1.Edook.model.Funcionario;
import com.pi1.Edook.repository.FuncionarioRepository;
import com.pi1.Edook.repository.ReservaRepository;


@Service
public class FuncionarioService {
    private final FuncionarioRepository funcionarioRepository;
    private final ReservaRepository reservaRepository;
    private final BCryptPasswordEncoder encoder;

    public FuncionarioService(FuncionarioRepository repository, ReservaRepository reservaRepository, BCryptPasswordEncoder encoder) {
        this.funcionarioRepository = repository;
        this.reservaRepository = reservaRepository;
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
        if (funcionarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(
                    "Email já cadastrado",
                    HttpStatus.BAD_REQUEST
            );
        }

        // checo se a matricula ja foi cadastrada
        if (funcionarioRepository.existsByMatricula(dto.getMatricula())) {
            throw new BusinessException(
                    "Matrícula já cadastrada",
                    HttpStatus.BAD_REQUEST
            );
        }

        // checo se o cpf ja foi cadastrado
        if (funcionarioRepository.existsByCpf(dto.getCpf())) {
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

        f.setCodigoVerificacao(dto.getCodigoVerificacao());
        f.setCodigoExpiracao(LocalDateTime.now().plusHours(24));
        return funcionarioRepository.save(f);
    }

    public Funcionario buscar(String identificador){
        //buscando o funcionario a partir do cpf e email, primeiro por cpf
        Funcionario funcionario;
        if (identificador.matches("\\d{11}")) {
            funcionario = funcionarioRepository.findByCpf(identificador);
        } else if (identificador.matches("\\d+")) {
            Integer matricula = Integer.valueOf(identificador);
            funcionario = funcionarioRepository.findByMatricula(matricula);
        } else {
            throw new BusinessException(
                "CPF ou matrícula inválidos",
                HttpStatus.BAD_REQUEST
            );
        }

        //se nao existir tal funcionario com tal cpf ou matricula, então da erro
        if (funcionario == null) {
            throw new BusinessException(
                    "Funcionário não encontrado",
                    HttpStatus.NOT_FOUND
            );
        }

        return funcionario;
    }

    public Funcionario atualizar(String cpf, FuncionarioUpdateDto dto) {

        Funcionario funcionario = funcionarioRepository.findById(cpf)
                .orElseThrow(() -> new BusinessException(
                        "Funcionário não encontrado",
                        HttpStatus.NOT_FOUND
                ));

        if (dto.getNome() != null) {
            funcionario.setNome(dto.getNome());
        }

        if (dto.getDdd() != null) {
            funcionario.setDdd(dto.getDdd());
        }

        if (dto.getNumero() != null) {
            funcionario.setNumero(dto.getNumero());
        }

        return funcionarioRepository.save(funcionario);
    }

    public void excluir(String cpf) {

        Funcionario funcionario = funcionarioRepository.findById(cpf)
            .orElseThrow(() -> new BusinessException(
                "Funcionário não encontrado",
                HttpStatus.NOT_FOUND
            ));

        if (reservaRepository.existsByFuncionarioCpfAndStatus(cpf, "Pendente")) {
            throw new BusinessException(
                "Não é possível excluir um funcionário com reservas pendentes",
                HttpStatus.BAD_REQUEST
            );
        }

        funcionarioRepository.delete(funcionario);
    }
}
