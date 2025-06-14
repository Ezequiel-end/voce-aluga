package com.vocealuga.model;

import jakarta.persistence.*;

@Entity
@Table(name = "filial")
public class Filial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdFilial")
    private Integer idFilial;

    @Column(name = "Nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "Endereco", nullable = false, length = 255)
    private String endereco;

    @Column(name = "Capacidade", nullable = false)
    private Integer capacidade;

    public Filial() {
    }

    public Filial(String nome, String endereco, Integer capacidade) {
        this.nome = nome;
        this.endereco = endereco;
        this.capacidade = capacidade;
    }

    // Getters and Setters
    public Integer getIdFilial() { return idFilial; }
    public void setIdFilial(Integer idFilial) { this.idFilial = idFilial; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }

    @Override
    public String toString() {
        return "Filial{" +
               "idFilial=" + idFilial +
               ", nome='" + nome + '\'' +
               ", endereco='" + endereco + '\'' +
               ", capacidade=" + capacidade +
               '}';
    }
}