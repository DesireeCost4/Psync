package com.example.psi.controller;

import com.example.psi.model.ClienteFinal;
import com.example.psi.model.SeguimentoTipo;
import com.example.psi.model.StatusFinanceiro;
import com.example.psi.repository.StatusFinanceiroProjection;
import com.example.psi.service.ClienteFinalService;
import com.example.psi.service.MensalidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ClienteFinalService clienteFinalService;

    @Autowired
    private MensalidadeService mensalidadeService;

    private SeguimentoTipo obterSegmento(String segmentoParam) {
        try {
            return SeguimentoTipo.valueOf(segmentoParam.toUpperCase());
        } catch (Exception e) {
            return SeguimentoTipo.PSICOLOGIA;
        }
    }

    @GetMapping("/")
    public String landingPage() {
        return "index"; // Aponta para index.html na pasta templates
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastroPsync(){
        return "cadastro-psync" ;
    }

    @GetMapping("/home")
    public String painellogado(@RequestParam(name = "segmento", defaultValue = "PSICOLOGIA") String segmentoParam,
                               Model model) {

        SeguimentoTipo segmentoAtivo = obterSegmento(segmentoParam);

        // ID fixo do profissional logado no momento >>>>> (Mock)
        Long admLogadoId = 1L;

        // Busca apenas os clientes do ADM 1
        List<ClienteFinal> listaClientes = clienteFinalService.buscarPorFiltros(admLogadoId, null, null, null);

        adicionarDadosDoPainel(model, listaClientes, segmentoAtivo, admLogadoId);

        return "home";
    }

    @GetMapping("/clientes")
    public String listarClientes(@RequestParam(name = "nome", required = false) String nome,
                                 @RequestParam(name = "cpfCnpj", required = false) String cpfCnpj,
                                 @RequestParam(name = "email", required = false) String email,
                                 @RequestParam(name = "segmento", defaultValue = "PSICOLOGIA") String segmentoParam,
                                 Model model) {

        // 1. Recuperar o segmento ativo para renderizar as variáveis da home.html
        SeguimentoTipo segmentoAtivo = obterSegmento(segmentoParam);

        // 2. ID do ADM logado (mock)
        Long admLogadoId = 1L;

        List<ClienteFinal> listaDeClientes = clienteFinalService.buscarPorFiltros(
                admLogadoId, nome, cpfCnpj, email
        );

        adicionarDadosDoPainel(model, listaDeClientes, segmentoAtivo, admLogadoId);
        model.addAttribute("filtroNome", nome);
        model.addAttribute("filtroCpfCnpj", cpfCnpj);
        model.addAttribute("filtroEmail", email);

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
    }

}