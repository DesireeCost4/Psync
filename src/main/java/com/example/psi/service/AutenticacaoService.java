package com.example.psi.service;

import com.example.psi.model.CadastroProfissional;
import com.example.psi.model.ClienteAdm;
import com.example.psi.model.DadosCadastro;
import com.example.psi.model.SegmentoAdm;
import com.example.psi.model.SeguimentoTipo;
import com.example.psi.repository.ClienteAdmRepository;
import com.example.psi.repository.SegmentoAdmRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AutenticacaoService {

    private final ClienteAdmRepository clienteAdmRepository;
    private final SegmentoAdmRepository segmentoAdmRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AutenticacaoService(ClienteAdmRepository clienteAdmRepository,
                               SegmentoAdmRepository segmentoAdmRepository,
                               BCryptPasswordEncoder passwordEncoder) {
        this.clienteAdmRepository = clienteAdmRepository;
        this.segmentoAdmRepository = segmentoAdmRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void cadastrarProfissional(CadastroProfissional cadastro) {
        String email = normalizarEmail(cadastro.getEmail());
        validar(cadastro, email);

        if (clienteAdmRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalArgumentException("Este e-mail já está cadastrado.");
        }

        SeguimentoTipo tipo = converterSegmento(cadastro.getSeguimentoTipo());
        SegmentoAdm segmento = segmentoAdmRepository.findByNomeIgnoreCase(tipo.name())
                .orElseGet(() -> criarSegmento(tipo));

        DadosCadastro dados = new DadosCadastro();
        dados.setNome(cadastro.getNome().trim());
        dados.setCpfCnpj(cadastro.getDocumento().trim());
        dados.setTelefone(cadastro.getTelefone().trim());
        dados.setRazaoSocial(normalizar(cadastro.getRazaoSocial()));

        ClienteAdm profissional = new ClienteAdm();
        profissional.setEmail(email);
        String senhaHash = passwordEncoder.encode(cadastro.getPassword());
        if (!passwordEncoder.matches(cadastro.getPassword(), senhaHash)) {
            throw new IllegalStateException("Não foi possível validar a senha cadastrada.");
        }
        profissional.setSenha(senhaHash);
        profissional.setSegmento(segmento);
        profissional.setDadosCadastro(dados);
        clienteAdmRepository.save(profissional);
    }

    private SegmentoAdm criarSegmento(SeguimentoTipo tipo) {
        SegmentoAdm segmento = new SegmentoAdm();
        segmento.setNome(tipo.name());
        return segmentoAdmRepository.save(segmento);
    }

    private void validar(CadastroProfissional cadastro, String email) {
        if (vazio(cadastro.getNome()) || vazio(email) || vazio(cadastro.getTelefone())
                || vazio(cadastro.getDocumento()) || vazio(cadastro.getPassword())
                || vazio(cadastro.getSeguimentoTipo())) {
            throw new IllegalArgumentException("Preencha todos os campos obrigatórios.");
        }
        if (!email.contains("@") || email.length() > 150) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        if (cadastro.getPassword().length() < 8) {
            throw new IllegalArgumentException("A senha deve possuir pelo menos 8 caracteres.");
        }
    }

    private SeguimentoTipo converterSegmento(String valor) {
        try {
            return SeguimentoTipo.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Selecione um segmento válido.");
        }
    }

    private String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizar(String valor) {
        return vazio(valor) ? null : valor.trim();
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
