package com.example.psi.repository;

import com.example.psi.model.ClienteFinal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteFinalRepository extends JpaRepository<ClienteFinal, Long> {

    // Busca todos os clientes finais pertencentes a um determinado ADM/Segmento
    List<ClienteFinal> findByClienteAdmId(Long clienteAdmId);

    List<ClienteFinal> findByDadosCadastroNomeContainingIgnoreCase(String nome);
}