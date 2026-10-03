package com.example.psi.controller;

import com.example.psi.model.ClienteFinal;
import com.example.psi.model.SeguimentoTipo;
import com.example.psi.service.ClienteFinalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ClienteFinalService clienteFinalService;

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
        List<ClienteFinal> listaClientes = clienteFinalService.listarPorClienteAdm(admLogadoId);

        model.addAttribute("clientes", listaClientes);
        model.addAttribute("segmento", segmentoAtivo);
        model.addAttribute("totalClientes", listaClientes.size());
        model.addAttribute("proximosAtendimentos", 0);
        model.addAttribute("mensalidadesPendentes", "R$ 0,00");
        model.addAttribute("planoNome", "Plano Profissional");
        model.addAttribute("planoVencimento", "15/10/2026");

        return "home";
    }

}