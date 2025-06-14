package com.vocealuga.service;

import com.vocealuga.model.FormaPagamento;
import com.vocealuga.dao.FormaPagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FormaPagamentoService {

    private final FormaPagamentoRepository formaPagamentoRepository;

    @Autowired
    public FormaPagamentoService(FormaPagamentoRepository formaPagamentoRepository) {
        this.formaPagamentoRepository = formaPagamentoRepository;
    }

    public List<FormaPagamento> getAllFormasPagamento() {
        return formaPagamentoRepository.findAll();
    }

    public Optional<FormaPagamento> getFormaPagamentoById(Integer id) {
        return formaPagamentoRepository.findById(id);
    }

    public FormaPagamento createFormaPagamento(FormaPagamento formaPagamento) {
        return formaPagamentoRepository.save(formaPagamento);
    }

    public FormaPagamento updateFormaPagamento(Integer id, FormaPagamento formaPagamentoDetails) {
        return formaPagamentoRepository.findById(id)
                .map(formaPagamento -> {
                    formaPagamento.setFormaPagamento(formaPagamentoDetails.getFormaPagamento());
                    return formaPagamentoRepository.save(formaPagamento);
                }).orElseThrow(() -> new RuntimeException("FormaPagamento not found with id " + id));
    }

    public void deleteFormaPagamento(Integer id) {
        formaPagamentoRepository.deleteById(id);
    }
}