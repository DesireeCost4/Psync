package com.example.psi.controller;

import com.example.psi.model.ClienteFinal;
import com.example.psi.model.SeguimentoTipo;
import com.example.psi.model.StatusFinanceiro;
import com.example.psi.repository.StatusFinanceiroProjection;
import com.example.psi.service.ClienteFinalService;
import com.example.psi.service.MensalidadeService;
import com.example.psi.service.ProcessoService;
import com.example.psi.service.ProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ClienteFinalService clienteFinalService;

    @Autowired
    private MensalidadeService mensalidadeService;

    private final ProcessoService processoService;
    private final ProfissionalService profissionalService;

    public HomeController(ProcessoService processoService, ProfissionalService profissionalService) {
        this.processoService = processoService;
        this.profissionalService = profissionalService;
    }

    @GetMapping("/")
    public String landingPage() {
        return "index"; // Aponta para index.html na pasta templates
    }

    @GetMapping("/home")
    public String painellogado(Authentication authentication,
                               Model model) {

        Long admLogadoId = profissionalService.obterId(authentication.getName());
        SeguimentoTipo segmentoAtivo = profissionalService.obterSegmento(authentication.getName());

        // Busca apenas os clientes do ADM 1
        List<ClienteFinal> listaClientes = clienteFinalService.buscarPorFiltros(admLogadoId, null, null, null);

        adicionarDadosDoPainel(model, listaClientes, segmentoAtivo, admLogadoId);

        return "home";
    }

    private void adicionarDadosDoPainel(Model model, List<ClienteFinal> clientes,
                                        SeguimentoTipo segmento, Long admId) {
        model.addAttribute("clientes", clientes);
        Map<Long, StatusFinanceiro> statusFinanceiro = mensalidadeService.statusPorClientes(admId).stream()
                .collect(Collectors.toMap(StatusFinanceiroProjection::getClienteId,
                        status -> StatusFinanceiro.valueOf(status.getStatus())));
        clientes.forEach(cliente -> statusFinanceiro.putIfAbsent(cliente.getId(), StatusFinanceiro.EM_DIA));
        model.addAttribute("statusFinanceiro", statusFinanceiro);
        model.addAttribute("segmento", segmento);
        model.addAttribute("totalClientes", clientes.size());
        model.addAttribute("proximosAtendimentos", 0);
        model.addAttribute("mensalidadesPendentes", mensalidadeService.totalPendente(admId));
        model.addAttribute("planoNome", "Plano Profissional");
        model.addAttribute("planoVencimento", "15/10/2026");
        model.addAttribute("advocacia", segmento == SeguimentoTipo.ADVOCACIA);
        model.addAttribute("metricasJuridicas", processoService.obterMetricas(admId));
    }

}