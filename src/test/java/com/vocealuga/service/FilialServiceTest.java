package com.vocealuga.service;

import com.vocealuga.model.Filial;
import com.vocealuga.dao.FilialRepository;
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

class FilialServiceTest {

    @Mock
    private FilialRepository filialRepository;

    @InjectMocks
    private FilialService filialService;

    private Filial filial;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filial = new Filial();
        filial.setIdFilial(1);
        filial.setNome("Filial Teste");
        filial.setEndereco("Rua Exemplo, 123");
        filial.setCapacidade(10);
    }

    @Test
    void testGetAllFiliais_Sucesso() {
        List<Filial> filiais = Arrays.asList(filial, new Filial());
        when(filialRepository.findAll()).thenReturn(filiais);

        List<Filial> resultado = filialService.getAllFiliais();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(filialRepository, times(1)).findAll();
    }

    @Test
    void testGetFilialById_Sucesso() {
        when(filialRepository.findById(1)).thenReturn(Optional.of(filial));

        Optional<Filial> resultado = filialService.getFilialById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Filial Teste", resultado.get().getNome());
        verify(filialRepository, times(1)).findById(1);
    }

    @Test
    void testGetFilialById_NaoEncontrado() {
        when(filialRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Filial> resultado = filialService.getFilialById(1);

        assertFalse(resultado.isPresent());
        verify(filialRepository, times(1)).findById(1);
    }

    @Test
    void testCreateFilial_Sucesso() {
        when(filialRepository.save(filial)).thenReturn(filial);

        Filial resultado = filialService.createFilial(filial);

        assertNotNull(resultado);
        assertEquals("Filial Teste", resultado.getNome());
        verify(filialRepository, times(1)).save(filial);
    }

    @Test
    void testUpdateFilial_Sucesso() {
        Filial novaFilial = new Filial();
        novaFilial.setNome("Nova Filial");
        novaFilial.setEndereco("Rua Nova, 456");
        novaFilial.setCapacidade(15);
        when(filialRepository.findById(1)).thenReturn(Optional.of(filial));
        when(filialRepository.save(filial)).thenReturn(filial);

        Filial resultado = filialService.updateFilial(1, novaFilial);

        assertNotNull(resultado);
        assertEquals("Nova Filial", resultado.getNome());
        verify(filialRepository, times(1)).findById(1);
        verify(filialRepository, times(1)).save(filial);
    }

    @Test
    void testUpdateFilial_NaoEncontrado() {
        when(filialRepository.findById(1)).thenReturn(Optional.empty());

        Filial novaFilial = new Filial();

        assertThrows(RuntimeException.class, () -> filialService.updateFilial(1, novaFilial),
                "Filial not found with id 1");
        verify(filialRepository, times(1)).findById(1);
        verify(filialRepository, never()).save(any());
    }

    @Test
    void testDeleteFilial_Sucesso() {
        doNothing().when(filialRepository).deleteById(1);
        
        filialService.deleteFilial(1);
        verify(filialRepository, times(1)).deleteById(1);
    }
}