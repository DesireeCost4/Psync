package com.example.psi.controller;

import com.example.psi.model.ClienteAdm;
import com.example.psi.model.ClienteFinal;
import com.example.psi.model.DadosCadastro;
import com.example.psi.model.Mensalidade;
import com.example.psi.model.SeguimentoTipo;
import com.example.psi.model.StatusFinanceiro;
import com.example.psi.repository.StatusFinanceiroProjection;
import com.example.psi.model.Agendamento;
import com.example.psi.model.Processo;
import com.example.psi.service.AgendamentoService;
import com.example.psi.repository.ProcessoRepository;
import com.example.psi.service.ClienteFinalService;
import com.example.psi.service.MensalidadeService;
import com.example.psi.service.ProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Objects;

@Controller
public class ClienteFinalController {

    @Autowired
    private ClienteFinalService clienteFinalService;

    @Autowired
    private MensalidadeService mensalidadeService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ProfissionalService profissionalService;

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private ProcessoRepository processoRepository;

    // 1. Rota Única para Detalhes / Prontuário do Paciente
    @GetMapping({"/detalhes-cliente", "/detalhe-clientes"})
    public String detalhesCliente(@RequestParam(name = "id", required = false) Long id,
                                  Authentication authentication,
                                  Model model) {

        Long admId = profissionalService.obterId(authentication.getName());
        SeguimentoTipo segmentoAtivo = profissionalService.obterSegmento(authentication.getName());

        ClienteFinal cliente = (id != null)
                ? clienteFinalService.buscarPorId(id).orElse(new ClienteFinal())
                : new ClienteFinal();
        if (cliente.getId() != null && (cliente.getClienteAdm() == null
                || !admId.equals(cliente.getClienteAdm().getId()))) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        model.addAttribute("cliente", cliente);
        model.addAttribute("segmento", segmentoAtivo);
        model.addAttribute("mensalidades", List.of());
        model.addAttribute("statusFinanceiro", StatusFinanceiro.EM_DIA);
        if (cliente.getId() != null) {
            model.addAttribute("mensalidades",
                    mensalidadeService.listarPorCliente(cliente.getId(), admId));
            StatusFinanceiro statusFinanceiro = mensalidadeService.statusPorClientes(admId).stream()
                    .filter(status -> Objects.equals(status.getClienteId(), cliente.getId()))
                    .map(StatusFinanceiroProjection::getStatus)
                    .map(StatusFinanceiro::valueOf)
                    .findFirst()
                    .orElse(StatusFinanceiro.EM_DIA);
            model.addAttribute("statusFinanceiro", statusFinanceiro);
            model.addAttribute("agendamentos",
                    agendamentoService.listarPorCliente(cliente.getId(), admId));
            model.addAttribute("processos",
                    processoRepository.findByClienteFinalIdAndClienteAdmIdOrderByUltimaMovimentacaoDesc(
                            cliente.getId(), admId));
        } else {
            model.addAttribute("agendamentos", List.of());
            model.addAttribute("processos", List.of());
        }

        return "detalhes-clienteFinal";
    }

