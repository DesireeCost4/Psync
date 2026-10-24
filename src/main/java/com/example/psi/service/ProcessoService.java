package com.example.psi.service;

import com.example.psi.model.StatusProcesso;
import com.example.psi.repository.ProcessoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ProcessoService {

    private final ProcessoRepository processoRepository;

    public ProcessoService(ProcessoRepository processoRepository) {
        this.processoRepository = processoRepository;
    }

    public MetricasJuridicas obterMetricas(Long admId) {
        return new MetricasJuridicas(
                processoRepository.countPrazosUrgentes(
                        admId, StatusProcesso.ATIVO, LocalDate.now().plusDays(7)),
                processoRepository.countProcessosParados(
                        admId, StatusProcesso.ATIVO, LocalDate.now().minusDays(30)),
                processoRepository.countByClienteAdmIdAndStatusAndIntimacaoPendenteTrue(
                        admId, StatusProcesso.ATIVO),
                processoRepository.countByClienteAdmIdAndStatusAndPublicacaoPendenteTrue(
                        admId, StatusProcesso.ATIVO),
                processoRepository.somarHonorariosAReceber(admId)
        );
    }

    public record MetricasJuridicas(
            long prazosProximos,
            long processosParados,
            long intimacoesPendentes,
            long publicacoesPendentes,
            BigDecimal honorariosAReceber) {
    }
}
