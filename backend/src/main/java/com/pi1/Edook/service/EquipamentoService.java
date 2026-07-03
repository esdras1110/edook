package com.pi1.Edook.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pi1.Edook.dto.EquipamentoCreateDto;
import com.pi1.Edook.dto.EquipamentoUpdateDto;
import com.pi1.Edook.exception.BusinessException;
import com.pi1.Edook.model.Equipamento;
import com.pi1.Edook.model.EquipamentoId;
import com.pi1.Edook.model.Funcionario;
import com.pi1.Edook.repository.EquipamentoRepository;
import com.pi1.Edook.repository.FuncionarioRepository;
import com.pi1.Edook.repository.UtilizaRepository;

@Service
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final UtilizaRepository utilizaRepository;

    public EquipamentoService(
            EquipamentoRepository equipamentoRepository,
            FuncionarioRepository funcionarioRepository,
            UtilizaRepository utilizaRepository) {

        this.equipamentoRepository = equipamentoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.utilizaRepository = utilizaRepository;
    }

    public Equipamento cadastrar(EquipamentoCreateDto dto) {
        System.out.println("===================================");
        System.out.println("CPF recebido: [" + dto.getCpfCadastro() + "]");
        System.out.println(funcionarioRepository.findAll());
        System.out.println("===================================");

        // Busca funcionário
        Funcionario funcionario =
                funcionarioRepository.findByCpf(dto.getCpfCadastro());

        // verifica se ele existe
        if (funcionario == null) {
            throw new BusinessException(
                    "Funcionário não encontrado",
                    HttpStatus.NOT_FOUND
            );
        }

        // Busca o maior número do prefixo
        Short maiorNumero =
                equipamentoRepository
                        .buscaMaximoId(dto.getPrefixo());

        Short proximoNumero;

        if (maiorNumero == null) {
            proximoNumero = 1;
        } else {
            proximoNumero = (short) (maiorNumero + 1);
        }

        // Monta a chave composta
        EquipamentoId id = new EquipamentoId();
        id.setPrefixo(dto.getPrefixo());
        id.setNumero(proximoNumero);

        // Cria o equipamento
        Equipamento equipamento = new Equipamento();
        equipamento.setId(id);
        equipamento.setDescricao(dto.getDescricao());
        equipamento.setTipo(dto.getTipo());
        equipamento.setFuncionario(funcionario);

        // Salva
        return equipamentoRepository.save(equipamento);
    }

    public Equipamento atualizar(String prefixo, Short numero, EquipamentoUpdateDto dto) {

        EquipamentoId id = new EquipamentoId(prefixo, numero);

        Equipamento equipamento =
            equipamentoRepository.findById(id)
            .orElseThrow(() ->
                new BusinessException(
                    "Equipamento não encontrado",
                    HttpStatus.NOT_FOUND
            ));

        equipamento.setDescricao(dto.getDescricao());
        equipamento.setTipo(dto.getTipo());

        return equipamentoRepository.save(equipamento);
    }

    public void excluir(String prefixo, Short numero) {

        EquipamentoId id = new EquipamentoId(prefixo, numero);

        Equipamento equipamento =
            equipamentoRepository.findById(id)
            .orElseThrow(() ->
                new BusinessException(
                    "Equipamento não encontrado",
                    HttpStatus.NOT_FOUND
                ));

        boolean possuiReservaPendente = utilizaRepository.existeReservaPendente(prefixo, numero);

        if (possuiReservaPendente) {
            throw new BusinessException(
                "Não é possível excluir um equipamento com reservas pendentes",
                HttpStatus.BAD_REQUEST
            );
        }

        equipamentoRepository.delete(equipamento);
    }

    public List<Equipamento> listar() {
        return equipamentoRepository.listarOrdenados();
    }
}
