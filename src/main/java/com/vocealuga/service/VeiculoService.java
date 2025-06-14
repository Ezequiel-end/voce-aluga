package com.vocealuga.service;

import com.vocealuga.model.Veiculo;
import com.vocealuga.dao.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    @Autowired
    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public List<Veiculo> getAllVeiculos() {
        return veiculoRepository.findAll();
    }

    public Optional<Veiculo> getVeiculoById(Integer id) {
        return veiculoRepository.findById(id);
    }

    public Veiculo createVeiculo(Veiculo veiculo) {
        return veiculoRepository.save(veiculo);
    }

    public Veiculo updateVeiculo(Integer id, Veiculo veiculoDetails) {
        return veiculoRepository.findById(id)
                .map(veiculo -> {
                    veiculo.setGrupoVeiculo(veiculoDetails.getGrupoVeiculo());
                    veiculo.setPlaca(veiculoDetails.getPlaca());
                    veiculo.setModelo(veiculoDetails.getModelo());
                    veiculo.setStatus(veiculoDetails.getStatus());
                    veiculo.setQuilometragem(veiculoDetails.getQuilometragem());
                    return veiculoRepository.save(veiculo);
                }).orElseThrow(() -> new RuntimeException("Veiculo not found with id " + id));
    }

    public void deleteVeiculo(Integer id) {
        veiculoRepository.deleteById(id);
    }
}