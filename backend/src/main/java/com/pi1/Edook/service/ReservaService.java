package com.pi1.Edook.service;

import java.time.LocalDate;
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

    public Reserva criar(ReservaCreateDto dto) {
        validarDados(dto);

        Funcionario funcionario =
                funcionarioRepository.findById(dto.getCpfFuncionario())
                .orElseThrow(() ->
                        new BusinessException(
                                "Funcionário não encontrado",
                                HttpStatus.NOT_FOUND
                        ));

        
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

        reserva = reservaRepository.save(reserva);

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
        if (!dto.getHorarioFim().isAfter(dto.getHorarioInicio())) {
            throw new BusinessException(
                    "Horário final deve ser maior que o horário inicial",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (dto.getDia().isBefore(LocalDate.now())) {
            throw new BusinessException(
                    "Não é possível reservar para uma data passada",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (dto.getEquipamentos() == null ||
                dto.getEquipamentos().isEmpty()) {

            throw new BusinessException(
                    "A reserva deve possuir ao menos um equipamento",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private Equipamento validarEquipamento(EquipamentoReservaDto dtoEquip, EquipamentoId id, ReservaCreateDto dto){
        Equipamento equipamento =
                    equipamentoRepository.findById(id)
                    .orElseThrow(() ->
                            new BusinessException(
                                    "Equipamento não encontrado: "
                                            + dtoEquip.getPrefixo()
                                            + dtoEquip.getNumero(),
                                    HttpStatus.NOT_FOUND
                            ));

            String chave = dtoEquip.getPrefixo() + "-" + dtoEquip.getNumero();

            if (!equipamentosUnicos.add(chave)) {
                throw new BusinessException(
                        "Equipamento repetido na reserva",
                        HttpStatus.BAD_REQUEST
                );
            }
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
}
