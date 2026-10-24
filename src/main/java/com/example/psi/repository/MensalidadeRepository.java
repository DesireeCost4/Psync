package com.example.psi.repository;

import com.example.psi.model.Mensalidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MensalidadeRepository extends JpaRepository<Mensalidade, Long> {

    List<Mensalidade> findByClienteAdmId(Long clienteAdmId);
    List<Mensalidade> findByClienteFinalIdOrderByDataVencimentoDesc(Long clienteFinalId);

    @Query("""
            SELECT m.clienteFinal.id AS clienteId,
                   CASE
                       WHEN SUM(CASE WHEN m.status <> com.example.psi.model.StatusMensalidade.PAGO
                                          AND m.status <> com.example.psi.model.StatusMensalidade.CANCELADO
                                          AND m.dataVencimento < :hoje THEN 1 ELSE 0 END) > 0
                           THEN 'ATRASADA'
                       WHEN SUM(CASE WHEN m.status = com.example.psi.model.StatusMensalidade.PENDENTE
                                          THEN 1 ELSE 0 END) > 0
                           THEN 'PENDENTE'
                       ELSE 'EM_DIA'
                   END AS status
            FROM Mensalidade m
            WHERE m.clienteAdm.id = :clienteAdmId
            GROUP BY m.clienteFinal.id
            """)
    List<StatusFinanceiroProjection> buscarStatusFinanceiro(@Param("clienteAdmId") Long clienteAdmId,
                                                              @Param("hoje") LocalDate hoje);

    @Query("""
            SELECT COALESCE(SUM(m.valor), 0)
            FROM Mensalidade m
            WHERE m.clienteAdm.id = :clienteAdmId
              AND m.status = com.example.psi.model.StatusMensalidade.PENDENTE
            """)
    BigDecimal somarMensalidadesPendentes(@Param("clienteAdmId") Long clienteAdmId);
}