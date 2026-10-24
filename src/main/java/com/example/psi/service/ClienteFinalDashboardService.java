package com.example.psi.service;

import com.example.psi.model.ClienteFinalDashboard;
import com.example.psi.model.Mensalidade;
import com.example.psi.model.Agendamento;
import com.example.psi.model.ClienteFinal;
import com.example.psi.repository.AgendamentoRepository;
import com.example.psi.repository.MensalidadeRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteFinalDashboardService {

    private final ClienteFinalService clienteFinalService;
    private final AgendamentoRepository agendamentoRepository;
    private final MensalidadeRepository mensalidadeRepository;
    private final MensalidadeService mensalidadeService;

    public ClienteFinalDashboardService(ClienteFinalService clienteFinalService,
                                        AgendamentoRepository agendamentoRepository,
                                        MensalidadeRepository mensalidadeRepository,
                                        MensalidadeService mensalidadeService) {
        this.clienteFinalService = clienteFinalService;
        this.agendamentoRepository = agendamentoRepository;
        this.mensalidadeRepository = mensalidadeRepository;
        this.mensalidadeService = mensalidadeService;
    }

    public ClienteFinalDashboard carregar(String email) {
        ClienteFinal cliente = clienteFinalService.buscarPorEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente final não encontrado."));
        Long clienteId = cliente.getId();
        Long admId = cliente.getClienteAdm().getId();

        List<Agendamento> agendamentos =
                agendamentoRepository.findByClienteFinalIdAndClienteAdmIdOrderByInicioDesc(clienteId, admId);
        List<Mensalidade> mensalidades =
                mensalidadeRepository.findByClienteFinalIdAndClienteAdmIdOrderByDataVencimentoDesc(
                        clienteId, admId);

        return new ClienteFinalDashboard(
                cliente,
                agendamentos,
                mensalidades,
                mensalidadeService.calcularStatusFinanceiro(mensalidades)
        );
    }
}
