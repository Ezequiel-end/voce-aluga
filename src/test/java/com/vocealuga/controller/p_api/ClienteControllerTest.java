package com.vocealuga.controller.p_api;

import com.vocealuga.model.Cliente;
import com.vocealuga.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setIdCliente(1); // idCliente
        cliente.setNome("João Silva");
        cliente.setCpf("12345678901");
        cliente.setCnh("987654321");
        cliente.setEmail("joao@email.com");
        cliente.setDataNascimento(LocalDate.of(1990, 1, 15));
        cliente.setSenha("senha123");
    }

    @Test
    void testGetAllClientes_Sucesso() throws Exception {
        when(clienteService.getAllClientes()).thenReturn(Arrays.asList(cliente, new Cliente()));
        mockMvc.perform(get("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
        verify(clienteService, times(1)).getAllClientes();
    }

    @Test
    void testGetClienteById_Sucesso() throws Exception {
        when(clienteService.getClienteById(1)).thenReturn(Optional.of(cliente));
        mockMvc.perform(get("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.cnh").value("987654321"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.dataNascimento").value("1990-01-15"))
                .andExpect(jsonPath("$.senha").value("senha123"));
        verify(clienteService, times(1)).getClienteById(1);
    }

    @Test
    void testGetClienteById_NaoEncontrado() throws Exception {
        when(clienteService.getClienteById(1)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(clienteService, times(1)).getClienteById(1);
    }

    @Test
    void testCreateCliente_Sucesso() throws Exception {
        when(clienteService.createCliente(any(Cliente.class))).thenReturn(cliente);
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"João Silva\",\"cpf\":\"12345678901\",\"cnh\":\"987654321\",\"email\":\"joao@email.com\",\"dataNascimento\":\"1990-01-15\",\"senha\":\"senha123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.cnh").value("987654321"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.dataNascimento").value("1990-01-15"))
                .andExpect(jsonPath("$.senha").value("senha123"));
        verify(clienteService, times(1)).createCliente(any(Cliente.class));
    }

    @Test
    void testUpdateCliente_Sucesso() throws Exception {
        Cliente updatedCliente = new Cliente();
        updatedCliente.setIdCliente(1);
        updatedCliente.setNome("Maria Oliveira");
        updatedCliente.setCpf("98765432100");
        updatedCliente.setCnh("123456789");
        updatedCliente.setEmail("maria@email.com");
        updatedCliente.setDataNascimento(LocalDate.of(1995, 5, 20));
        updatedCliente.setSenha("novaSenha");
        when(clienteService.updateCliente(anyInt(), any(Cliente.class))).thenReturn(updatedCliente); // Usar any() para capturar

        mockMvc.perform(put("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Maria Oliveira\",\"cpf\":\"98765432100\",\"cnh\":\"123456789\",\"email\":\"maria@email.com\",\"dataNascimento\":\"1995-05-20\",\"senha\":\"novaSenha\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Oliveira"))
                .andExpect(jsonPath("$.cpf").value("98765432100"))
                .andExpect(jsonPath("$.cnh").value("123456789"))
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.dataNascimento").value("1995-05-20"))
                .andExpect(jsonPath("$.senha").value("novaSenha"));
        verify(clienteService, times(1)).updateCliente(anyInt(), any(Cliente.class));
    }

    @Test
    void testUpdateCliente_NaoEncontrado() throws Exception {
        Cliente updatedCliente = new Cliente();
        updatedCliente.setNome("Maria Oliveira");
        updatedCliente.setCpf("98765432100");
        updatedCliente.setCnh("123456789");
        updatedCliente.setEmail("maria@email.com");
        updatedCliente.setDataNascimento(LocalDate.of(1995, 5, 20));
        updatedCliente.setSenha("novaSenha");
        doThrow(new RuntimeException("Cliente not found with id 1")).when(clienteService).updateCliente(eq(1), any(Cliente.class)); // Especificar o ID

        mockMvc.perform(put("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Maria Oliveira\",\"cpf\":\"98765432100\",\"cnh\":\"123456789\",\"email\":\"maria@email.com\",\"dataNascimento\":\"1995-05-20\",\"senha\":\"novaSenha\"}"))
                .andExpect(status().isNotFound());
        verify(clienteService, times(1)).updateCliente(eq(1), any(Cliente.class));
    }

    @Test
    void testDeleteCliente_Sucesso() throws Exception {
        doNothing().when(clienteService).deleteCliente(1);
        mockMvc.perform(delete("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(clienteService, times(1)).deleteCliente(1);
    }

    @Test
    void testDeleteCliente_NaoEncontrado() throws Exception {
        doThrow(new RuntimeException()).when(clienteService).deleteCliente(1);
        mockMvc.perform(delete("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(clienteService, times(1)).deleteCliente(1);
    }
}