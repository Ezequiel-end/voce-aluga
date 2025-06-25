package com.vocealuga.service;

import com.vocealuga.model.Manutencao;
import com.vocealuga.model.Funcionario;
import com.vocealuga.model.Veiculo;
import com.vocealuga.dao.ManutencaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ManutencaoServiceTest {

    @Mock
    private ManutencaoRepository manutencaoRepository;

    @InjectMocks
    private ManutencaoService manutencaoService;

    private Manutencao manutencao;
    private Funcionario funcionario;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        funcionario = new Funcionario();
        funcionario.setIdFuncionario(1);
        veiculo = new Veiculo();
        veiculo.setIdVeiculo(1);
        manutencao = new Manutencao();
        manutencao.setIdManutencao(1);
        manutencao.setFuncionario(funcionario);
        manutencao.setVeiculo(veiculo);
        manutencao.setDataInicio(LocalDate.now());
        manutencao.setDataFim(LocalDateTime.now().plusDays(1));
        manutencao.setMotivo("Troca de óleo");
    }

    @Test
    void obterTodasManutencoesDeveRetornarListaQuandoExistirem() {
        List<Manutencao> manutencoes = Arrays.asList(manutencao, new Manutencao());
        when(manutencaoRepository.findAll()).thenReturn(manutencoes);

        List<Manutencao> resultado = manutencaoService.getAllManutencoes();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(manutencaoRepository, times(1)).findAll();
    }

    @Test
    void obterManutencaoPorIdDeveRetornarManutencaoQuandoEncontrado() {
        when(manutencaoRepository.findById(1)).thenReturn(Optional.of(manutencao));

        Optional<Manutencao> resultado = manutencaoService.getManutencaoById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Troca de óleo", resultado.get().getMotivo());
        verify(manutencaoRepository, times(1)).findById(1);
    }

    @Test
    void obterManutencaoPorIdDeveRetornarVazioQuandoNaoEncontrado() {
        when(manutencaoRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Manutencao> resultado = manutencaoService.getManutencaoById(1);

        assertFalse(resultado.isPresent());
        verify(manutencaoRepository, times(1)).findById(1);
    }

    @Test
    void criarManutencaoDeveRetornarManutencaoQuandoDadosValidos() {
        when(manutencaoRepository.save(manutencao)).thenReturn(manutencao);

        Manutencao resultado = manutencaoService.createManutencao(manutencao);

        assertNotNull(resultado);
        assertEquals("Troca de óleo", resultado.getMotivo());
        verify(manutencaoRepository, times(1)).save(manutencao);
    }

    @Test
    void atualizarManutencaoDeveRetornarManutencaoAtualizadaQuandoEncontrado() {
        Manutencao novaManutencao = new Manutencao();
        Funcionario novoFuncionario = new Funcionario();
        novoFuncionario.setIdFuncionario(2);
        Veiculo novoVeiculo = new Veiculo();
        novoVeiculo.setIdVeiculo(2);
        novaManutencao.setFuncionario(novoFuncionario);
        novaManutencao.setVeiculo(novoVeiculo);
        novaManutencao.setDataInicio(LocalDate.now().plusDays(2));
        novaManutencao.setDataFim(LocalDateTime.now().plusDays(3));
        novaManutencao.setMotivo("Reparo de freios");
        when(manutencaoRepository.findById(1)).thenReturn(Optional.of(manutencao));
        when(manutencaoRepository.save(manutencao)).thenReturn(manutencao);

        Manutencao resultado = manutencaoService.updateManutencao(1, novaManutencao);

        assertNotNull(resultado);
        assertEquals("Reparo de freios", resultado.getMotivo());
        verify(manutencaoRepository, times(1)).findById(1);
        verify(manutencaoRepository, times(1)).save(manutencao);
    }

    @Test
    void atualizarManutencaoDeveLancarExcecaoQuandoNaoEncontrado() {
        when(manutencaoRepository.findById(1)).thenReturn(Optional.empty());

        Manutencao novaManutencao = new Manutencao();

        assertThrows(RuntimeException.class, () -> manutencaoService.updateManutencao(1, novaManutencao),
                "Manutencao not found with id 1");
        verify(manutencaoRepository, times(1)).findById(1);
        verify(manutencaoRepository, never()).save(any());
    }

    @Test
    void excluirManutencaoDeveExecutarSucessoQuandoEncontrado() {
        doNothing().when(manutencaoRepository).deleteById(1);
        
        manutencaoService.deleteManutencao(1);
        verify(manutencaoRepository, times(1)).deleteById(1);
    }
}