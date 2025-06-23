package com.vocealuga.service;

import com.vocealuga.model.Funcionario;
import com.vocealuga.utils.ValidationsUtils;
import com.vocealuga.dao.FuncionarioRepository;
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

class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private ValidationsUtils validations;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private Funcionario funcionario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        funcionario = new Funcionario();
        funcionario.setIdFuncionario(1);
        funcionario.setNome("João Silva");
        funcionario.setCpf("12345678901");
        funcionario.setEmail("joao@email.com");
        funcionario.setSenha("senha123");
    }

    @Test
    void testGetAllFuncionarios_Sucesso() {
        List<Funcionario> funcionarios = Arrays.asList(funcionario, new Funcionario());
        when(funcionarioRepository.findAll()).thenReturn(funcionarios);

        List<Funcionario> resultado = funcionarioService.getAllFuncionarios();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(funcionarioRepository, times(1)).findAll();
    }

    @Test
    void testGetFuncionarioById_Sucesso() {
        when(funcionarioRepository.findById(1)).thenReturn(Optional.of(funcionario));

        Optional<Funcionario> resultado = funcionarioService.getFuncionarioById(1);

        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
        verify(funcionarioRepository, times(1)).findById(1);
    }

    @Test
    void testGetFuncionarioById_NaoEncontrado() {
        when(funcionarioRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Funcionario> resultado = funcionarioService.getFuncionarioById(1);

        assertFalse(resultado.isPresent());
        verify(funcionarioRepository, times(1)).findById(1);
    }

    @Test
    void testCreateFuncionario_Sucesso() {
        when(ValidationsUtils.isValidCPF("12345678901")).thenReturn(true);
        when(validations.isEmailGloballyUnique("joao@email.com")).thenReturn(true);
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);

        Funcionario resultado = funcionarioService.createFuncionario(funcionario);

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        verify(funcionarioRepository, times(1)).save(funcionario);
    }

    @Test
    void testCreateFuncionario_CpfInvalido() {
        when(ValidationsUtils.isValidCPF("123")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> funcionarioService.createFuncionario(funcionario),
                "CPF inválido!");
    }

    @Test
    void testCreateFuncionario_EmailJaCadastrado() {
        when(ValidationsUtils.isValidCPF("12345678901")).thenReturn(true);
        when(validations.isEmailGloballyUnique("joao@email.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> funcionarioService.createFuncionario(funcionario),
                "E-mail já cadastrado!");
    }

    @Test
    void testUpdateFuncionario_Sucesso() {
        Funcionario novoFuncionario = new Funcionario();
        novoFuncionario.setNome("Maria Oliveira");
        novoFuncionario.setCpf("98765432100");
        novoFuncionario.setEmail("maria@email.com");
        novoFuncionario.setSenha("nova123");
        when(funcionarioRepository.findById(1)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);

        Funcionario resultado = funcionarioService.updateFuncionario(1, novoFuncionario);

        assertNotNull(resultado);
        assertEquals("Maria Oliveira", resultado.getNome());
        verify(funcionarioRepository, times(1)).findById(1);
        verify(funcionarioRepository, times(1)).save(funcionario);
    }

    @Test
    void testUpdateFuncionario_NaoEncontrado() {
        when(funcionarioRepository.findById(1)).thenReturn(Optional.empty());

        Funcionario novoFuncionario = new Funcionario();

        assertThrows(RuntimeException.class, () -> funcionarioService.updateFuncionario(1, novoFuncionario),
                "Funcionario not found with id 1");
        verify(funcionarioRepository, times(1)).findById(1);
        verify(funcionarioRepository, never()).save(any());
    }

    @Test
    void testDeleteFuncionario_Sucesso() {
        doNothing().when(funcionarioRepository).deleteById(1);

        funcionarioService.deleteFuncionario(1);
        verify(funcionarioRepository, times(1)).deleteById(1);
    }

    @Test
    void testLogin_Sucesso() {
        when(funcionarioRepository.findByEmailAndSenha("joao@email.com", "senha123")).thenReturn(Optional.of(funcionario));

        Optional<Funcionario> resultado = funcionarioService.login("joao@email.com", "senha123");

        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
        verify(funcionarioRepository, times(1)).findByEmailAndSenha("joao@email.com", "senha123");
    }

    @Test
    void testLogin_Falha() {
        when(funcionarioRepository.findByEmailAndSenha("joao@email.com", "senhaerrada")).thenReturn(Optional.empty());

        Optional<Funcionario> resultado = funcionarioService.login("joao@email.com", "senhaerrada");
        
        assertFalse(resultado.isPresent());
        verify(funcionarioRepository, times(1)).findByEmailAndSenha("joao@email.com", "senhaerrada");
    }
}