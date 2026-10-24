package com.example.psi.controller;

import com.example.psi.model.Agendamento;
import com.example.psi.model.StatusAgendamento;
import com.example.psi.service.AgendamentoService;
import com.example.psi.service.ClienteFinalService;
import com.example.psi.service.ProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;

@Controller
public class AgendaController {

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private ClienteFinalService clienteFinalService;
    
    @Autowired
    private ProfissionalService profissionalService;

    @GetMapping("/agenda")
    public String agenda(@RequestParam(name = "data", required = false) LocalDate data,
                         Authentication authentication,
                         Model model) {
        Long admId = profissionalService.obterId(authentication.getName());
        LocalDate dataSelecionada = data == null ? LocalDate.now() : data;
        model.addAttribute("agendamentos", agendamentoService.listar(admId, dataSelecionada));
        model.addAttribute("clientes", clienteFinalService.listarPorClienteAdm(admId));
        model.addAttribute("dataSelecionada", dataSelecionada);
        model.addAttribute("statusAgendamento", StatusAgendamento.values());
        model.addAttribute("novoAgendamento", new Agendamento());
        return "agenda";
    }

    @PostMapping("/agenda")
    public String salvar(@ModelAttribute("novoAgendamento") Agendamento agendamento,
                         @RequestParam Long clienteId,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        Long admId = profissionalService.obterId(authentication.getName());
        try {
            agendamentoService.salvar(clienteId, admId, agendamento);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Agendamento salvo com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/agenda";
    }

    @PostMapping("/agenda/{id}/status")
    public String atualizarStatus(@PathVariable Long id,
                                  @RequestParam StatusAgendamento status,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        Long admId = profissionalService.obterId(authentication.getName());
        try {
            agendamentoService.atualizarStatus(id, admId, status);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Status atualizado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/agenda";
    }

    @PostMapping("/agenda/{id}/deletar")
    public String excluir(@PathVariable Long id,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        Long admId = profissionalService.obterId(authentication.getName());
        try {
            agendamentoService.excluir(id, admId);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Agendamento excluído.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/agenda";
    }
}
