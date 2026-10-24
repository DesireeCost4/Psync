package com.example.psi.service;

import com.example.psi.model.ClienteFinal;
import com.example.psi.model.Mensalidade;
import com.example.psi.model.StatusMensalidade;
import com.example.psi.repository.ClienteFinalRepository;
import com.example.psi.repository.MensalidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class MensalidadeService {

    @Autowired
    private MensalidadeRepository mensalidadeRepository;

    @Autowired
    private ClienteFinalRepository clienteFinalRepository;

    public List<Mensalidade> listarPorCliente(Long clienteId, Long admId) {
        validarCliente(clienteId, admId);
        return mensalidadeRepository.findByClienteFinalIdOrderByDataVencimentoDesc(clienteId);
    }

    public void criar(Long clienteId, Long admId, Mensalidade mensalidade) {
        ClienteFinal cliente = validarCliente(clienteId, admId);
        if (mensalidade.getValor() == null || mensalidade.getValor().signum() <= 0
                || mensalidade.getDataVencimento() == null) {
            throw new IllegalArgumentException("Valor e vencimento são obrigatórios.");
        }

        mensalidade.setId(null);
        mensalidade.setClienteFinal(cliente);
        mensalidade.setClienteAdm(cliente.getClienteAdm());
        mensalidade.setStatus(StatusMensalidade.PENDENTE);
        mensalidadeRepository.save(mensalidade);
    }

    public void marcarComoPaga(Long id, Long admId) {
        Mensalidade mensalidade = buscarDoAdm(id, admId);
        mensalidade.setStatus(StatusMensalidade.PAGO);
        mensalidadeRepository.save(mensalidade);
    }

    public void deletar(Long id, Long admId) {
        mensalidadeRepository.delete(buscarDoAdm(id, admId));
    }

    public List<com.example.psi.repository.StatusFinanceiroProjection> statusPorClientes(Long admId) {
        return mensalidadeRepository.buscarStatusFinanceiro(admId, LocalDate.now());
    }

    public BigDecimal totalPendente(Long admId) {
        return mensalidadeRepository.somarMensalidadesPendentes(admId);
    }

    private ClienteFinal validarCliente(Long clienteId, Long admId) {
        return clienteFinalRepository.findById(clienteId)
                .filter(cliente -> cliente.getClienteAdm() != null
                        && admId.equals(cliente.getClienteAdm().getId()))
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
    }

    private Mensalidade buscarDoAdm(Long id, Long admId) {
        return mensalidadeRepository.findById(id)
                .filter(mensalidade -> mensalidade.getClienteAdm() != null
                        && admId.equals(mensalidade.getClienteAdm().getId()))
                .orElseThrow(() -> new IllegalArgumentException("Mensalidade não encontrada."));
    }
}
