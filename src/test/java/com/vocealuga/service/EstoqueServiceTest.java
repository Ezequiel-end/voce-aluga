package com.vocealuga.service;

import com.vocealuga.model.Estoque;
import com.vocealuga.model.Filial;
import com.vocealuga.model.Veiculo; // Import adicionado
import com.vocealuga.dao.EstoqueRepository;
import com.vocealuga.dao.FilialRepository;
import com.vocealuga.dao.VeiculoRepository;
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

class EstoqueServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private FilialRepository filialRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private EstoqueService estoqueService;

    private Estoque estoque;
    private Filial filial;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filial = new Filial();
        filial.setIdFilial(1);
        veiculo = new Veiculo();
        veiculo.setIdVeiculo(1);
        estoque = new Estoque();
        estoque.setIdEstoque(1);
        estoque.setFilial(filial);
        estoque.setVeiculo(veiculo);
    }

    @Test
    void testGetAllEstoques() {
        List<Estoque> estoques = Arrays.asList(estoque, new Estoque());
        when(estoqueRepository.findAll()).thenReturn(estoques);
        List<Estoque> resultado = estoqueService.getAllEstoques();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(estoqueRepository, times(1)).findAll();
    }

    @Test
    void testGetEstoqueById() {
        when(estoqueRepository.findById(1)).thenReturn(Optional.of(estoque));
        Optional<Estoque> resultado = estoqueService.getEstoqueById(1);
        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getIdEstoque());
        verify(estoqueRepository, times(1)).findById(1);
    }

    @Test
    void testGetEstoqueById_NaoEncontrado() {
        when(estoqueRepository.findById(1)).thenReturn(Optional.empty());
        Optional<Estoque> resultado = estoqueService.getEstoqueById(1);
        assertFalse(resultado.isPresent());
        verify(estoqueRepository, times(1)).findById(1);
    }

    @Test
    void testCreateEstoque() {
        when(estoqueRepository.save(estoque)).thenReturn(estoque);
        Estoque resultado = estoqueService.createEstoque(estoque);
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdEstoque());
        verify(estoqueRepository, times(1)).save(estoque);
    }

    @Test
    void testUpdateEstoque() {
        Filial novaFilial = new Filial();
        novaFilial.setIdFilial(2);
        Veiculo novoVeiculo = new Veiculo();
        novoVeiculo.setIdVeiculo(2);
        Estoque novoEstoque = new Estoque();
        novoEstoque.setFilial(novaFilial);
        novoEstoque.setVeiculo(novoVeiculo);
        when(estoqueRepository.findById(1)).thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(estoque)).thenReturn(estoque);

        Estoque resultado = estoqueService.updateEstoque(1, novoEstoque);
        assertNotNull(resultado);
        assertEquals(2, resultado.getFilial().getIdFilial());
        verify(estoqueRepository, times(1)).findById(1);
        verify(estoqueRepository, times(1)).save(estoque);
    }

    @Test
    void testUpdateEstoque_NaoEncontrado() {
        when(estoqueRepository.findById(1)).thenReturn(Optional.empty());
        Estoque novoEstoque = new Estoque();
        assertThrows(RuntimeException.class, () -> estoqueService.updateEstoque(1, novoEstoque),
                "Estoque not found with id 1");
        verify(estoqueRepository, times(1)).findById(1);
        verify(estoqueRepository, never()).save(any());
    }

    @Test
    void testDeleteEstoque() {
        doNothing().when(estoqueRepository).deleteById(1);
        estoqueService.deleteEstoque(1);
        verify(estoqueRepository, times(1)).deleteById(1);
    }

    @Test
    void testTransferirVeiculoParaFilial() {
        Filial filialDestino = new Filial();
        filialDestino.setIdFilial(2);
        Veiculo veiculoMock = new Veiculo();
        veiculoMock.setIdVeiculo(1);
        when(veiculoRepository.findById(1)).thenReturn(Optional.of(veiculoMock));
        when(filialRepository.findById(2)).thenReturn(Optional.of(filialDestino));
        when(estoqueRepository.findByVeiculoAndFilial(1, 1)).thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(estoque)).thenReturn(estoque);

        Estoque resultado = estoqueService.transferirVeiculoParaFilial(1, 1, 2);
        assertNotNull(resultado);
        assertEquals(2, resultado.getFilial().getIdFilial());
        verify(veiculoRepository, times(1)).findById(1);
        verify(filialRepository, times(1)).findById(2);
        verify(estoqueRepository, times(1)).findByVeiculoAndFilial(1, 1);
        verify(estoqueRepository, times(1)).save(estoque);
    }

    @Test
    void testTransferirVeiculoParaFilial_MesmaFilial() {
        assertThrows(RuntimeException.class, () -> estoqueService.transferirVeiculoParaFilial(1, 1, 1),
                "A filial de origem e destino não podem ser a mesma.");
    }

    @Test
    void testTransferirVeiculoParaFilial_VeiculoNaoEncontrado() {
        when(veiculoRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> estoqueService.transferirVeiculoParaFilial(1, 1, 2),
                "Veículo não encontrado com id 1");
    }

    @Test
    void testTransferirVeiculoParaFilial_FilialDestinoNaoEncontrada() {
        Veiculo veiculoMock = new Veiculo();
        veiculoMock.setIdVeiculo(1);
        when(veiculoRepository.findById(1)).thenReturn(Optional.of(veiculoMock));
        when(filialRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> estoqueService.transferirVeiculoParaFilial(1, 1, 2),
                "Filial de destino não encontrada com id 2");
    }

    @Test
    void testTransferirVeiculoParaFilial_EstoqueNaoEncontrado() {
        Veiculo veiculoMock = new Veiculo();
        veiculoMock.setIdVeiculo(1);
        when(veiculoRepository.findById(1)).thenReturn(Optional.of(veiculoMock));
        when(filialRepository.findById(2)).thenReturn(Optional.of(new Filial()));
        when(estoqueRepository.findByVeiculoAndFilial(1, 1)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> estoqueService.transferirVeiculoParaFilial(1, 1, 2),
                "Veículo não encontrado na filial de origem com id 1");
    }
}