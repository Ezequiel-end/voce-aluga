package com.vocealuga.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdCliente")
    private Integer idCliente;

    @Column(name = "CPF", nullable = false, unique = true, length = 50)
    private String cpf;

    @Column(name = "Nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "Email", nullable = false, length = 255)
    private String email;

    @Column(name = "Senha", nullable = false, length = 50)
    private String senha;

    @Column(name = "CNH", nullable = false, length = 50)
    private String cnh;

    @Column(name = "DataNascimento", nullable = false)
    private LocalDate dataNascimento;

    // NOVO CAMPO PARA FIDELIDADE
    @Column(name = "PontosFidelidade", nullable = false)
    private Integer pontosFidelidade;

    public Cliente() {
        // Inicializa pontos de fidelidade para 0 por padrão para novos clientes
        this.pontosFidelidade = 0;
    }

    public Cliente(String cpf, String nome, String email, String senha, String cnh, LocalDate dataNascimento) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cnh = cnh;
        this.dataNascimento = dataNascimento;
        // Inicializa pontos de fidelidade para 0 para novos clientes
        this.pontosFidelidade = 0;
    }

    // Getters and Setters existentes...
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getCnh() { return cnh; }
    public void setCnh(String cnh) { this.cnh = cnh; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    // NOVOS GETTER E SETTER PARA FIDELIDADE
    public Integer getPontosFidelidade() { return pontosFidelidade; }
    public void setPontosFidelidade(Integer pontosFidelidade) { this.pontosFidelidade = pontosFidelidade; }

    @Override
    public String toString() {
        return "Cliente{" +
               "idCliente=" + idCliente +
               ", cpf='" + cpf + '\'' +
               ", nome='" + nome + '\'' +
               ", email='" + email + '\'' +
               ", senha='" + senha + '\'' +
               ", cnh='" + cnh + '\'' +
               ", dataNascimento=" + dataNascimento +
               ", pontosFidelidade=" + pontosFidelidade + // Adicionado ao toString
               '}';
    }
}