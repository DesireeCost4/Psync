package com.example.psi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_cliente_final")
public class ClienteFinal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @ManyToOne
    @JoinColumn(name = "cliente_adm_id", nullable = false)
    private ClienteAdm clienteAdm;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "dados_cadastro_id")
    private DadosCadastro dadosCadastro;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public ClienteAdm getClienteAdm() { return clienteAdm; }
    public void setClienteAdm(ClienteAdm clienteAdm) { this.clienteAdm = clienteAdm; }
    public DadosCadastro getDadosCadastro() { return dadosCadastro; }
    public void setDadosCadastro(DadosCadastro dadosCadastro) { this.dadosCadastro = dadosCadastro; }
}