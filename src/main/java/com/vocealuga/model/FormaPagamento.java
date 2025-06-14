package com.vocealuga.model;

import jakarta.persistence.*;

@Entity
@Table(name = "formapagamento")
public class FormaPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdFormaPagamento")
    private Integer idFormaPagamento;

    @Column(name = "FormaPagamento", nullable = false, length = 255)
    private String formaPagamento; // The column name in DB is 'FormaPagamento'

    public FormaPagamento() {
    }

    public FormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    // Getters and Setters
    public Integer getIdFormaPagamento() { return idFormaPagamento; }
    public void setIdFormaPagamento(Integer idFormaPagamento) { this.idFormaPagamento = idFormaPagamento; }
    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    @Override
    public String toString() {
        return "FormaPagamento{" +
               "idFormaPagamento=" + idFormaPagamento +
               ", formaPagamento='" + formaPagamento + '\'' +
               '}';
    }
}