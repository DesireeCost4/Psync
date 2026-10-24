package com.example.psi.model;

import java.util.List;

public record ClienteFinalDashboard(
        ClienteFinal cliente,
        List<Agendamento> agendamentos,
        List<Mensalidade> mensalidades,
        StatusFinanceiro statusFinanceiro) {
}
