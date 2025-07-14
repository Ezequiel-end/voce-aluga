package com.vocealuga.service;

import com.vocealuga.dao.EstoqueRepository;
import com.vocealuga.dao.ManutencaoRepository;
import com.vocealuga.dao.ReservaRepository;
import com.vocealuga.dao.VeiculoRepository;
import com.vocealuga.model.Estoque;
import com.vocealuga.model.Manutencao;
import com.vocealuga.model.Reserva;
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
        // 1. Buscar o veículo
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        // 2. Validar se há reservas ativas
        List<Reserva> reservasAtivas = reservaRepository.findByVeiculoIdAndStatusNot(veiculoId, "Cancelada");
        if (!reservasAtivas.isEmpty()) {
            throw new RuntimeException("Não pode remover, veículo reservado");
        }

        // 3. Marcar o veículo como inativo (soft delete)
        veiculo.setAtivo(false);
        veiculoRepository.save(veiculo);

        // 4. Atualizar reservas associadas (marcar como canceladas)
        List<Reserva> reservas = reservaRepository.findByVeiculoId(veiculoId);
        for (Reserva reserva : reservas) {
            reserva.setStatus("Cancelada");
            //reserva.setVeiculo(null); // Desassociar o veículo
            reservaRepository.save(reserva);
        }

        // 5. Atualizar manutenções (desassociar o veículo)
        List<Manutencao> manutencoes = manutencaoRepository.findByVeiculoId(veiculoId);
        for (Manutencao manutencao : manutencoes) {
           // manutencao.setVeiculo(null);  Desassociar o veículo
            manutencaoRepository.save(manutencao);
        }

        // 6. Remover estoque associado (deletar o registro)
        Optional<Estoque> estoque = estoqueRepository.findByVeiculoId(veiculoId);
        if (estoque.isPresent()) {
            estoqueRepository.delete(estoque.get()); // Deleta o registro de estoque
        }
    }

    // Método para listar apenas veículos ativos
    public List<Veiculo> getAllVeiculosAtivos() {
        return veiculoRepository.findByAtivoTrue();
    }

    // Novo método para listar veículos ativos e disponíveis
    public List<Veiculo> getVeiculosAtivosDisponiveis() {
        return veiculoRepository.findAtivosDisponiveis();
    }
}
