package com.example.psi.repository;

import com.example.psi.model.Agendamento;
import com.example.psi.model.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByClienteAdmIdAndInicioBetweenOrderByInicio(
            Long clienteAdmId, LocalDateTime inicio, LocalDateTime fim);

    List<Agendamento> findByClienteAdmIdOrderByInicioAsc(Long clienteAdmId);

    @Query("""
            SELECT COUNT(a) > 0
            FROM Agendamento a
            WHERE a.clienteAdm.id = :admId
              AND a.status = com.example.psi.model.StatusAgendamento.AGENDADO
              AND (:id IS NULL OR a.id <> :id)
              AND a.inicio < :fim
              AND a.fim > :inicio
            """)
    boolean existeConflito(@Param("admId") Long admId,
                           @Param("id") Long id,
                           @Param("inicio") LocalDateTime inicio,
                           @Param("fim") LocalDateTime fim);

    List<Agendamento> findByClienteFinalIdAndClienteAdmIdOrderByInicioDesc(
            Long clienteFinalId, Long clienteAdmId);

    long countByClienteAdmIdAndInicioBetweenAndStatus(
            Long clienteAdmId, LocalDateTime inicio, LocalDateTime fim, StatusAgendamento status);
}
