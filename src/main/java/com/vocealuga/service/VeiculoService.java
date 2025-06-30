package com.vocealuga.service;

import com.vocealuga.dao.EstoqueRepository;
import com.vocealuga.dao.ManutencaoRepository;
import com.vocealuga.dao.ReservaRepository;
import com.vocealuga.dao.VeiculoRepository;
import com.vocealuga.model.Veiculo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ManutencaoRepository manutencaoRepository;
    private final EstoqueRepository estoqueRepository;
    private final ReservaRepository reservaRepository;

    @Autowired
    public VeiculoService(VeiculoRepository veiculoRepository,
                          ManutencaoRepository manutencaoRepository,
                          EstoqueRepository estoqueRepository,
                          ReservaRepository reservaRepository) {
        this.veiculoRepository = veiculoRepository;
        this.manutencaoRepository = manutencaoRepository;
        this.estoqueRepository = estoqueRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Veiculo> getAllVeiculos() {
        return veiculoRepository.findAll();
    }

    public Optional<Veiculo> getVeiculoById(Integer id) {
        return veiculoRepository.findById(id);
    }

    public Veiculo createVeiculo(Veiculo veiculo) {
        Optional<Veiculo> existente = veiculoRepository.findByPlaca(veiculo.getPlaca());
        if (existente.isPresent()) {
            throw new RuntimeException("Já existe um veículo cadastrado com a placa: " + veiculo.getPlaca());
        }
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

    @Transactional
    public void deleteVeiculo(Integer veiculoId) {
        // 1. Remover manutenções associadas
        manutencaoRepository.deleteByVeiculoId(veiculoId);

        // 2. Remover estoque associado
        estoqueRepository.deleteByVeiculoId(veiculoId);

        // 3. Por fim, remover o veículo
        veiculoRepository.deleteById(veiculoId);

        // 4. Remover reservas associadas
        reservaRepository.deleteByVeiculoId(veiculoId);
    }
}
