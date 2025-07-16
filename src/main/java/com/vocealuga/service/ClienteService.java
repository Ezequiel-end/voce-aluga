package com.vocealuga.service;

import com.vocealuga.model.Cliente;
import com.vocealuga.utils.ValidationsUtils;
import com.vocealuga.dao.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    // Mantido 'validation' injetado, pois 'isEmailGloballyUnique' pode não ser
    // estático puro
    private final ValidationsUtils validation;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, ValidationsUtils validation) {
        this.clienteRepository = clienteRepository;
        this.validation = validation;
    }

    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> getClienteById(Integer id) {
        return clienteRepository.findById(id);
    }

    public Cliente createCliente(Cliente cliente) {
        // Correção: Acessando isValidCPF de forma estática
        if (!ValidationsUtils.isValidCPF(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF inválido!");
        }

        // Correção: Acessando isValidCNH de forma estática
        if (!ValidationsUtils.isValidCNH(cliente.getCnh())) {
            throw new IllegalArgumentException("CNH inválida");
        }

        if (!validation.isEmailGloballyUnique(cliente.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }
        // Garante que novos clientes comecem com 0 pontos de fidelidade
        if (cliente.getPontosFidelidade() == null) {
            cliente.setPontosFidelidade(0);
        }
        return clienteRepository.save(cliente);
    }

    public Cliente updateCliente(Integer id, Cliente clienteDetails) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    // cliente.setCpf(clienteDetails.getCpf());
                    cliente.setNome(clienteDetails.getNome());
                    cliente.setEmail(clienteDetails.getEmail());
                    // A senha só deve ser atualizada se for fornecida no clienteDetails
                    if (clienteDetails.getSenha() != null && !clienteDetails.getSenha().isEmpty()) {
                        cliente.setSenha(clienteDetails.getSenha());
                    }
                    // cliente.setCnh(clienteDetails.getCnh());
                    // cliente.setDataNascimento(clienteDetails.getDataNascimento());

                    return clienteRepository.save(cliente);
                }).orElseThrow(() -> new RuntimeException("Cliente not found with id " + id));
    }

    /**
     * Adiciona pontos de fidelidade a um cliente existente.
     * 
     * @param clienteId           O ID do cliente.
     * @param pontosParaAdicionar O número de pontos a ser adicionado.
     * @return O cliente atualizado.
     * @throws RuntimeException se o cliente não for encontrado.
     */
    public Cliente addPontosFidelidade(Integer clienteId, Integer pontosParaAdicionar) {
        return clienteRepository.findById(clienteId)
                .map(cliente -> {
                    Integer pontosAtuais = cliente.getPontosFidelidade();
                    cliente.setPontosFidelidade(pontosAtuais + pontosParaAdicionar);
                    return clienteRepository.save(cliente);
                }).orElseThrow(() -> new RuntimeException("Cliente not found with id " + clienteId));
    }

    public void deleteCliente(Integer id) {
        clienteRepository.deleteById(id);
    }

    public Optional<Cliente> login(String email, String senha) {
        return clienteRepository.findByEmailAndSenha(email, senha);
    }

    public Optional<Cliente> findClienteByCpf(String cpf) {
        String cleanCpf = cpf.replaceAll("[^0-9]", "");

        if (!ValidationsUtils.isValidCPF(cleanCpf)) {
            throw new IllegalArgumentException("CPF inválido: " + cleanCpf);
        }
        return clienteRepository.findByCpf(cleanCpf)
                .or(() -> {
                    throw new RuntimeException("Cliente não registrado com CPF: " + cleanCpf);
                });
    }

    public boolean existsByCpf(String cpf) {
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        return clienteRepository.existsByCpf(cleanCpf);
    }

}