package com.example.psi.model;

public enum SeguimentoTipo {

    PSICOLOGIA("Psicologia", "Paciente", "Sessão"),
    ODONTOLOGIA("Odontologia", "Paciente", "Consulta"),
    MEDICINA("Medicina", "Paciente", "Consulta"),
    ADVOCACIA("Advocacia", "Cliente", "Atendimento"),
    OUTRO("Geral", "Cliente", "Atendimento");

    private final String descricao;
    private final String termoCliente;   // Ex: Paciente ou Cliente
    private final String termoAtendimento; // Ex: Sessão ou Consulta

    SeguimentoTipo(String descricao, String termoCliente, String termoAtendimento) {
        this.descricao = descricao;
        this.termoCliente = termoCliente;
        this.termoAtendimento = termoAtendimento;
    }

    public String getDescricao() { return descricao; }
    public String getTermoCliente() { return termoCliente; }
    public String getTermoAtendimento() { return termoAtendimento; }

}
