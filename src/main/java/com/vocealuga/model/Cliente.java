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

    public Cliente() {
    }

    public Cliente(String cpf, String nome, String email, String senha, String cnh, LocalDate dataNascimento) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cnh = cnh;
        this.dataNascimento = dataNascimento;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "Cliente{" +
               "idCliente=" + idCliente +
               ", cpf='" + cpf + '\'' +
               ", nome='" + nome + '\'' +
               ", email='" + email + '\'' +
               ", cnh='" + cnh + '\'' +
               ", dataNascimento=" + dataNascimento +
               '}';
    }
}