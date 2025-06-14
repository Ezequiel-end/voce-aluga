package com.vocealuga.service;

import com.vocealuga.model.Pagamento;
import com.vocealuga.dao.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;

    @Autowired
    public PagamentoService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }

    public List<Pagamento> getAllPagamentos() {
        return pagamentoRepository.findAll();
    }

    public Optional<Pagamento> getPagamentoById(Integer id) {
        return pagamentoRepository.findById(id);
    }

    public Pagamento createPagamento(Pagamento pagamento) {
        return pagamentoRepository.save(pagamento);
    }

    public Pagamento updatePagamento(Integer id, Pagamento pagamentoDetails) {
        return pagamentoRepository.findById(id)
                .map(pagamento -> {
                    pagamento.setFormaPagamento(pagamentoDetails.getFormaPagamento());
                    pagamento.setReserva(pagamentoDetails.getReserva());
                    pagamento.setDataPagamento(pagamentoDetails.getDataPagamento());
                    return pagamentoRepository.save(pagamento);
                }).orElseThrow(() -> new RuntimeException("Pagamento not found with id " + id));
    }

    public void deletePagamento(Integer id) {
        pagamentoRepository.deleteById(id);
    }
}