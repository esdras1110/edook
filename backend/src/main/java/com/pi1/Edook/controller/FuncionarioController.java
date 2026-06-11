package com.pi1.Edook.controller;

import com.pi1.Edook.model.Funcionario;
import com.pi1.Edook.repository.FuncionarioRepository;
import com.pi1.Edook.dto.FuncionarioCreateDto;
import com.pi1.Edook.dto.FuncionarioResponseDto;
import com.pi1.Edook.service.EmailService;
import com.pi1.Edook.service.FuncionarioService;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        serviceEmail.enviarConfirmacaoEmail(f.getEmail(), f.getTokenVerificacao());

        FuncionarioResponseDto response = new FuncionarioResponseDto();

        response.setNome(f.getNome());
        response.setCpf(f.getCpf());
        response.setEmail(f.getEmail());
        response.setCargo(f.getCargo());
        response.setMatricula(f.getMatricula());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/confirmar-email")
    public ResponseEntity<String> confirmarEmail(@RequestParam String token){
        Funcionario funcionario = repository.findByTokenVerificacao(token);

        if(funcionario == null){
            return ResponseEntity.status(400).body("Token inválido");
        }

        if(funcionario.getTokenExpiracao().isBefore(LocalDateTime.now())){
            return ResponseEntity.badRequest()
                    .body("Token expirado");
        }

        funcionario.setEmailVerificado(true);
        funcionario.setTokenVerificacao(null);
        repository.save(funcionario);
        return ResponseEntity.ok("Email confirmado com sucesso");
    }

    @PostMapping("/reenviar-confirmacao")
    public ResponseEntity<String> reenviarConfirmacao(@RequestParam String email){
        Funcionario f = repository.findByEmail(email);

        if(f == null){
            return ResponseEntity.status(400).body("Email não encontrado");
        }

        if (f.isEmailVerificado()) {
            return ResponseEntity.badRequest()
                    .body("Email já confirmado");
        }

        String token = UUID.randomUUID().toString();
        f.setTokenVerificacao(token);
        f.setTokenExpiracao(LocalDateTime.now().plusHours(24));

        repository.save(f);

        serviceEmail.enviarConfirmacaoEmail(email, token);

        return ResponseEntity.ok().body("Novo email de confirmação enviado");

    }

}
