package com.vocealuga.service;

import com.vocealuga.model.Manutencao;
import com.vocealuga.dao.ManutencaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        if (manutencao.getDataInicio().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("A data de início não pode ser anterior ao momento atual.");
        }
        if (manutencao.getDataFim().isBefore(manutencao.getDataInicio().plusDays(1))) {
            throw new RuntimeException("A data de fim deve ser pelo menos 1 dia após a data de início.");
        }

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
                    manutencao.setStatus(manutencaoDetails.getStatus());
                    return manutencaoRepository.save(manutencao);
                }).orElseThrow(() -> new RuntimeException("Manutencao not found with id " + id));
    }

    public void deleteManutencao(Integer id) {
        manutencaoRepository.deleteById(id);
    }
}