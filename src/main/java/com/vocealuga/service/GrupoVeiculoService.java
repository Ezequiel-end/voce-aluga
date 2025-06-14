package com.vocealuga.service;

import com.vocealuga.model.GrupoVeiculo;
import com.vocealuga.dao.GrupoVeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoVeiculoService {

    private final GrupoVeiculoRepository grupoVeiculoRepository;

    @Autowired
    public GrupoVeiculoService(GrupoVeiculoRepository grupoVeiculoRepository) {
        this.grupoVeiculoRepository = grupoVeiculoRepository;
    }

    public List<GrupoVeiculo> getAllGruposVeiculo() {
        return grupoVeiculoRepository.findAll();
    }

    public Optional<GrupoVeiculo> getGrupoVeiculoById(Integer id) {
        return grupoVeiculoRepository.findById(id);
    }

    public GrupoVeiculo createGrupoVeiculo(GrupoVeiculo grupoVeiculo) {
        return grupoVeiculoRepository.save(grupoVeiculo);
    }

    public GrupoVeiculo updateGrupoVeiculo(Integer id, GrupoVeiculo grupoVeiculoDetails) {
        return grupoVeiculoRepository.findById(id)
                .map(grupoVeiculo -> {
                    grupoVeiculo.setGrupo(grupoVeiculoDetails.getGrupo());
                    return grupoVeiculoRepository.save(grupoVeiculo);
                }).orElseThrow(() -> new RuntimeException("GrupoVeiculo not found with id " + id));
    }

    public void deleteGrupoVeiculo(Integer id) {
        grupoVeiculoRepository.deleteById(id);
    }
}