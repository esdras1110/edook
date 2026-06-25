package com.pi1.Edook.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pi1.Edook.dto.EquipamentoResponseDto;
import com.pi1.Edook.dto.ReservaCreateDto;
import com.pi1.Edook.dto.ReservaResponseDto;
import com.pi1.Edook.model.Equipamento;
import com.pi1.Edook.model.Reserva;
import com.pi1.Edook.repository.UtilizaRepository;
import com.pi1.Edook.service.ReservaService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final UtilizaRepository utilizaRepository;

    public ReservaController(
            ReservaService reservaService,
            UtilizaRepository utilizaRepository) {

        this.reservaService = reservaService;
        this.utilizaRepository = utilizaRepository;
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDto> criar(
            @Valid @RequestBody ReservaCreateDto dto) {

        Reserva reserva = reservaService.criar(dto);

        List<EquipamentoResponseDto> equipamentos =
                utilizaRepository.findByReservaId(reserva.getId())
                        .stream()
                        .map(utiliza -> {

                            Equipamento e = utiliza.getEquipamento();

                            return new EquipamentoResponseDto(
                                    e.getId().getPrefixo(),
                                    e.getId().getNumero(),
                                    e.getDescricao(),
                                    e.getTipo()
                            );
                        })
                        .toList();

        ReservaResponseDto response = new ReservaResponseDto();

        response.setId(reserva.getId());
        response.setNome(reserva.getNome());
        response.setLocalidade(reserva.getLocalidade());
        response.setDia(reserva.getDia());
        response.setHorarioInicio(reserva.getHorarioInicio());
        response.setHorarioFim(reserva.getHorarioFim());
        response.setStatus(reserva.getStatus());
        response.setCpfFuncionario(
                reserva.getFuncionario().getCpf()
        );
        response.setEquipamentos(equipamentos);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    @GetMapping
	public ResponseEntity<List<ReservaResponseDto>> listar() {

	List<ReservaResponseDto> reservas =
		reservaService.listarProximasReservas()
			.stream()
			.map(reserva -> {

				List<EquipamentoResponseDto> equipamentos = utilizaRepository.findByReservaId(reserva.getId())
					.stream()
					.map(utiliza -> {
						Equipamento e = utiliza.getEquipamento();

						return new EquipamentoResponseDto(
							e.getId().getPrefixo(),
							e.getId().getNumero(),
							e.getDescricao(),
							e.getTipo()
						);
					})
					.toList();

					ReservaResponseDto response = new ReservaResponseDto();

					response.setId(reserva.getId());
					response.setNome(reserva.getNome());
					response.setLocalidade(reserva.getLocalidade());
					response.setDia(reserva.getDia());
					response.setHorarioInicio(
							reserva.getHorarioInicio()
					);
					response.setHorarioFim(
							reserva.getHorarioFim()
					);
					response.setStatus(
							reserva.getStatus()
					);
					response.setCpfFuncionario(
							reserva.getFuncionario().getCpf()
					);
					response.setEquipamentos(
							equipamentos
					);

					return response;
			})
			.toList();

	return ResponseEntity.ok(reservas);
	}

	@PatchMapping("/{id}/cancelar")
	public ResponseEntity<Void> cancelar(@PathVariable Integer id, @RequestParam String cpf) {
		reservaService.cancelar(id, cpf);

		return ResponseEntity.noContent().build();
	}
}
