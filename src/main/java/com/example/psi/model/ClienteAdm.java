package com.example.psi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_cliente_adm")
public class ClienteAdm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @ManyToOne
    @JoinColumn(name = "segmento_id", nullable = false)
    private SegmentoAdm segmento;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dados_cadastro_id")
    private DadosCadastro dadosCadastro;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public SegmentoAdm getSegmento() { return segmento; }
    public void setSegmento(SegmentoAdm segmento) { this.segmento = segmento; }
    public DadosCadastro getDadosCadastro() { return dadosCadastro; }
    public void setDadosCadastro(DadosCadastro dadosCadastro) { this.dadosCadastro = dadosCadastro; }
}