    @PostMapping("/clientes/{clienteId}/mensalidades")
    public String criarMensalidade(@PathVariable Long clienteId,
                                   @ModelAttribute Mensalidade mensalidade,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        Long admId = profissionalService.obterId(authentication.getName());
        try {
            mensalidadeService.criar(clienteId, admId, mensalidade);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Mensalidade criada com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/detalhes-cliente?id=" + clienteId;
    }

    @PostMapping("/mensalidades/{id}/pagar")
    public String pagarMensalidade(@PathVariable Long id,
                                   @RequestParam Long clienteId,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        Long admId = profissionalService.obterId(authentication.getName());
        try {
            mensalidadeService.marcarComoPaga(id, admId);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Mensalidade baixada com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/detalhes-cliente?id=" + clienteId;
    }

    @PostMapping("/mensalidades/{id}/deletar")
    public String deletarMensalidade(@PathVariable Long id,
                                     @RequestParam Long clienteId,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        Long admId = profissionalService.obterId(authentication.getName());
        try {
            mensalidadeService.deletar(id, admId);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Mensalidade excluída com sucesso.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }
        return "redirect:/detalhes-cliente?id=" + clienteId;
    }

    // 2. Form de Cadastro Novo
    @GetMapping("/add-cliente")
    public String exibirFormulario(Authentication authentication,
                                   Model model) {

        SeguimentoTipo segmentoAtivo = profissionalService.obterSegmento(authentication.getName());

        ClienteFinal cliente = new ClienteFinal();
        cliente.setDadosCadastro(new DadosCadastro());

        model.addAttribute("clienteFinal", cliente);
        model.addAttribute("segmento", segmentoAtivo);

        return "add-clienteFinal";
    }

    // 3. Form de Edição de Paciente Existente
    @GetMapping("/editar-cliente")
    public String editarCliente(@RequestParam("id") Long id,
                                Authentication authentication,
                                Model model) {

        SeguimentoTipo segmentoAtivo = profissionalService.obterSegmento(authentication.getName());

        // Trata o Optional retornado pelo Service
        ClienteFinal cliente = clienteFinalService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com o ID: " + id));
        Long admId = profissionalService.obterId(authentication.getName());
        if (cliente.getClienteAdm() == null || !admId.equals(cliente.getClienteAdm().getId())) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }

        model.addAttribute("clienteFinal", cliente);
        model.addAttribute("segmento", segmentoAtivo);

        return "add-clienteFinal"; // Aponta para o template correto
    }

    // 4. Salvar / Atualizar
    @PostMapping("/salvar-cliente")
    public String salvarCliente(@ModelAttribute("clienteFinal") ClienteFinal clienteFinal,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        Long admId = profissionalService.obterId(authentication.getName());

        // 1. Garante que o ClienteAdm (mock do utilizador logado) está associado
        if (clienteFinal.getClienteAdm() == null || clienteFinal.getClienteAdm().getId() == null) {
            ClienteAdm admMock = new ClienteAdm();
            admMock.setId(admId);
            clienteFinal.setClienteAdm(admMock);
        }

        // 2. Se for uma edição e o id de clienteFinal já existir, garante a associação de DadosCadastro
        if (clienteFinal.getId() != null) {
            clienteFinalService.buscarPorId(clienteFinal.getId()).ifPresent(clienteExistente -> {
                if (clienteExistente.getClienteAdm() == null
                        || !admId.equals(clienteExistente.getClienteAdm().getId())) {
                    throw new IllegalArgumentException("Cliente não encontrado.");
                }
                if (clienteExistente.getDadosCadastro() != null && clienteFinal.getDadosCadastro() != null) {
                    clienteFinal.getDadosCadastro().setId(clienteExistente.getDadosCadastro().getId());
                }
            });
        }

        try {
            if (clienteFinal.getSenhaAcesso() != null && !clienteFinal.getSenhaAcesso().isBlank()) {
                clienteFinal.setSenha(passwordEncoder.encode(clienteFinal.getSenhaAcesso()));
            } else if (clienteFinal.getId() != null) {
                clienteFinalService.buscarPorId(clienteFinal.getId())
                        .ifPresent(clienteExistente -> clienteFinal.setSenha(clienteExistente.getSenha()));
            }
            clienteFinalService.salvar(clienteFinal);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Cadastro salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar cadastro: " + e.getMessage());
        }

        return "redirect:/home";
    }

    // 5. Excluir Paciente
    @PostMapping("/deletar-cliente/{id}")
    public String deletarCliente(@PathVariable("id") Long id,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            Long admId = profissionalService.obterId(authentication.getName());
            clienteFinalService.deletar(id, admId);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Paciente/Cliente excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao excluir o registro.");
        }
        return "redirect:/home";
    }
}