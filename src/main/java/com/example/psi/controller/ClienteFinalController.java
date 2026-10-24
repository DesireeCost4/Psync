package com.example.psi.controller;

import com.example.psi.model.ClienteAdm;
import com.example.psi.model.ClienteFinal;
import com.example.psi.model.DadosCadastro;
import com.example.psi.model.SeguimentoTipo;
import com.example.psi.service.ClienteFinalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClienteFinalController {

    @Autowired
    private ClienteFinalService clienteFinalService;

    private SeguimentoTipo obterSegmento(String segmentoParam) {
        try {
            return SeguimentoTipo.valueOf(segmentoParam.toUpperCase());
        } catch (Exception e) {
            return SeguimentoTipo.PSICOLOGIA;
        }
    }

    // 1. Rota Única para Detalhes / Prontuário do Paciente
    @GetMapping("/detalhes-cliente")
    public String detalhesCliente(@RequestParam(name = "id", required = false) Long id,
                                  @RequestParam(name = "segmento", defaultValue = "PSICOLOGIA") String segmentoParam,
                                  Model model) {

        SeguimentoTipo segmentoAtivo = obterSegmento(segmentoParam);

        ClienteFinal cliente = (id != null)
                ? clienteFinalService.buscarPorId(id).orElse(new ClienteFinal())
                : new ClienteFinal();

        model.addAttribute("cliente", cliente);
        model.addAttribute("segmento", segmentoAtivo);

        return "detalhes-clienteFinal";
    }

    // 2. Form de Cadastro Novo
    @GetMapping("/add-cliente")
    public String exibirFormulario(@RequestParam(name = "segmento", defaultValue = "PSICOLOGIA") String segmentoParam,
                                   Model model) {

        SeguimentoTipo segmentoAtivo = obterSegmento(segmentoParam);

        ClienteFinal cliente = new ClienteFinal();
        cliente.setDadosCadastro(new DadosCadastro());

        model.addAttribute("clienteFinal", cliente);
        model.addAttribute("segmento", segmentoAtivo);

        return "add-clienteFinal";
    }

    // 3. Form de Edição de Paciente Existente
    @GetMapping("/editar-cliente")
    public String editarCliente(@RequestParam("id") Long id,
                                @RequestParam(name = "segmento", defaultValue = "PSICOLOGIA") String segmentoParam,
                                Model model) {

        SeguimentoTipo segmentoAtivo = obterSegmento(segmentoParam);

        // Trata o Optional retornado pelo Service
        ClienteFinal cliente = clienteFinalService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com o ID: " + id));

        model.addAttribute("clienteFinal", cliente);
        model.addAttribute("segmento", segmentoAtivo);

        return "add-clienteFinal"; // Aponta para o template correto
    }

    // 4. Salvar / Atualizar
    @PostMapping("/salvar-cliente")
    public String salvarCliente(@ModelAttribute("clienteFinal") ClienteFinal clienteFinal,
                                @RequestParam(name = "segmento", defaultValue = "PSICOLOGIA") String segmentoParam,
                                RedirectAttributes redirectAttributes) {

        // 1. Garante que o ClienteAdm (mock do utilizador logado) está associado
        if (clienteFinal.getClienteAdm() == null || clienteFinal.getClienteAdm().getId() == null) {
            ClienteAdm admMock = new ClienteAdm();
            admMock.setId(1L);
            clienteFinal.setClienteAdm(admMock);
        }

        // 2. Se for uma edição e o id de clienteFinal já existir, garante a associação de DadosCadastro
        if (clienteFinal.getId() != null) {
            clienteFinalService.buscarPorId(clienteFinal.getId()).ifPresent(clienteExistente -> {
                if (clienteExistente.getDadosCadastro() != null && clienteFinal.getDadosCadastro() != null) {
                    clienteFinal.getDadosCadastro().setId(clienteExistente.getDadosCadastro().getId());
                }
            });
        }

        try {
            clienteFinalService.salvar(clienteFinal);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Cadastro salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar cadastro: " + e.getMessage());
        }

        return "redirect:/home?segmento=" + segmentoParam;
    }

    // 5. Excluir Paciente
    @PostMapping("/deletar-cliente/{id}")
    public String deletarCliente(@PathVariable("id") Long id,
                                 @RequestParam(name = "segmento", defaultValue = "PSICOLOGIA") String segmentoParam,
                                 RedirectAttributes redirectAttributes) {
        try {
            clienteFinalService.deletar(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Paciente/Cliente excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao excluir o registro.");
        }
        return "redirect:/home?segmento=" + segmentoParam;
    }
}