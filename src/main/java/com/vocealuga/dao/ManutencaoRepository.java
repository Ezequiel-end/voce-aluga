package com.vocealuga.dao;

import com.vocealuga.model.Manutencao;
import com.vocealuga.model.Reserva;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, Integer> {

    @Query("SELECT m FROM Manutencao m WHERE m.veiculo.idVeiculo = :veiculoId")
    List<Manutencao> findByVeiculoId(@Param("veiculoId") Integer veiculoId);

    @Modifying
    @Query("DELETE FROM Manutencao m WHERE m.veiculo.idVeiculo = :veiculoId")
    void deleteByVeiculoId(@Param("veiculoId") Integer veiculoId);

    @Query("SELECT m FROM Manutencao m WHERE m.veiculo.idVeiculo = :veiculoId AND m.status != :status")
    List<Manutencao> findByVeiculoIdAndStatusNot(@Param("veiculoId") Integer veiculoId, @Param("status") String status);
}
