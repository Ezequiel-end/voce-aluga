package com.vocealuga.service;

import com.vocealuga.model.Cliente;
import com.vocealuga.utils.ValidationsUtils;
import com.vocealuga.dao.ClienteRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ValidationsUtils validation;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks
        cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setCnh("1234567890");
        cliente.setEmail("joao@email.com");
        cliente.setSenha("senha123");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 1));
    }

    @Test
    void obterTodosClientesDeveRetornarListaQuandoExistirem() {
        // Arrange
        List<Cliente> clientes = Arrays.asList(cliente, new Cliente());
        when(clienteRepository.findAll()).thenReturn(clientes);

        // Act
        List<Cliente> resultado = clienteService.getAllClientes();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(cliente.getNome(), resultado.get(0).getNome());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void obterClientePorIdDeveRetornarClienteQuandoEncontrado() {
        // Arrange
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        // Act
        Optional<Cliente> resultado = clienteService.getClienteById(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(cliente.getNome(), resultado.get().getNome());
        verify(clienteRepository, times(1)).findById(1);
    }

    @Test
    void obterClientePorIdDeveRetornarVazioQuandoNaoEncontrado() {
        // Arrange
        when(clienteRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        Optional<Cliente> resultado = clienteService.getClienteById(1);

        // Assert
        assertFalse(resultado.isPresent());
        verify(clienteRepository, times(1)).findById(1);
    }

    @Test
    void criarClienteDeveRetornarClienteQuandoDadosValidos() {
        // Arrange
        try (var mockedStatic = mockStatic(ValidationsUtils.class)) {
            mockedStatic.when(() -> ValidationsUtils.isValidCPF(cliente.getCpf())).thenReturn(true);
            mockedStatic.when(() -> ValidationsUtils.isValidCNH(cliente.getCnh())).thenReturn(true);
            when(validation.isEmailGloballyUnique(cliente.getEmail())).thenReturn(true);
            when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

            // Act
            Cliente resultado = clienteService.createCliente(cliente);

            // Assert
            assertNotNull(resultado);
            assertEquals(cliente.getNome(), resultado.getNome());
            assertEquals(cliente.getCpf(), resultado.getCpf());
            verify(clienteRepository, times(1)).save(any(Cliente.class));
            verify(validation, times(1)).isEmailGloballyUnique(cliente.getEmail());
        }
    }

    @Test
    void criarClienteDeveLancarExcecaoQuandoCpfInvalido() {
        // Arrange
        try (var mockedStatic = mockStatic(ValidationsUtils.class)) {
            mockedStatic.when(() -> ValidationsUtils.isValidCPF(cliente.getCpf())).thenReturn(false);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                clienteService.createCliente(cliente);
            }, "CPF inválido!");
            verify(clienteRepository, never()).save(any(Cliente.class));
            verify(validation, never()).isEmailGloballyUnique(anyString());
        }
    }

    @Test
    void criarClienteDeveLancarExcecaoQuandoCnhInvalida() {
        // Arrange
        try (var mockedStatic = mockStatic(ValidationsUtils.class)) {
            mockedStatic.when(() -> ValidationsUtils.isValidCPF(cliente.getCpf())).thenReturn(true);
            mockedStatic.when(() -> ValidationsUtils.isValidCNH(cliente.getCnh())).thenReturn(false);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                clienteService.createCliente(cliente);
            }, "CNH inválida");
            verify(clienteRepository, never()).save(any(Cliente.class));
            verify(validation, never()).isEmailGloballyUnique(anyString());
        }
    }

    @Test
    void criarClienteDeveLancarExcecaoQuandoEmailJaCadastrado() {
        // Arrange
        try (var mockedStatic = mockStatic(ValidationsUtils.class)) {
            mockedStatic.when(() -> ValidationsUtils.isValidCPF(cliente.getCpf())).thenReturn(true);
            mockedStatic.when(() -> ValidationsUtils.isValidCNH(cliente.getCnh())).thenReturn(true);
            when(validation.isEmailGloballyUnique(cliente.getEmail())).thenReturn(false);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                clienteService.createCliente(cliente);
            }, "E-mail já cadastrado!");
            verify(clienteRepository, never()).save(any(Cliente.class));
        }
    }

    @Test
    void atualizarClienteDeveRetornarClienteAtualizadoQuandoExistir() {
        // Arrange
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setNome("Maria Oliveira");
        clienteAtualizado.setCpf("98765432100");
        clienteAtualizado.setCnh("0987654321");
        clienteAtualizado.setEmail("maria@email.com");
        clienteAtualizado.setSenha("novaSenha");
        clienteAtualizado.setDataNascimento(LocalDate.of(1985, 5, 10));

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteAtualizado);

        // Act
        Cliente resultado = clienteService.updateCliente(1, clienteAtualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals(clienteAtualizado.getNome(), resultado.getNome());
        assertEquals(clienteAtualizado.getCpf(), resultado.getCpf());
        verify(clienteRepository, times(1)).findById(1);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void atualizarClienteDeveLancarExcecaoQuandoNaoEncontrado() {
        // Arrange
        Cliente clienteAtualizado = new Cliente();
        when(clienteRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            clienteService.updateCliente(1, clienteAtualizado);
        }, "Cliente not found with id 1");
        verify(clienteRepository, times(1)).findById(1);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void excluirClienteDeveExecutarSucessoQuandoEncontrado() {
        // Arrange
        doNothing().when(clienteRepository).deleteById(1);

        // Act
        clienteService.deleteCliente(1);

        // Assert
        verify(clienteRepository, times(1)).deleteById(1);
    }

    @Test
    void fazerLoginDeveRetornarClienteQuandoCredenciaisValidas() {
        // Arrange
        String email = "joao@email.com";
        String senha = "senha123";
        when(clienteRepository.findByEmailAndSenha(email, senha)).thenReturn(Optional.of(cliente));

        // Act
        Optional<Cliente> resultado = clienteService.login(email, senha);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(cliente.getEmail(), resultado.get().getEmail());
        verify(clienteRepository, times(1)).findByEmailAndSenha(email, senha);
    }

    @Test
    void fazerLoginDeveRetornarVazioQuandoCredenciaisInvalidas() {
        // Arrange
        String email = "joao@email.com";
        String senha = "senhaErrada";
        when(clienteRepository.findByEmailAndSenha(email, senha)).thenReturn(Optional.empty());

        // Act
        Optional<Cliente> resultado = clienteService.login(email, senha);

        // Assert
        assertFalse(resultado.isPresent());
        verify(clienteRepository, times(1)).findByEmailAndSenha(email, senha);
    }
}

