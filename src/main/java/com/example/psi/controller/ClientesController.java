package com.example.psi.controller;

import com.example.psi.model.Agendamento;
import com.example.psi.model.ClienteFinal;
import com.example.psi.model.SeguimentoTipo;
import com.example.psi.model.StatusFinanceiro;
import com.example.psi.repository.StatusFinanceiroProjection;
import com.example.psi.repository.AgendamentoRepository;
import com.example.psi.service.ClienteFinalService;
import com.example.psi.service.MensalidadeService;
import com.example.psi.service.ProfissionalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ClientesController {

    private final ClienteFinalService clienteFinalService;
    private final MensalidadeService mensalidadeService;
    private final ProfissionalService profissionalService;
    private final AgendamentoRepository agendamentoRepository;

    public ClientesController(ClienteFinalService clienteFinalService,
                              MensalidadeService mensalidadeService,
                              ProfissionalService profissionalService,
                              AgendamentoRepository agendamentoRepository) {
        this.clienteFinalService = clienteFinalService;
        this.mensalidadeService = mensalidadeService;
        this.profissionalService = profissionalService;
        this.agendamentoRepository = agendamentoRepository;
    }

    @GetMapping("/clientes")
    public String listar(@RequestParam(required = false) String nome,
                         @RequestParam(required = false) String cpfCnpj,
                         @RequestParam(required = false) String email,
                         @RequestParam(defaultValue = "TODOS") String status,
                         Model model,
                         Authentication authentication) {
        Long admId = profissionalService.obterId(authentication.getName());
        SeguimentoTipo segmento = profissionalService.obterSegmento(authentication.getName());
        List<ClienteFinal> resultados = clienteFinalService.buscarPorFiltros(admId, nome, cpfCnpj, email);
        Map<Long, StatusFinanceiro> statusPorCliente = statusPorClientes(admId);
        resultados.forEach(cliente -> statusPorCliente.putIfAbsent(cliente.getId(), StatusFinanceiro.EM_DIA));

        long pendentes = resultados.stream()
                .filter(cliente -> statusPorCliente.get(cliente.getId()) != StatusFinanceiro.EM_DIA)
                .count();
        String filtroStatus = normalizarStatus(status);
        List<ClienteFinal> clientes = resultados.stream()
                .filter(cliente -> "TODOS".equals(filtroStatus)
                        || statusPorCliente.get(cliente.getId()).name().equals(filtroStatus))
                .toList();

        Map<Long, Agendamento> ultimoAtendimento = ultimosAtendimentos(admId);
        Map<Long, String> linksWhatsapp = clientes.stream().collect(Collectors.toMap(
                ClienteFinal::getId,
                cliente -> gerarLinkWhatsapp(cliente, statusPorCliente.get(cliente.getId())),
                (primeiro, segundo) -> primeiro
        ));

        model.addAttribute("clientes", clientes);
        model.addAttribute("statusFinanceiro", statusPorCliente);
        model.addAttribute("ultimoAtendimento", ultimoAtendimento);
        model.addAttribute("linksWhatsapp", linksWhatsapp);
        model.addAttribute("totalClientes", resultados.size());
        model.addAttribute("clientesAtencao", pendentes);
        model.addAttribute("segmento", segmento);
        model.addAttribute("filtroStatus", filtroStatus);
        model.addAttribute("filtroNome", nome);
        model.addAttribute("filtroCpfCnpj", cpfCnpj);
        model.addAttribute("filtroEmail", email);
        model.addAttribute("statusOpcoes", List.of("TODOS", "EM_DIA", "PENDENTE", "ATRASADA"));
        return "clientes";
    }

    private Map<Long, StatusFinanceiro> statusPorClientes(Long admId) {
        return mensalidadeService.statusPorClientes(admId).stream()
                .collect(Collectors.toMap(StatusFinanceiroProjection::getClienteId,
                        item -> StatusFinanceiro.valueOf(item.getStatus())));
    }

    private Map<Long, Agendamento> ultimosAtendimentos(Long admId) {
        Map<Long, Agendamento> ultimos = new HashMap<>();
        for (Agendamento agendamento : agendamentoRepository.findByClienteAdmIdOrderByInicioDesc(admId)) {
            ultimos.putIfAbsent(agendamento.getClienteFinal().getId(), agendamento);
        }
        return ultimos;
    }

    private String gerarLinkWhatsapp(ClienteFinal cliente, StatusFinanceiro status) {
        if (status == StatusFinanceiro.EM_DIA || cliente.getDadosCadastro() == null
                || cliente.getDadosCadastro().getTelefone() == null
                || cliente.getDadosCadastro().getTelefone().isBlank()) {
            return "";
        }
        String telefone = cliente.getDadosCadastro().getTelefone().replaceAll("\\D", "");
        String nome = cliente.getDadosCadastro().getNome() == null
                ? "cliente" : cliente.getDadosCadastro().getNome();
        String mensagem = "Olá, " + nome + "! Identificamos uma mensalidade "
                + (status == StatusFinanceiro.ATRASADA ? "em atraso" : "pendente")
                + " no seu cadastro. Podemos ajudar?";
        return "https://wa.me/55" + telefone + "?text="
                + URLEncoder.encode(mensagem, StandardCharsets.UTF_8);
    }

    private String normalizarStatus(String status) {
        String valor = status == null ? "TODOS" : status.trim().toUpperCase();
        return List.of("TODOS", "EM_DIA", "PENDENTE", "ATRASADA").contains(valor)
                ? valor : "TODOS";
    }
}
