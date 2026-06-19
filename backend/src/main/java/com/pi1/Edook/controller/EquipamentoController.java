package com.pi1.Edook.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi1.Edook.dto.EquipamentoCreateDto;
import com.pi1.Edook.dto.EquipamentoResponseDto;
import com.pi1.Edook.model.Equipamento;
import com.pi1.Edook.service.EquipamentoService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/equipamentos")
public class EquipamentoController {
    private final EquipamentoService service;

    public EquipamentoController(EquipamentoService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EquipamentoResponseDto> cadastrar(@Valid @RequestBody EquipamentoCreateDto dto){
        Equipamento equipamento = service.cadastrar(dto);

        EquipamentoResponseDto response = new EquipamentoResponseDto(
            equipamento.getId().getPrefixo(),
        equipamento.getId().getNumero(),
        equipamento.getDescricao(),
        equipamento.getTipo(),
        equipamento.getFuncionario().getCpf()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                                .body(response);
    }
}
