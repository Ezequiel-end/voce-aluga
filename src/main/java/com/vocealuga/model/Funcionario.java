package com.vocealuga.model;

import jakarta.persistence.*;

@Entity
@Table(name = "funcionario")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdFuncionario")
    private Integer idFuncionario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdFilial", nullable = false)
    private Filial filial; // FK to filial

    @Column(name = "CPF", nullable = false, unique = true, length = 50)
    private String cpf;

    @Column(name = "Nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "Email", nullable = false, length = 255)
    private String email;

    @Column(name = "Senha", nullable = false, length = 50)
    private String senha;

    public Funcionario() {
    }

    public Funcionario(Filial filial, String cpf, String nome, String email, String senha) {
        this.filial = filial;
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // Getters and Setters
    public Integer getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Integer idFuncionario) { this.idFuncionario = idFuncionario; }
    public Filial getFilial() { return filial; }
    public void setFilial(Filial filial) { this.filial = filial; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    @Override
    public String toString() {
        return "Funcionario{" +
               "idFuncionario=" + idFuncionario +
               ", filial=" + (filial != null ? filial.getIdFilial() : "null") +
               ", cpf='" + cpf + '\'' +
               ", nome='" + nome + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}