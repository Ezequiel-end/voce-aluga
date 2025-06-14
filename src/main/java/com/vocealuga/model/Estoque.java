package com.vocealuga.model;

import jakarta.persistence.*;

@Entity
@Table(name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdEstoque")
    private Integer idEstoque;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdFilial", nullable = false)
    private Filial filial; // FK to filial

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdVeiculo", nullable = false)
    private Veiculo veiculo; // FK to veiculo

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdFuncionario", nullable = false)
    private Funcionario funcionario; // FK to funcionario

    @Column(name = "Situacao", nullable = false, length = 50)
    private String situacao;

    public Estoque() {
    }

    public Estoque(Filial filial, Veiculo veiculo, Funcionario funcionario, String situacao) {
        this.filial = filial;
        this.veiculo = veiculo;
        this.funcionario = funcionario;
        this.situacao = situacao;
    }

    // Getters and Setters
    public Integer getIdEstoque() { return idEstoque; }
    public void setIdEstoque(Integer idEstoque) { this.idEstoque = idEstoque; }
    public Filial getFilial() { return filial; }
    public void setFilial(Filial filial) { this.filial = filial; }
    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }
    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    @Override
    public String toString() {
        return "Estoque{" +
               "idEstoque=" + idEstoque +
               ", filial=" + (filial != null ? filial.getIdFilial() : "null") +
               ", veiculo=" + (veiculo != null ? veiculo.getIdVeiculo() : "null") +
               ", funcionario=" + (funcionario != null ? funcionario.getIdFuncionario() : "null") +
               ", situacao='" + situacao + '\'' +
               '}';
    }
}