package com.pi1.Edook.controller;

import com.pi1.Edook.model.Funcionario;
import com.pi1.Edook.dto.FuncionarioCreateDto;
import com.pi1.Edook.dto.FuncionarioResponseDto;
import com.pi1.Edook.service.FuncionarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FuncionarioResponseDto> criar(@RequestBody FuncionarioCreateDto dto) {

        System.out.println("================================");
        System.out.println("Nome: " + dto.getNome());
        System.out.println("Cpf: " + dto.getCpf());
        System.out.println("Email: " + dto.getEmail());
        System.out.println("================================");

        Funcionario f = service.criar(dto);

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
}
