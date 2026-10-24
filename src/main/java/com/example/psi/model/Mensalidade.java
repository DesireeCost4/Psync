package com.example.psi.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_mensalidade")
public class Mensalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMensalidade status = StatusMensalidade.PENDENTE;

    @ManyToOne
    @JoinColumn(name = "cliente_adm_id")
    private ClienteAdm clienteAdm;

    @ManyToOne
    @JoinColumn(name = "cliente_final_id")
    private ClienteFinal clienteFinal;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
    public StatusMensalidade getStatus() { return status; }
    public void setStatus(StatusMensalidade status) { this.status = status; }
    public ClienteAdm getClienteAdm() { return clienteAdm; }
    public void setClienteAdm(ClienteAdm clienteAdm) { this.clienteAdm = clienteAdm; }
    public ClienteFinal getClienteFinal() { return clienteFinal; }
    public void setClienteFinal(ClienteFinal clienteFinal) { this.clienteFinal = clienteFinal; }
}