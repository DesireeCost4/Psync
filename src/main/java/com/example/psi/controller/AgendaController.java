package com.example.psi.controller;

import com.example.psi.model.Agendamento;
import com.example.psi.model.StatusAgendamento;
import com.example.psi.service.AgendamentoService;
import com.example.psi.service.ClienteFinalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class AgendaController {

    private static final Long ADM_LOGADO_ID = 1L;

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private ClienteFinalService clienteFinalService;

    @GetMapping("/agenda")
    public String agenda(@RequestParam(name = "data", required = false) LocalDate data,
                         @RequestParam(name = "segmento", defaultValue = "PSICOLOGIA") String segmento,
                         Model model) {
        LocalDate dataSelecionada = data == null ? LocalDate.now() : data;
        model.addAttribute("agendamentos", agendamentoService.listar(ADM_LOGADO_ID, dataSelecionada));
        model.addAttribute("clientes", clienteFinalService.listarPorClienteAdm(ADM_LOGADO_ID));
        model.addAttribute("dataSelecionada", dataSelecionada);
        model.addAttribute("segmento", segmento);
        model.addAttribute("statusAgendamento", StatusAgendamento.values());
        model.addAttribute("novoAgendamento", new Agendamento());
        return "agenda";
    }

    @PostMapping("/agenda")
    public String salvar(@ModelAttribute("novoAgendamento") Agendamento agendamento,
                         @RequestParam Long clienteId,
                         @RequestParam(name = "segmento", defaultValue = "PSICOLOGIA") String segmento,
                         RedirectAttributes redirectAttributes) {
        try {
            agendamentoService.salvar(clienteId, ADM_LOGADO_ID, agendamento);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Agendamento salvo com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/agenda?segmento=" + segmento;
    }

    @PostMapping("/agenda/{id}/status")
    public String atualizarStatus(@PathVariable Long id,
                                  @RequestParam StatusAgendamento status,
                                  @RequestParam(name = "segmento", defaultValue = "PSICOLOGIA") String segmento,
                                  RedirectAttributes redirectAttributes) {
        try {
            agendamentoService.atualizarStatus(id, ADM_LOGADO_ID, status);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Status atualizado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/agenda?segmento=" + segmento;
    }

    @PostMapping("/agenda/{id}/deletar")
    public String excluir(@PathVariable Long id,
                          @RequestParam(name = "segmento", defaultValue = "PSICOLOGIA") String segmento,
                          RedirectAttributes redirectAttributes) {
        try {
            agendamentoService.excluir(id, ADM_LOGADO_ID);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Agendamento excluído.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/agenda?segmento=" + segmento;
    }
}
