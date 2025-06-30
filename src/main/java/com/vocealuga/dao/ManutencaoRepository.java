package com.vocealuga.dao;

import com.vocealuga.model.Manutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Manutencao m WHERE m.veiculo.idVeiculo = :veiculoId")
    void deleteByVeiculoId(@Param("veiculoId") Integer veiculoId);
}
