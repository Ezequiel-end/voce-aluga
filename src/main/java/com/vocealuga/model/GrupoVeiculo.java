package com.vocealuga.model;

import jakarta.persistence.*;

@Entity
@Table(name = "grupoveiculo")
public class GrupoVeiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdGrupoVeiculo")
    private Integer idGrupoVeiculo;

    @Column(name = "Grupo", nullable = false, length = 50)
    private String grupo;

    public GrupoVeiculo() {
    }

    public GrupoVeiculo(String grupo) {
        this.grupo = grupo;
    }

    // Getters and Setters
    public Integer getIdGrupoVeiculo() { return idGrupoVeiculo; }
    public void setIdGrupoVeiculo(Integer idGrupoVeiculo) { this.idGrupoVeiculo = idGrupoVeiculo; }
    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    @Override
    public String toString() {
        return "GrupoVeiculo{" +
               "idGrupoVeiculo=" + idGrupoVeiculo +
               ", grupo='" + grupo + '\'' +
               '}';
    }
}