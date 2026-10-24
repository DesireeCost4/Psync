package com.example.psi.repository;

import com.example.psi.model.Processo;
import com.example.psi.model.StatusProcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProcessoRepository extends JpaRepository<Processo, Long> {

    List<Processo> findByClienteFinalIdAndClienteAdmIdOrderByUltimaMovimentacaoDesc(
            Long clienteFinalId, Long clienteAdmId);

    @Query("""
            SELECT COUNT(p)
            FROM Processo p
            WHERE p.clienteAdm.id = :admId
              AND p.status = :status
              AND p.proximoPrazo IS NOT NULL
              AND p.proximoPrazo <= :limite
            """)
    long countPrazosUrgentes(@Param("admId") Long admId,
                             @Param("status") StatusProcesso status,
                             @Param("limite") LocalDate limite);

    @Query("""
            SELECT COUNT(p)
            FROM Processo p
            WHERE p.clienteAdm.id = :admId
              AND p.status = :status
              AND (p.ultimaMovimentacao IS NULL OR p.ultimaMovimentacao < :limite)
            """)
    long countProcessosParados(@Param("admId") Long admId,
                               @Param("status") StatusProcesso status,
                               @Param("limite") LocalDate limite);

    long countByClienteAdmIdAndStatusAndIntimacaoPendenteTrue(Long admId, StatusProcesso status);

    long countByClienteAdmIdAndStatusAndPublicacaoPendenteTrue(Long admId, StatusProcesso status);

    @Query("""
            SELECT COALESCE(SUM(p.honorariosAReceber), 0)
            FROM Processo p
            WHERE p.clienteAdm.id = :admId
              AND p.status = com.example.psi.model.StatusProcesso.ATIVO
            """)
    BigDecimal somarHonorariosAReceber(@Param("admId") Long admId);
}
