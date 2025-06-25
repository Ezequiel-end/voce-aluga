package com.vocealuga.service;

import com.vocealuga.model.GrupoVeiculo;
import com.vocealuga.dao.GrupoVeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GrupoVeiculoServiceTest {

    @Mock
    private GrupoVeiculoRepository grupoVeiculoRepository;

    @InjectMocks
    private GrupoVeiculoService grupoVeiculoService;

    private GrupoVeiculo grupoVeiculo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        grupoVeiculo = new GrupoVeiculo();
        grupoVeiculo.setIdGrupoVeiculo(1);
        grupoVeiculo.setGrupo("SUV");
    }

    @Test
    void obterTodosGruposVeiculoDeveRetornarListaQuandoExistirem() {
        List<GrupoVeiculo> grupos = Arrays.asList(grupoVeiculo, new GrupoVeiculo());
        when(grupoVeiculoRepository.findAll()).thenReturn(grupos);

        List<GrupoVeiculo> resultado = grupoVeiculoService.getAllGruposVeiculo();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(grupoVeiculoRepository, times(1)).findAll();
    }

    @Test
    void obterGrupoVeiculoPorIdDeveRetornarGrupoVeiculoQuandoEncontrado() {
        when(grupoVeiculoRepository.findById(1)).thenReturn(Optional.of(grupoVeiculo));

        Optional<GrupoVeiculo> resultado = grupoVeiculoService.getGrupoVeiculoById(1);

        assertTrue(resultado.isPresent());
        assertEquals("SUV", resultado.get().getGrupo());
        verify(grupoVeiculoRepository, times(1)).findById(1);
    }

    @Test
    void obterGrupoVeiculoPorIdDeveRetornarVazioQuandoNaoEncontrado() {
        when(grupoVeiculoRepository.findById(1)).thenReturn(Optional.empty());

        Optional<GrupoVeiculo> resultado = grupoVeiculoService.getGrupoVeiculoById(1);

        assertFalse(resultado.isPresent());
        verify(grupoVeiculoRepository, times(1)).findById(1);
    }

    @Test
    void criarGrupoVeiculoDeveRetornarGrupoVeiculoQuandoDadosValidos() {
        when(grupoVeiculoRepository.save(grupoVeiculo)).thenReturn(grupoVeiculo);

        GrupoVeiculo resultado = grupoVeiculoService.createGrupoVeiculo(grupoVeiculo);

        assertNotNull(resultado);
        assertEquals("SUV", resultado.getGrupo());
        verify(grupoVeiculoRepository, times(1)).save(grupoVeiculo);
    }

    @Test
    void atualizarGrupoVeiculoDeveRetornarGrupoVeiculoAtualizadoQuandoEncontrado() {
        GrupoVeiculo novoGrupo = new GrupoVeiculo();
        novoGrupo.setGrupo("Sedan");
        when(grupoVeiculoRepository.findById(1)).thenReturn(Optional.of(grupoVeiculo));
        when(grupoVeiculoRepository.save(grupoVeiculo)).thenReturn(grupoVeiculo);

        GrupoVeiculo resultado = grupoVeiculoService.updateGrupoVeiculo(1, novoGrupo);

        assertNotNull(resultado);
        assertEquals("Sedan", resultado.getGrupo());
        verify(grupoVeiculoRepository, times(1)).findById(1);
        verify(grupoVeiculoRepository, times(1)).save(grupoVeiculo);
    }

    @Test
    void atualizarGrupoVeiculoDeveLancarExcecaoQuandoNaoEncontrado() {
        when(grupoVeiculoRepository.findById(1)).thenReturn(Optional.empty());

        GrupoVeiculo novoGrupo = new GrupoVeiculo();

        assertThrows(RuntimeException.class, () -> grupoVeiculoService.updateGrupoVeiculo(1, novoGrupo),
                "GrupoVeiculo not found with id 1");
        verify(grupoVeiculoRepository, times(1)).findById(1);
        verify(grupoVeiculoRepository, never()).save(any());
    }

    @Test
    void excluirGrupoVeiculoDeveExecutarSucessoQuandoEncontrado() {
        doNothing().when(grupoVeiculoRepository).deleteById(1);
        
        grupoVeiculoService.deleteGrupoVeiculo(1);
        verify(grupoVeiculoRepository, times(1)).deleteById(1);
    }
}