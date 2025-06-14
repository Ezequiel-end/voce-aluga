package com.vocealuga.service;

import com.vocealuga.model.Cliente;
import com.vocealuga.dao.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> getClienteById(Integer id) {
        return clienteRepository.findById(id);
    }

    public Cliente createCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente updateCliente(Integer id, Cliente clienteDetails) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setCpf(clienteDetails.getCpf());
                    cliente.setNome(clienteDetails.getNome());
                    cliente.setEmail(clienteDetails.getEmail());
                    cliente.setSenha(clienteDetails.getSenha());
                    cliente.setCnh(clienteDetails.getCnh());
                    cliente.setDataNascimento(clienteDetails.getDataNascimento());
                    return clienteRepository.save(cliente);
                }).orElseThrow(() -> new RuntimeException("Cliente not found with id " + id));
    }

    public void deleteCliente(Integer id) {
        clienteRepository.deleteById(id);
    }
}