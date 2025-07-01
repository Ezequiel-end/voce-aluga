package com.vocealuga.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime; // For DataFim which is DATETIME

@Entity
@Table(name = "manutencao")
public class Manutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdManuetencao") // Note: The SQL schema has 'IdManuetencao' with 'e'
    private Integer idManutencao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdFuncionario", nullable = false)
    private Funcionario funcionario; // FK to funcionario

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdVeiculo", nullable = false)
    private Veiculo veiculo; // FK to veiculo

    @Column(name = "DataInicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "DataFim", nullable = false)
    private LocalDateTime dataFim;

    @Column(name = "Motivo", nullable = false, length = 255)
    private String motivo;

    @Column(name = "Status", nullable = false)
    private String status;

    public Manutencao() {
    }

    public Manutencao(Funcionario funcionario, Veiculo veiculo, LocalDateTime dataInicio, LocalDateTime dataFim, String motivo, String status) {
        this.funcionario = funcionario;
        this.veiculo = veiculo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.motivo = motivo;
        this.status = status;
    }

    // Getters and Setters
    public Integer getIdManutencao() { return idManutencao; }
    public void setIdManutencao(Integer idManutencao) { this.idManutencao = idManutencao; }
    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getStatus() { return status;}
    public void setStatus(String status) {this.status = status;}

    @Override
    public String toString() {
        return "Manutencao{" +
            "idManutencao=" + idManutencao +
            ", funcionario=" + (funcionario != null ? funcionario.getIdFuncionario() : "null") +
            ", veiculo=" + (veiculo != null ? veiculo.getIdVeiculo() : "null") +
            ", dataInicio=" + dataInicio +
            ", dataFim=" + dataFim +
            ", motivo='" + motivo + '\'' +
            ", status='" + status + '\'' +
            '}';
    }
}