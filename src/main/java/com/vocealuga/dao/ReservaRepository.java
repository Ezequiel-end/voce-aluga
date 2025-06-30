package com.vocealuga.dao;

import com.vocealuga.model.Reserva;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
    List<Reserva> findByCliente_IdCliente(Integer idCliente);

    @Transactional
    @Modifying
    @Query("DELETE FROM Reserva r WHERE r.veiculo.idVeiculo = :veiculoId")
    void deleteByVeiculoId(@Param("veiculoId") Integer veiculoId);
}