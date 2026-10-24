package com.example.psi.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tb_dados_cadastro")
public class DadosCadastro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String cpfCnpj;
    private String telefone;
    private String endereco;

    private LocalDate dataNascimento;
    private String profissao;
    private String contatoEmergencia;
    private String telefoneEmergencia;
    private String observacoesIniciais;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public String getContatoEmergencia() {
        return contatoEmergencia;
    }

    public void setContatoEmergencia(String contatoEmergencia) {
        this.contatoEmergencia = contatoEmergencia;
    }

    public String getTelefoneEmergencia() {
        return telefoneEmergencia;
    }

    public void setTelefoneEmergencia(String telefoneEmergencia) {
        this.telefoneEmergencia = telefoneEmergencia;
    }

    public String getObservacoesIniciais() {
        return observacoesIniciais;
    }

    public void setObservacoesIniciais(String observacoesIniciais) {
        this.observacoesIniciais = observacoesIniciais;
    }
}