package com.example.psi.controller;

import com.example.psi.model.ClienteFinalDashboard;
import com.example.psi.service.ClienteFinalDashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClienteFinalDashboardController {

    private final ClienteFinalDashboardService dashboardService;

    public ClienteFinalDashboardController(ClienteFinalDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/cliente/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        ClienteFinalDashboard dashboard = dashboardService.carregar(authentication.getName());
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("cliente", dashboard.cliente());
        model.addAttribute("agendamentos", dashboard.agendamentos());
        model.addAttribute("mensalidades", dashboard.mensalidades());
        model.addAttribute("statusFinanceiro", dashboard.statusFinanceiro());
        return "cliente-dashboard";
    }
}
