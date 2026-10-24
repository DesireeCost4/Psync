package com.example.psi.service;

import com.example.psi.model.Agendamento;
import com.example.psi.model.ClienteFinal;
import com.example.psi.model.StatusAgendamento;
import com.example.psi.repository.AgendamentoRepository;
import com.example.psi.repository.ClienteFinalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private ClienteFinalRepository clienteFinalRepository;

    public List<Agendamento> listar(Long admId, LocalDate data) {
        if (data == null) {
            return agendamentoRepository.findByClienteAdmIdOrderByInicioAsc(admId);
        }
        return agendamentoRepository.findByClienteAdmIdAndInicioBetweenOrderByInicio(
                admId, data.atStartOfDay(), data.plusDays(1).atStartOfDay().minusNanos(1));
    }

    public List<Agendamento> listarPorCliente(Long clienteId, Long admId) {
        validarCliente(clienteId, admId);
        return agendamentoRepository.findByClienteFinalIdAndClienteAdmIdOrderByInicioDesc(clienteId, admId);
    }

    public void salvar(Long clienteId, Long admId, Agendamento agendamento) {
        ClienteFinal cliente = validarCliente(clienteId, admId);
        validarDados(agendamento);

        if (agendamentoRepository.existeConflito(admId, agendamento.getId(),
                agendamento.getInicio(), agendamento.getFim())) {
            throw new IllegalArgumentException("Já existe uma consulta nesse intervalo.");
        }

        agendamento.setClienteFinal(cliente);
        agendamento.setClienteAdm(cliente.getClienteAdm());
        if (agendamento.getStatus() == null) {
            agendamento.setStatus(StatusAgendamento.AGENDADO);
        }
        agendamentoRepository.save(agendamento);
    }

    public void atualizarStatus(Long id, Long admId, StatusAgendamento status) {
        Agendamento agendamento = buscarDoAdm(id, admId);
        if (status == null) {
            throw new IllegalArgumentException("Status inválido.");
        }
        agendamento.setStatus(status);
        agendamentoRepository.save(agendamento);
    }

    public void excluir(Long id, Long admId) {
        agendamentoRepository.delete(buscarDoAdm(id, admId));
    }

    private void validarDados(Agendamento agendamento) {
        if (agendamento.getInicio() == null || agendamento.getFim() == null
                || agendamento.getTitulo() == null || agendamento.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Título, início e fim são obrigatórios.");
        }
        if (!agendamento.getFim().isAfter(agendamento.getInicio())) {
            throw new IllegalArgumentException("O fim deve ser posterior ao início.");
        }
    }

    private ClienteFinal validarCliente(Long clienteId, Long admId) {
        return clienteFinalRepository.findById(clienteId)
                .filter(cliente -> cliente.getClienteAdm() != null
                        && admId.equals(cliente.getClienteAdm().getId()))
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
    }

    private Agendamento buscarDoAdm(Long id, Long admId) {
        return agendamentoRepository.findById(id)
                .filter(agendamento -> agendamento.getClienteAdm() != null
                        && admId.equals(agendamento.getClienteAdm().getId()))
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));
    }
}
