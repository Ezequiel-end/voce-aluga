package com.vocealuga.service;

import com.vocealuga.model.FormaPagamento;
import com.vocealuga.dao.FormaPagamentoRepository;
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

class FormaPagamentoServiceTest {

    @Mock
    private FormaPagamentoRepository formaPagamentoRepository;

    @InjectMocks
    private FormaPagamentoService formaPagamentoService;

    private FormaPagamento formaPagamento;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        formaPagamento = new FormaPagamento();
        formaPagamento.setIdFormaPagamento(1);
        formaPagamento.setFormaPagamento("Cartão de Crédito");
    }

    @Test
    void testGetAllFormasPagamento_Sucesso() {
        List<FormaPagamento> formasPagamento = Arrays.asList(formaPagamento, new FormaPagamento());
        when(formaPagamentoRepository.findAll()).thenReturn(formasPagamento);

        List<FormaPagamento> resultado = formaPagamentoService.getAllFormasPagamento();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(formaPagamentoRepository, times(1)).findAll();
    }

    @Test
    void testGetFormaPagamentoById_Sucesso() {
        when(formaPagamentoRepository.findById(1)).thenReturn(Optional.of(formaPagamento));

        Optional<FormaPagamento> resultado = formaPagamentoService.getFormaPagamentoById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Cartão de Crédito", resultado.get().getFormaPagamento());
        verify(formaPagamentoRepository, times(1)).findById(1);
    }

    @Test
    void testGetFormaPagamentoById_NaoEncontrado() {
        when(formaPagamentoRepository.findById(1)).thenReturn(Optional.empty());

        Optional<FormaPagamento> resultado = formaPagamentoService.getFormaPagamentoById(1);

        assertFalse(resultado.isPresent());
        verify(formaPagamentoRepository, times(1)).findById(1);
    }

    @Test
    void testCreateFormaPagamento_Sucesso() {
        when(formaPagamentoRepository.save(formaPagamento)).thenReturn(formaPagamento);

        FormaPagamento resultado = formaPagamentoService.createFormaPagamento(formaPagamento);

        assertNotNull(resultado);
        assertEquals("Cartão de Crédito", resultado.getFormaPagamento());
        verify(formaPagamentoRepository, times(1)).save(formaPagamento);
    }

    @Test
    void testUpdateFormaPagamento_Sucesso() {
        FormaPagamento novaForma = new FormaPagamento();
        novaForma.setFormaPagamento("Pix");
        when(formaPagamentoRepository.findById(1)).thenReturn(Optional.of(formaPagamento));
        when(formaPagamentoRepository.save(formaPagamento)).thenReturn(formaPagamento);

        FormaPagamento resultado = formaPagamentoService.updateFormaPagamento(1, novaForma);

        assertNotNull(resultado);
        assertEquals("Pix", resultado.getFormaPagamento());
        verify(formaPagamentoRepository, times(1)).findById(1);
        verify(formaPagamentoRepository, times(1)).save(formaPagamento);
    }

    @Test
    void testUpdateFormaPagamento_NaoEncontrado() {
        when(formaPagamentoRepository.findById(1)).thenReturn(Optional.empty());

        FormaPagamento novaForma = new FormaPagamento();

        assertThrows(RuntimeException.class, () -> formaPagamentoService.updateFormaPagamento(1, novaForma),
                "FormaPagamento not found with id 1");
        verify(formaPagamentoRepository, times(1)).findById(1);
        verify(formaPagamentoRepository, never()).save(any());
    }

    @Test
    void testDeleteFormaPagamento_Sucesso() {
        doNothing().when(formaPagamentoRepository).deleteById(1);
        
        formaPagamentoService.deleteFormaPagamento(1);
        verify(formaPagamentoRepository, times(1)).deleteById(1);
    }
}