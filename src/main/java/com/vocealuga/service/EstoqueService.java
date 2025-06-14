package com.vocealuga.service;

import com.vocealuga.model.Estoque;
import com.vocealuga.dao.EstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    @Autowired
    public EstoqueService(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    public List<Estoque> getAllEstoques() {
        return estoqueRepository.findAll();
    }

    public Optional<Estoque> getEstoqueById(Integer id) {
        return estoqueRepository.findById(id);
    }

    public Estoque createEstoque(Estoque estoque) {
        return estoqueRepository.save(estoque);
    }

    public Estoque updateEstoque(Integer id, Estoque estoqueDetails) {
        return estoqueRepository.findById(id)
                .map(estoque -> {
                    estoque.setFilial(estoqueDetails.getFilial());
                    estoque.setVeiculo(estoqueDetails.getVeiculo());
                    estoque.setFuncionario(estoqueDetails.getFuncionario());
                    estoque.setSituacao(estoqueDetails.getSituacao());
                    return estoqueRepository.save(estoque);
                }).orElseThrow(() -> new RuntimeException("Estoque not found with id " + id));
    }

    public void deleteEstoque(Integer id) {
        estoqueRepository.deleteById(id);
    }
}