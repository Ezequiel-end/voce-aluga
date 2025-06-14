package com.vocealuga.service;

import com.vocealuga.model.Manutencao;
import com.vocealuga.dao.ManutencaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManutencaoService {

    private final ManutencaoRepository manutencaoRepository;

    @Autowired
    public ManutencaoService(ManutencaoRepository manutencaoRepository) {
        this.manutencaoRepository = manutencaoRepository;
    }

    public List<Manutencao> getAllManutencoes() {
        return manutencaoRepository.findAll();
    }

    public Optional<Manutencao> getManutencaoById(Integer id) {
        return manutencaoRepository.findById(id);
    }

    public Manutencao createManutencao(Manutencao manutencao) {
        return manutencaoRepository.save(manutencao);
    }

    public Manutencao updateManutencao(Integer id, Manutencao manutencaoDetails) {
        return manutencaoRepository.findById(id)
                .map(manutencao -> {
                    manutencao.setFuncionario(manutencaoDetails.getFuncionario());
                    manutencao.setVeiculo(manutencaoDetails.getVeiculo());
                    manutencao.setDataInicio(manutencaoDetails.getDataInicio());
                    manutencao.setDataFim(manutencaoDetails.getDataFim());
                    manutencao.setMotivo(manutencaoDetails.getMotivo());
                    return manutencaoRepository.save(manutencao);
                }).orElseThrow(() -> new RuntimeException("Manutencao not found with id " + id));
    }

    public void deleteManutencao(Integer id) {
        manutencaoRepository.deleteById(id);
    }
}