package com.vocealuga.dao;

import com.vocealuga.model.Veiculo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Integer> {
    Optional<Veiculo> findByPlaca(String placa);

    @Query("SELECT v FROM Veiculo v WHERE v.ativo = true")
    List<Veiculo> findByAtivoTrue();

    @Query("SELECT v FROM Veiculo v WHERE v.ativo = true AND v.status = 'Disponível'")
    List<Veiculo> findAtivosDisponiveis();

    @Query("SELECT v FROM Veiculo v JOIN Estoque e ON e.veiculo = v WHERE e.situacao = :situacao")
    List<Veiculo> findVeiculosByEstoqueSituacao(@Param("situacao") String situacao);
}