package com.vocealuga.service;

import com.vocealuga.model.Estoque;
import com.vocealuga.model.Filial; // Import adicionado para Filial
import com.vocealuga.model.Veiculo; // Import adicionado para Veiculo
import com.vocealuga.dao.EstoqueRepository;
import com.vocealuga.dao.FilialRepository; // Injetar FilialRepository
import com.vocealuga.dao.VeiculoRepository; // Injetar VeiculoRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final FilialRepository filialRepository; // Injetar FilialRepository
    private final VeiculoRepository veiculoRepository; // Injetar VeiculoRepository

    @Autowired
    public EstoqueService(EstoqueRepository estoqueRepository, FilialRepository filialRepository, VeiculoRepository veiculoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.filialRepository = filialRepository;
        this.veiculoRepository = veiculoRepository;
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

    
    public Estoque transferirVeiculoParaFilial(Integer veiculoId, Integer filialOrigemId, Integer filialDestinoId) {

        if (filialOrigemId.equals(filialDestinoId)) {
        throw new RuntimeException("A filial de origem e destino não podem ser a mesma.");
    	}

        // 1. Valida se o veículo existe
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com id " + veiculoId));

        // 2. Valida se a filial de destino existe
        Filial filialDestino = filialRepository.findById(filialDestinoId)
                .orElseThrow(() -> new RuntimeException("Filial de destino não encontrada com id " + filialDestinoId));

        // 3. Encontra o registro de estoque do veículo na filial de origem
        // Assumindo que um veículo só pode estar em uma filial por vez no estoque
        Estoque estoqueAtual = estoqueRepository.findByVeiculoIdAndFilialId(veiculoId, filialOrigemId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado na filial de origem com id " + filialOrigemId));

        // 4. Atualiza a filial do estoque para a filial de destino
        estoqueAtual.setFilial(filialDestino);

        // 5. Salva a atualização
        return estoqueRepository.save(estoqueAtual);
    }
}
