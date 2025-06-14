package com.vocealuga.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdPagamento")
    private Integer idPagamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdFormaPagamento", nullable = false)
    private FormaPagamento formaPagamento; // FK to formapagamento

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdReserva", nullable = false)
    private Reserva reserva; // FK to reserva

    @Column(name = "DataPagamento", nullable = false)
    private LocalDate dataPagamento;

    public Pagamento() {
    }

    public Pagamento(FormaPagamento formaPagamento, Reserva reserva, LocalDate dataPagamento) {
        this.formaPagamento = formaPagamento;
        this.reserva = reserva;
        this.dataPagamento = dataPagamento;
    }

    // Getters and Setters
    public Integer getIdPagamento() { return idPagamento; }
    public void setIdPagamento(Integer idPagamento) { this.idPagamento = idPagamento; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }
    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }

    @Override
    public String toString() {
        return "Pagamento{" +
               "idPagamento=" + idPagamento +
               ", formaPagamento=" + (formaPagamento != null ? formaPagamento.getIdFormaPagamento() : "null") +
               ", reserva=" + (reserva != null ? reserva.getIdReserva() : "null") +
               ", dataPagamento=" + dataPagamento +
               '}';
    }
}