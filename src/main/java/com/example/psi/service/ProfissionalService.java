package com.example.psi.service;

import com.example.psi.model.ClienteAdm;
import com.example.psi.model.SeguimentoTipo;
import com.example.psi.repository.ClienteAdmRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProfissionalService {

    private final ClienteAdmRepository clienteAdmRepository;

    public ProfissionalService(ClienteAdmRepository clienteAdmRepository) {
        this.clienteAdmRepository = clienteAdmRepository;
    }

    public ClienteAdm autenticado(String email) {
        return clienteAdmRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Profissional não encontrado."));
    }

    public Long obterId(String email) {
        return autenticado(email).getId();
    }

    public SeguimentoTipo obterSegmento(String email) {
        ClienteAdm profissional = autenticado(email);
        String nomeSegmento = profissional.getSegmento() == null
                ? ""
                : profissional.getSegmento().getNome();
        try {
            return SeguimentoTipo.valueOf(nomeSegmento.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Segmento do profissional não configurado.");
        }
    }
}
