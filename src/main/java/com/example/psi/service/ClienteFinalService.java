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

    public Optional<ClienteFinal> buscarPorEmail(String email) {
        return email == null || email.isBlank()
                ? Optional.empty()
                : clienteFinalRepository.findByEmailIgnoreCase(email.trim());
    }

    public ClienteFinal salvar(ClienteFinal clienteFinal) {
        return clienteFinalRepository.save(clienteFinal);
    }

    public void deletar(Long id) {
        clienteFinalRepository.deleteById(id);
    }

    public void deletar(Long id, Long clienteAdmId) {
        ClienteFinal cliente = clienteFinalRepository.findById(id)
                .filter(item -> item.getClienteAdm() != null
                        && clienteAdmId.equals(item.getClienteAdm().getId()))
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        clienteFinalRepository.delete(cliente);
    }

    private String normalizarFiltro(String filtro) {
        return filtro == null || filtro.isBlank() ? null : filtro.trim();
    }
}