package com.example.psi.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_processo")
public class Processo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String numero;

    @Column(nullable = false, length = 200)
    private String titulo;

    private LocalDate proximoPrazo;
    private LocalDate ultimaMovimentacao;
    private boolean intimacaoPendente;
    private boolean publicacaoPendente;

    @Column(precision = 10, scale = 2)
    private BigDecimal honorariosAReceber = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusProcesso status = StatusProcesso.ATIVO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_adm_id", nullable = false)
    private ClienteAdm clienteAdm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_final_id", nullable = false)
    private ClienteFinal clienteFinal;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public LocalDate getProximoPrazo() { return proximoPrazo; }
    public void setProximoPrazo(LocalDate proximoPrazo) { this.proximoPrazo = proximoPrazo; }
    public LocalDate getUltimaMovimentacao() { return ultimaMovimentacao; }
    public void setUltimaMovimentacao(LocalDate ultimaMovimentacao) { this.ultimaMovimentacao = ultimaMovimentacao; }
    public boolean isIntimacaoPendente() { return intimacaoPendente; }
    public void setIntimacaoPendente(boolean intimacaoPendente) { this.intimacaoPendente = intimacaoPendente; }
    public boolean isPublicacaoPendente() { return publicacaoPendente; }
    public void setPublicacaoPendente(boolean publicacaoPendente) { this.publicacaoPendente = publicacaoPendente; }
    public BigDecimal getHonorariosAReceber() { return honorariosAReceber; }
    public void setHonorariosAReceber(BigDecimal honorariosAReceber) { this.honorariosAReceber = honorariosAReceber; }
    public StatusProcesso getStatus() { return status; }
    public void setStatus(StatusProcesso status) { this.status = status; }
    public ClienteAdm getClienteAdm() { return clienteAdm; }
    public void setClienteAdm(ClienteAdm clienteAdm) { this.clienteAdm = clienteAdm; }
    public ClienteFinal getClienteFinal() { return clienteFinal; }
    public void setClienteFinal(ClienteFinal clienteFinal) { this.clienteFinal = clienteFinal; }
}
