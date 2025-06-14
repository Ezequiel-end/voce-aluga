package com.vocealuga.model;

import jakarta.persistence.*;

@Entitys
@Table(name = "veiculo")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdVeiculo")
    private Integer idVeiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdGrupoVeiculo", nullable = false)
    private GrupoVeiculo grupoVeiculo; // FK to grupoveiculo

    @Column(name = "Placa", nullable = false, unique = true, length = 50)
    private String placa;

    @Column(name = "Modelo", nullable = false, length = 50)
    private String modelo;

    @Column(name = "Status", nullable = false, length = 50)
    private String status;

    @Column(name = "Quilometragem", nullable = false)
    private Float quilometragem;

    public Veiculo() {
    }

    public Veiculo(GrupoVeiculo grupoVeiculo, String placa, String modelo, String status, Float quilometragem) {
        this.grupoVeiculo = grupoVeiculo;
        this.placa = placa;
        this.modelo = modelo;
        this.status = status;
        this.quilometragem = quilometragem;
    }

    // Getters and Setters
    public Integer getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(Integer idVeiculo) { this.idVeiculo = idVeiculo; }
    public GrupoVeiculo getGrupoVeiculo() { return grupoVeiculo; }
    public void setGrupoVeiculo(GrupoVeiculo grupoVeiculo) { this.grupoVeiculo = grupoVeiculo; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Float getQuilometragem() { return quilometragem; }
    public void setQuilometragem(Float quilometragem) { this.quilometragem = quilometragem; }

    @Override
    public String toString() {
        return "Veiculo{" +
               "idVeiculo=" + idVeiculo +
               ", grupoVeiculo=" + (grupoVeiculo != null ? grupoVeiculo.getIdGrupoVeiculo() : "null") +
               ", placa='" + placa + '\'' +
               ", modelo='" + modelo + '\'' +
               ", status='" + status + '\'' +
               ", quilometragem=" + quilometragem +
               '}';
    }
}