package com.pi1.Edook.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pi1.Edook.dto.EquipamentoReservaDto;
import com.pi1.Edook.dto.ReservaCreateDto;
import com.pi1.Edook.exception.BusinessException;
import com.pi1.Edook.model.Equipamento;
import com.pi1.Edook.model.EquipamentoId;
import com.pi1.Edook.model.Funcionario;
import com.pi1.Edook.model.Reserva;
import com.pi1.Edook.model.Utiliza;
import com.pi1.Edook.model.UtilizaId;
import com.pi1.Edook.repository.EquipamentoRepository;
import com.pi1.Edook.repository.FuncionarioRepository;
import com.pi1.Edook.repository.ReservaRepository;
import com.pi1.Edook.repository.UtilizaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final UtilizaRepository utilizaRepository;

    private List<Equipamento> equipamentos = new ArrayList<>();
    private Set<String> equipamentosUnicos = new HashSet<>();

    public ReservaService(
            ReservaRepository reservaRepository,
            FuncionarioRepository funcionarioRepository,
            EquipamentoRepository equipamentoRepository,
            UtilizaRepository utilizaRepository) {

        this.reservaRepository = reservaRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.utilizaRepository = utilizaRepository;
    }


    // Cadastrar reserva
    public Reserva criar(ReservaCreateDto dto) {
        // valido os dados da reserva
        validarDados(dto);

        Funcionario funcionario =
                funcionarioRepository.findById(dto.getCpfFuncionario())
                .orElseThrow(() ->
                        new BusinessException(
                                "Funcionário não encontrado",
                                HttpStatus.NOT_FOUND
                        ));

        // verifica cada equipamento que quero reservar - se existe, se tem algum repetido, se tem choque de horario
        for (EquipamentoReservaDto dtoEquip : dto.getEquipamentos()) {

            EquipamentoId id = new EquipamentoId(
                    dtoEquip.getPrefixo(),
                    dtoEquip.getNumero()
            );

            Equipamento equipamento = validarEquipamento(dtoEquip, id, dto);

            equipamentos.add(equipamento);
        }

        Reserva reserva = new Reserva();

        reserva.setNome(dto.getNome());
        reserva.setLocalidade(dto.getLocalidade());
        reserva.setDia(dto.getDia());
        reserva.setHorarioInicio(dto.getHorarioInicio());
        reserva.setHorarioFim(dto.getHorarioFim());
        reserva.setStatus("Pendente");
        reserva.setFuncionario(funcionario);

        // salvo a reserva na tabela
        reserva = reservaRepository.save(reserva);

        // salvo equipamento por equipamento na tabela de utiliza
        for (Equipamento equipamento : equipamentos) {

            Utiliza utiliza = new Utiliza();

            utiliza.setId(
                    new UtilizaId(
                            reserva.getId(),
                            equipamento.getId().getPrefixo(),
                            equipamento.getId().getNumero()
                    )
            );

            utiliza.setReserva(reserva);
            utiliza.setEquipamento(equipamento);

            utilizaRepository.save(utiliza);
        }

        return reserva;
    }

    private void validarDados(ReservaCreateDto dto){
        // verifica se o horario faz sentido
        if (!dto.getHorarioFim().isAfter(dto.getHorarioInicio())) {
            throw new BusinessException(
                    "Horário final deve ser maior que o horário inicial",
                    HttpStatus.BAD_REQUEST
            );
        }

        // verifica se a data ja passou
        if (dto.getDia().isBefore(LocalDate.now())) {
            throw new BusinessException(
                    "Não é possível reservar para uma data passada",
                    HttpStatus.BAD_REQUEST
            );
        }

        // verifica se equipamentos é null
        if (dto.getEquipamentos() == null ||
                dto.getEquipamentos().isEmpty()) {

            throw new BusinessException(
                    "A reserva deve possuir ao menos um equipamento",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private Equipamento validarEquipamento(EquipamentoReservaDto dtoEquip, EquipamentoId id, ReservaCreateDto dto){
        // verifico se o equipamento existe
        Equipamento equipamento = equipamentoRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(
                                    "Equipamento não encontrado: "
                                            + dtoEquip.getPrefixo()
                                            + dtoEquip.getNumero(),
                                    HttpStatus.NOT_FOUND
                            ));

        String chave = dtoEquip.getPrefixo() + "-" + dtoEquip.getNumero();

        // verifico se eu não estou salvando nenhum repetido
        if (!equipamentosUnicos.add(chave)) {
        throw new BusinessException(
                "Equipamento repetido na reserva",
                HttpStatus.BAD_REQUEST
        );
        }

        // verifico os choques de horarios
        boolean conflito = utilizaRepository.existeConflito(
            dtoEquip.getPrefixo(),
            dtoEquip.getNumero(),
            dto.getDia(),
            dto.getHorarioInicio(),
            dto.getHorarioFim()
        );

        if (conflito) {
        throw new BusinessException(
            "Equipamento "
                    + dtoEquip.getPrefixo()
                    + dtoEquip.getNumero()
                    + " já está reservado nesse horário",
            HttpStatus.BAD_REQUEST
        );
        }

        return equipamento;
    }

    // atualiza apenas as reservas que são pendentes para concluida caso o horario ja tenha passado
    private void atualizarReservasConcluidas() {

        List<Reserva> reservas = reservaRepository.findByStatus("Pendente");

        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();

        for (Reserva reserva : reservas) {

			boolean terminou =
					reserva.getDia().isBefore(hoje)
					||
					(
					reserva.getDia().equals(hoje)
					&& reserva.getHorarioFim().isBefore(agora)
					);

			if (terminou) {
				reserva.setStatus("Concluída");
			}
        }
	}

	public List<Reserva> listarProximasReservas() {
    	atualizarReservasConcluidas();

    	return reservaRepository.buscarProximasReservas(
            LocalDate.now()
    	);
	}

	public void cancelar(Integer idReserva, String cpfFuncionario) {
		Reserva reserva = reservaRepository.findById(idReserva)
			.orElseThrow(() ->
				new BusinessException(
					"Reserva não encontrada",
					HttpStatus.NOT_FOUND
			));
		
		Funcionario funcionario =
        funcionarioRepository.findById(cpfFuncionario)
			.orElseThrow(() ->
				new BusinessException(
					"Funcionário não encontrado",
					HttpStatus.NOT_FOUND
			));

		if (reserva.getStatus().equals("Cancelada")) {
			throw new BusinessException(
				"A reserva já está cancelada",
				HttpStatus.BAD_REQUEST
			);
		}

		if (reserva.getStatus().equals("Concluída")) {
			throw new BusinessException(
				"Não é possível cancelar uma reserva concluída",
				HttpStatus.BAD_REQUEST
			);
		}

		if (funcionario.getCargo().equals("Docente") && !reserva.getFuncionario()
			.getCpf()
			.equals(funcionario.getCpf())) {

			throw new BusinessException(
				"Você não possui permissão para cancelar esta reserva",
				HttpStatus.FORBIDDEN
			);
		}

		reserva.setStatus("Cancelada");
	}
}
