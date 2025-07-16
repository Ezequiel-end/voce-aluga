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

    /**
     * Valida a data de início da manutenção.
     * 
     * @param dataInicio Data de início da manutenção
     * @throws RuntimeException se a data de início for anterior ao momento atual
     */
    public void validateDataInicio(LocalDateTime dataInicio) {
        if (dataInicio != null && dataInicio.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("A data de início não pode ser anterior ao momento atual.");
        }
    }

    /**
     * Valida a data de fim da manutenção.
     * 
     * @param dataInicio Data de início da manutenção
     * @param dataFim    Data de fim da manutenção
     * @throws RuntimeException se a data de fim for anterior a data de início + 1
     *                          dia
     */
    public void validateDataFim(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataFim != null && dataInicio != null && dataFim.isBefore(dataInicio.plusDays(1))) {
            throw new RuntimeException("A data de fim deve ser pelo menos 1 dia após a data de início.");
        }
    }
}