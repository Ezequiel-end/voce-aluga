package com.vocealuga.dao;

import com.vocealuga.vocealuga.model.GrupoVeiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoVeiculoRepository extends JpaRepository<GrupoVeiculo, Integer> {
}