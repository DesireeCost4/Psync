package com.example.psi.controller;

import com.example.psi.model.CadastroProfissional;
import com.example.psi.service.AutenticacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        if (!model.containsAttribute("cadastro")) {
            model.addAttribute("cadastro", new CadastroProfissional());
        }
        return "cadastro-psync";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@ModelAttribute("cadastro") CadastroProfissional cadastro,
                            RedirectAttributes redirectAttributes) {
        try {
            autenticacaoService.cadastrarProfissional(cadastro);
            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    "Conta criada com sucesso. Faça login para continuar.");
            return "redirect:/login";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("mensagemErro", exception.getMessage());
            redirectAttributes.addFlashAttribute("cadastro", cadastro);
            return "redirect:/cadastro";
        }
    }
}
