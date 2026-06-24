package com.vocealuga.service;

import com.vocealuga.model.Veiculo;
import com.vocealuga.model.GrupoVeiculo;
import com.vocealuga.dao.VeiculoRepository;
import com.vocealuga.dao.ReservaRepository;
import com.vocealuga.dao.ManutencaoRepository;
import com.vocealuga.dao.EstoqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private ManutencaoRepository manutencaoRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private VeiculoService veiculoService;

    private Veiculo veiculo;
    private GrupoVeiculo grupoVeiculo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        grupoVeiculo = new GrupoVeiculo();
        grupoVeiculo.setIdGrupoVeiculo(1);
        grupoVeiculo.setGrupo("SUV");
        veiculo = new Veiculo();
        veiculo.setIdVeiculo(1);
        veiculo.setGrupoVeiculo(grupoVeiculo);
        veiculo.setPlaca("ABC1234");
        veiculo.setModelo("Toyota Corolla");
        veiculo.setStatus("Disponível");
        veiculo.setQuilometragem(50000.0f);
    }

    @Test
    void obterTodosVeiculosDeveRetornarListaQuandoEncontrados() {
        List<Veiculo> veiculos = Arrays.asList(veiculo, new Veiculo());
        when(veiculoRepository.findAll()).thenReturn(veiculos);

        List<Veiculo> resultado = veiculoService.getAllVeiculos();
        
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(veiculoRepository, times(1)).findAll();
    }

    @Test
    void obterVeiculoPorIdDeveRetornarVeiculoQuandoEncontrado() {
        when(veiculoRepository.findById(1)).thenReturn(Optional.of(veiculo));

        Optional<Veiculo> resultado = veiculoService.getVeiculoById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Toyota Corolla", resultado.get().getModelo());
        verify(veiculoRepository, times(1)).findById(1);
    }

    @Test
    void obterVeiculoPorIdDeveRetornarVazioQuandoNaoEncontrado() {
        when(veiculoRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Veiculo> resultado = veiculoService.getVeiculoById(1);

        assertFalse(resultado.isPresent());
        verify(veiculoRepository, times(1)).findById(1);
    }

    @Test
    void criarVeiculoDeveRetornarVeiculoQuandoDadosValidos() {
        when(veiculoRepository.save(veiculo)).thenReturn(veiculo);

        Veiculo resultado = veiculoService.createVeiculo(veiculo);

        assertNotNull(resultado);
        assertEquals("Toyota Corolla", resultado.getModelo());
        verify(veiculoRepository, times(1)).save(veiculo);
    }

    @Test
    void atualizarVeiculoDeveRetornarVeiculoAtualizadoQuandoEncontrado() {
        GrupoVeiculo novoGrupoVeiculo = new GrupoVeiculo();
        novoGrupoVeiculo.setIdGrupoVeiculo(2);
        novoGrupoVeiculo.setGrupo("Sedan");
        Veiculo novoVeiculo = new Veiculo();
        novoVeiculo.setGrupoVeiculo(novoGrupoVeiculo);
        novoVeiculo.setPlaca("XYZ5678");
        novoVeiculo.setModelo("Honda Civic");
        novoVeiculo.setStatus("Em Manutenção");
        novoVeiculo.setQuilometragem(60000.0f);
        when(veiculoRepository.findById(1)).thenReturn(Optional.of(veiculo));
        when(veiculoRepository.save(veiculo)).thenReturn(veiculo);

        Veiculo resultado = veiculoService.updateVeiculo(1, novoVeiculo);

        assertNotNull(resultado);
        assertEquals("Honda Civic", resultado.getModelo());
        verify(veiculoRepository, times(1)).findById(1);
        verify(veiculoRepository, times(1)).save(veiculo);
    }

    @Test
    void atualizarVeiculoDeveLancarExcecaoQuandoNaoEncontrado() {
        when(veiculoRepository.findById(1)).thenReturn(Optional.empty());

        Veiculo novoVeiculo = new Veiculo();

        assertThrows(RuntimeException.class, () -> veiculoService.updateVeiculo(1, novoVeiculo),
                "Veiculo not found with id 1");
        verify(veiculoRepository, times(1)).findById(1);
        verify(veiculoRepository, never()).save(any());
    }

    @Test
    void excluirVeiculoDeveExecutarSucessoQuandoEncontrado() {
        when(veiculoRepository.findById(1)).thenReturn(Optional.of(veiculo));
        when(reservaRepository.findByVeiculoIdAndStatusNot(1, "Cancelada")).thenReturn(List.of());
        when(manutencaoRepository.findByVeiculoIdAndStatusNot(1, "Cancelada")).thenReturn(List.of());
        when(reservaRepository.findByVeiculoId(1)).thenReturn(List.of());
        when(manutencaoRepository.findByVeiculoId(1)).thenReturn(List.of());
        when(estoqueRepository.findByVeiculoId(1)).thenReturn(Optional.empty());
        when(veiculoRepository.save(veiculo)).thenReturn(veiculo);

        veiculoService.deleteVeiculo(1);

        verify(veiculoRepository, times(1)).findById(1);
        verify(veiculoRepository, times(1)).save(veiculo);
    }
}