package com.vocealuga.dao;

import com.vocealuga.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

    @Query("SELECT e FROM Estoque e WHERE e.veiculo.idVeiculo = :veiculoId AND e.filial.idFilial = :filialId")
    Optional<Estoque> findByVeiculoAndFilial(@Param("veiculoId") Integer veiculoId,
                                              @Param("filialId") Integer filialId);
    Optional<Estoque> findByVeiculo_IdVeiculo(Integer idVeiculo);
}                                                                                              

