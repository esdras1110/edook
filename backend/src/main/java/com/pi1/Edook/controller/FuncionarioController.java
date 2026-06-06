package com.pi1.Edook.controller;

import com.pi1.Edook.model.Funcionario;
import com.pi1.Edook.repository.FuncionarioRepository;
import com.pi1.Edook.dto.FuncionarioCreateDto;
import com.pi1.Edook.dto.FuncionarioResponseDto;
import com.pi1.Edook.service.EmailService;
import com.pi1.Edook.service.FuncionarioService;

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
    public ResponseEntity<FuncionarioResponseDto> criar(@RequestBody FuncionarioCreateDto dto) {

        System.out.println("================================");
        System.out.println("Nome: " + dto.getNome());
        System.out.println("Cpf: " + dto.getCpf());
        System.out.println("Email: " + dto.getEmail());
        System.out.println("================================");

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

        funcionario.setEmail_verificado(true);
        funcionario.setTokenVerificacao(null);
        return ResponseEntity.ok("Email confirmado com sucesso");
    }

    @GetMapping("/teste-email")
    public ResponseEntity<String> testeEmail() {

        serviceEmail.enviarConfirmacaoEmail(
            "oesdras709@gmail.com",
            "token-teste-123"
        );

        return ResponseEntity.ok("Email de teste enviado (se SMTP estiver certo)");
    }
}
