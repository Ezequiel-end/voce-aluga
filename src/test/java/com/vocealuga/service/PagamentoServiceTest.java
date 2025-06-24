package com.vocealuga.service;

import com.vocealuga.model.Pagamento;
import com.vocealuga.model.FormaPagamento;
import com.vocealuga.model.Reserva;
import com.vocealuga.dao.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Pagamento pagamento;
    private FormaPagamento formaPagamento;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        formaPagamento = new FormaPagamento();
        formaPagamento.setIdFormaPagamento(1);
        formaPagamento.setFormaPagamento("Cartão");
        reserva = new Reserva();
        reserva.setIdReserva(1);
        pagamento = new Pagamento();
        pagamento.setIdPagamento(1);
        pagamento.setFormaPagamento(formaPagamento);
        pagamento.setReserva(reserva);
        pagamento.setDataPagamento(LocalDate.now());
    }

    @Test
    void testGetAllPagamentos_Sucesso() {
        List<Pagamento> pagamentos = Arrays.asList(pagamento, new Pagamento());
        when(pagamentoRepository.findAll()).thenReturn(pagamentos);
        List<Pagamento> resultado = pagamentoService.getAllPagamentos();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(pagamentoRepository, times(1)).findAll();
    }

    @Test
    void testGetPagamentoById_Sucesso() {
        when(pagamentoRepository.findById(1)).thenReturn(Optional.of(pagamento));
        Optional<Pagamento> resultado = pagamentoService.getPagamentoById(1);
        assertTrue(resultado.isPresent());
        assertEquals("Cartão", resultado.get().getFormaPagamento().getFormaPagamento());
        verify(pagamentoRepository, times(1)).findById(1);
    }

    @Test
    void testGetPagamentoById_NaoEncontrado() {
        when(pagamentoRepository.findById(1)).thenReturn(Optional.empty());
        Optional<Pagamento> resultado = pagamentoService.getPagamentoById(1);
        assertFalse(resultado.isPresent());
        verify(pagamentoRepository, times(1)).findById(1);
    }

    @Test
    void testCreatePagamento_Sucesso() {
        when(pagamentoRepository.save(pagamento)).thenReturn(pagamento);
        Pagamento resultado = pagamentoService.createPagamento(pagamento);
        assertNotNull(resultado);
        assertEquals("Cartão", resultado.getFormaPagamento().getFormaPagamento());
        verify(pagamentoRepository, times(1)).save(pagamento);
    }

    @Test
    void testUpdatePagamento_Sucesso() {
        FormaPagamento novaForma = new FormaPagamento();
        novaForma.setIdFormaPagamento(2);
        novaForma.setFormaPagamento("Pix");
        Reserva novaReserva = new Reserva();
        novaReserva.setIdReserva(2);
        Pagamento novoPagamento = new Pagamento();
        novoPagamento.setFormaPagamento(novaForma);
        novoPagamento.setReserva(novaReserva);
        novoPagamento.setDataPagamento(LocalDate.now().plusDays(1));
        when(pagamentoRepository.findById(1)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(pagamento)).thenReturn(pagamento);

        Pagamento resultado = pagamentoService.updatePagamento(1, novoPagamento);
        assertNotNull(resultado);
        assertEquals("Pix", resultado.getFormaPagamento().getFormaPagamento());
        verify(pagamentoRepository, times(1)).findById(1);
        verify(pagamentoRepository, times(1)).save(pagamento);
    }

    @Test
    void testUpdatePagamento_NaoEncontrado() {
        when(pagamentoRepository.findById(1)).thenReturn(Optional.empty());
        Pagamento novoPagamento = new Pagamento();
        assertThrows(RuntimeException.class, () -> pagamentoService.updatePagamento(1, novoPagamento),
                "Pagamento not found with id 1");
        verify(pagamentoRepository, times(1)).findById(1);
        verify(pagamentoRepository, never()).save(any());
    }

    @Test
    void testDeletePagamento_Sucesso() {
        doNothing().when(pagamentoRepository).deleteById(1);
        pagamentoService.deletePagamento(1);
        verify(pagamentoRepository, times(1)).deleteById(1);
    }
}