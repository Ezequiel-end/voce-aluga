package com.vocealuga.service;

import com.vocealuga.model.Reserva;
import com.vocealuga.model.Funcionario;
import com.vocealuga.model.Filial;
import com.vocealuga.model.Cliente;
import com.vocealuga.model.Veiculo;
import com.vocealuga.dao.ReservaRepository;
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
import static org.mockito.Mockito.*;

class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reserva;
    private Funcionario funcionario;
    private Filial filial;
    private Cliente cliente;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        funcionario = new Funcionario();
        funcionario.setIdFuncionario(1);
        filial = new Filial();
        filial.setIdFilial(1);
        cliente = new Cliente();
        cliente.setIdCliente(1);
        veiculo = new Veiculo();
        veiculo.setIdVeiculo(1);
        reserva = new Reserva();
        reserva.setIdReserva(1);
        reserva.setFuncionario(funcionario);
        reserva.setFilial(filial);
        reserva.setCliente(cliente);
        reserva.setVeiculo(veiculo);
        reserva.setDataInicio(LocalDateTime.now().plusDays(1));
        reserva.setDataFim(LocalDateTime.now().plusDays(2));
        reserva.setValor(100.0f);
        reserva.setStatus("Pendente");
    }

    @Test
    void obterTodasReservasDeveRetornarListaQuandoExistirem() {
        List<Reserva> reservas = Arrays.asList(reserva, new Reserva());
        when(reservaRepository.findAll()).thenReturn(reservas);

        List<Reserva> resultado = reservaService.getAllReservas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(reservaRepository, times(1)).findAll();
    }

    @Test
    void obterReservaPorIdDeveRetornarReservaQuandoEncontrada() {
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));

        Optional<Reserva> resultado = reservaService.getReservaById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Pendente", resultado.get().getStatus());
        verify(reservaRepository, times(1)).findById(1);
    }

    @Test
    void obterReservaPorIdDeveRetornarVazioQuandoNaoEncontrada() {
        when(reservaRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Reserva> resultado = reservaService.getReservaById(1);

        assertFalse(resultado.isPresent());
        verify(reservaRepository, times(1)).findById(1);
    }

    @Test
    void criarReservaDeveRetornarReservaQuandoDadosValidos() {
        when(reservaRepository.save(reserva)).thenReturn(reserva);

        Reserva resultado = reservaService.createReserva(reserva);

        assertNotNull(resultado);
        assertEquals("Pendente", resultado.getStatus());
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void atualizarReservaDeveRetornarReservaAtualizadaQuandoEncontrada() {
        Reserva novaReserva = new Reserva();
        Funcionario novoFuncionario = new Funcionario();
        novoFuncionario.setIdFuncionario(2);
        Filial novaFilial = new Filial();
        novaFilial.setIdFilial(2);
        Cliente novoCliente = new Cliente();
        novoCliente.setIdCliente(2);
        Veiculo novoVeiculo = new Veiculo();
        novoVeiculo.setIdVeiculo(2);
        novaReserva.setFuncionario(novoFuncionario);
        novaReserva.setFilial(novaFilial);
        novaReserva.setCliente(novoCliente);
        novaReserva.setVeiculo(novoVeiculo);
        novaReserva.setDataInicio(LocalDateTime.now().plusDays(2));
        novaReserva.setDataFim(LocalDateTime.now().plusDays(3));
        novaReserva.setValor(150.0f);
        novaReserva.setStatus("Confirmada");
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(reserva)).thenReturn(reserva);

        Reserva resultado = reservaService.updateReserva(1, novaReserva);

        assertNotNull(resultado);
        assertEquals("Confirmada", resultado.getStatus());
        verify(reservaRepository, times(1)).findById(1);
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    void atualizarReservaDeveLancarExcecaoQuandoNaoEncontrada() {
        when(reservaRepository.findById(1)).thenReturn(Optional.empty());
        
        Reserva novaReserva = new Reserva();

        assertThrows(RuntimeException.class, () -> reservaService.updateReserva(1, novaReserva),
                "Reserva not found with id 1");
        verify(reservaRepository, times(1)).findById(1);
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void excluirReservaDeveExecutarSucessoQuandoEncontrada() {
        doNothing().when(reservaRepository).deleteById(1);

        reservaService.deleteReserva(1);

        verify(reservaRepository, times(1)).deleteById(1);
    }
}