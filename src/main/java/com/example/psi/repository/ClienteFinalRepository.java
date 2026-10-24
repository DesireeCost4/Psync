package com.example.psi.repository;

import com.example.psi.model.ClienteFinal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteFinalRepository extends JpaRepository<ClienteFinal, Long> {

    // Busca todos os clientes finais pertencentes a um determinado ADM/Segmento
    List<ClienteFinal> findByClienteAdmId(Long clienteAdmId);

    @Query("""
            SELECT cliente
            FROM ClienteFinal cliente
            JOIN cliente.dadosCadastro dados
            WHERE cliente.clienteAdm.id = :clienteAdmId
              AND (:nome IS NULL OR LOWER(dados.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
              AND (:cpfCnpj IS NULL OR LOWER(dados.cpfCnpj) LIKE LOWER(CONCAT('%', :cpfCnpj, '%')))
              AND (:email IS NULL OR LOWER(cliente.email) LIKE LOWER(CONCAT('%', :email, '%')))
            ORDER BY LOWER(dados.nome)
            """)
    List<ClienteFinal> buscarComFiltros(@Param("clienteAdmId") Long clienteAdmId,
                                        @Param("nome") String nome,
                                        @Param("cpfCnpj") String cpfCnpj,
                                        @Param("email") String email);
}