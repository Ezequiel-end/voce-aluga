package com.vocealuga.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime; // For DataFim which is DATETIME

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdReserva")
    private Integer idReserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdFuncionario", nullable = false)
    private Funcionario funcionario; // FK to funcionario

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdFilial", nullable = false)
    private Filial filial; // FK to filial

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdCliente", nullable = false)
    private Cliente cliente; // FK to cliente

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdVeiculo", nullable = false)
    private Veiculo veiculo; // FK to veiculo

    @Column(name = "DataInicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "DataFim", nullable = false)
    private LocalDateTime dataFim;

    @Column(name = "Valor", nullable = false)
    private Float valor;

    @Column(name = "Status", nullable = false, length = 50)
    private String status;

    public Reserva() {
    }

    public Reserva(Funcionario funcionario, Filial filial, Cliente cliente, Veiculo veiculo, LocalDateTime dataInicio, LocalDateTime dataFim, Float valor, String status) {
        this.funcionario = funcionario;
        this.filial = filial;
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.valor = valor;
        this.status = status;
    }

    // Getters and Setters
    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }
    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }
    public Filial getFilial() { return filial; }
    public void setFilial(Filial filial) { this.filial = filial; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public Float getValor() { return valor; }
    public void setValor(Float valor) { this.valor = valor; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Reserva{" +
               "idReserva=" + idReserva +
               ", funcionario=" + (funcionario != null ? funcionario.getIdFuncionario() : "null") +
               ", filial=" + (filial != null ? filial.getIdFilial() : "null") +
               ", cliente=" + (cliente != null ? cliente.getIdCliente() : "null") +
               ", veiculo=" + (veiculo != null ? veiculo.getIdVeiculo() : "null") +
               ", dataInicio=" + dataInicio +
               ", dataFim=" + dataFim +
               ", valor=" + valor +
               ", status='" + status + '\'' +
               '}';
    }
}