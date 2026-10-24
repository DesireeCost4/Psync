package com.example.psi.service;

import com.example.psi.model.ClienteFinal;
import com.example.psi.repository.ClienteFinalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteFinalService {

    @Autowired
    private ClienteFinalRepository clienteFinalRepository;

    public List<ClienteFinal> listarTodos() {
        return clienteFinalRepository.findAll();
    }

    public List<ClienteFinal> listarPorClienteAdm(Long clienteAdmId) {
        return clienteFinalRepository.findByClienteAdmId(clienteAdmId);
    }

    public List<ClienteFinal> buscarPorFiltros(Long clienteAdmId, String nome, String cpfCnpj, String email) {
        return clienteFinalRepository.buscarComFiltros(
                clienteAdmId,
                normalizarFiltro(nome),
                normalizarFiltro(cpfCnpj),
                normalizarFiltro(email)
        );
    }

    public Optional<ClienteFinal> buscarPorId(Long id) {
        return clienteFinalRepository.findById(id);
    }

    public ClienteFinal salvar(ClienteFinal clienteFinal) {
        return clienteFinalRepository.save(clienteFinal);
    }

    public void deletar(Long id) {
        clienteFinalRepository.deleteById(id);
    }

    private String normalizarFiltro(String filtro) {
        return filtro == null || filtro.isBlank() ? null : filtro.trim();
    }
}