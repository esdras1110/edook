package com.pi1.Edook.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi1.Edook.dto.EquipamentoCreateDto;
import com.pi1.Edook.dto.EquipamentoResponseDto;
import com.pi1.Edook.dto.EquipamentoUpdateDto;
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
        equipamento.getTipo()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                                .body(response); 
    }

    @PutMapping("/{prefixo}/{numero}")
    public ResponseEntity<EquipamentoResponseDto> atualizar(@PathVariable String prefixo, @PathVariable Short numero,
            @Valid @RequestBody EquipamentoUpdateDto dto) {

        Equipamento equipamento = service.atualizar(prefixo, numero, dto);

        EquipamentoResponseDto response =
            new EquipamentoResponseDto(
                equipamento.getId().getPrefixo(),
                equipamento.getId().getNumero(),
                equipamento.getDescricao(),
                equipamento.getTipo()
            );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{prefixo}/{numero}")
    public ResponseEntity<Void> excluir(@PathVariable String prefixo, @PathVariable Short numero) {
        service.excluir(prefixo, numero);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EquipamentoResponseDto>> listar() {

        List<EquipamentoResponseDto> response =
                service.listar()
                        .stream()
                        .map(e -> new EquipamentoResponseDto(
                                e.getId().getPrefixo(),
                                e.getId().getNumero(),
                                e.getDescricao(),
                                e.getTipo()
                        ))
                        .toList();

        return ResponseEntity.ok(response);
    }
}
