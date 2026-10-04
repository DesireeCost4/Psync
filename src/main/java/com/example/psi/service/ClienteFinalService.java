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

    public Optional<ClienteFinal> buscarPorId(Long id) {
        return clienteFinalRepository.findById(id);
    }

    public ClienteFinal salvar(ClienteFinal clienteFinal) {
        return clienteFinalRepository.save(clienteFinal);
    }

    public void deletar(Long id) {
        clienteFinalRepository.deleteById(id);
    }

    public List<ClienteFinal> buscarPorNome(String nome) {

        // Se o termo for nulo ou composto apenas por espaços (blank)
            if (nome == null || nome.isBlank()) {

                return clienteFinalRepository.findAll();

            }

            return clienteFinalRepository.findByDadosCadastroNomeContainingIgnoreCase(nome.trim());

    }
}