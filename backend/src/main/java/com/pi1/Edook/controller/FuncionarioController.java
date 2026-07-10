package com.pi1.Edook.controller;

import com.pi1.Edook.model.Funcionario;
import com.pi1.Edook.repository.FuncionarioRepository;
import com.pi1.Edook.dto.FuncionarioCreateDto;
import com.pi1.Edook.dto.FuncionarioResponseDto;
import com.pi1.Edook.dto.FuncionarioUpdateDto;
import com.pi1.Edook.dto.ReenviarConfirmacaoDto;
import com.pi1.Edook.service.EmailService;
import com.pi1.Edook.service.FuncionarioService;

import jakarta.validation.Valid;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService serviceFuncionario;
    private final EmailService serviceEmail;
    private final FuncionarioRepository repository;

    public FuncionarioController(FuncionarioService serviceFuncionario, FuncionarioRepository repository,
        EmailService serviceEmail) {
        this.serviceFuncionario = serviceFuncionario;
        this.serviceEmail = serviceEmail;
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<FuncionarioResponseDto> criar(@Valid @RequestBody FuncionarioCreateDto dto) {
        // chama a função para salvar no banco
        Funcionario f = serviceFuncionario.criar(dto);
        serviceEmail.enviarConfirmacaoEmail(f.getEmail(), f.getCodigoVerificacao());

        FuncionarioResponseDto response = new FuncionarioResponseDto();

        response.setNome(f.getNome());
        response.setCpf(f.getCpf());
        response.setEmail(f.getEmail());
        response.setCargo(f.getCargo());
        response.setDdd(f.getDdd());
        response.setNumero(f.getNumero());
        response.setMatricula(f.getMatricula());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/busca")
    public ResponseEntity<FuncionarioResponseDto> buscar(@RequestParam String identificador) {
        Funcionario f = serviceFuncionario.buscar(identificador);

        FuncionarioResponseDto response = new FuncionarioResponseDto();
        response.setNome(f.getNome());
        response.setCpf(f.getCpf());
        response.setEmail(f.getEmail());
        response.setCargo(f.getCargo());
        response.setDdd(f.getDdd());
        response.setNumero(f.getNumero());
        response.setMatricula(f.getMatricula());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<FuncionarioResponseDto> atualizar(@PathVariable String cpf, @Valid @RequestBody FuncionarioUpdateDto dto) {

        Funcionario funcionario = serviceFuncionario.atualizar(cpf, dto);

        FuncionarioResponseDto response = new FuncionarioResponseDto();

        response.setNome(funcionario.getNome());
        response.setDdd(funcionario.getDdd());
        response.setNumero(funcionario.getNumero());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> excluir(@PathVariable String cpf) {
        serviceFuncionario.excluir(cpf);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/confirmar-email")
    public ResponseEntity<String> confirmarEmail(@Valid @RequestBody ReenviarConfirmacaoDto dto){
        Funcionario funcionario = repository.findByEmail(dto.getEmail());

        if(funcionario == null){
            return ResponseEntity.status(400).body("Email não encontrado");
        }

        if (!dto.getCodigo().equals(funcionario.getCodigoVerificacao())) {
            return ResponseEntity.badRequest()
                    .body("Código inválido");
        }

        if(funcionario.getCodigoExpiracao().isBefore(LocalDateTime.now())){
            return ResponseEntity.badRequest()
                    .body("Código expirado");
        }

        funcionario.setEmailVerificado(true);
        funcionario.setCodigoVerificacao(null);
        repository.save(funcionario);
        return ResponseEntity.ok("Email confirmado com sucesso");
    }

    @PostMapping("/reenviar-confirmacao")
    public ResponseEntity<String> reenviarConfirmacao(@Valid @RequestBody ReenviarConfirmacaoDto dto) {

        Funcionario f = repository.findByEmail(dto.getEmail());

        if (f == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Email não encontrado");
        }

        if (f.isEmailVerificado()) {
            return ResponseEntity
                    .badRequest()
                    .body("Email já confirmado");
        }

        f.setCodigoVerificacao(dto.getCodigo());
        f.setCodigoExpiracao(LocalDateTime.now().plusHours(24));

        repository.save(f);

        serviceEmail.enviarConfirmacaoEmail(
                f.getEmail(),
                f.getCodigoVerificacao()
        );

        return ResponseEntity.ok("Novo código de confirmação enviado");
    }

}
