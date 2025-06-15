package com.vocealuga.service;

import com.vocealuga.model.Funcionario;
import com.vocealuga.dao.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    @Autowired
    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Funcionario> getAllFuncionarios() {
        return funcionarioRepository.findAll();
    }

    public Optional<Funcionario> getFuncionarioById(Integer id) {
        return funcionarioRepository.findById(id);
    }

    public Funcionario createFuncionario(Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
    }

    public Funcionario updateFuncionario(Integer id, Funcionario funcionarioDetails) {
        return funcionarioRepository.findById(id)
                .map(funcionario -> {
                    funcionario.setFilial(funcionarioDetails.getFilial());
                    funcionario.setCpf(funcionarioDetails.getCpf());
                    funcionario.setNome(funcionarioDetails.getNome());
                    funcionario.setEmail(funcionarioDetails.getEmail());
                    funcionario.setSenha(funcionarioDetails.getSenha());
                    return funcionarioRepository.save(funcionario);
                }).orElseThrow(() -> new RuntimeException("Funcionario not found with id " + id));
    }

    public void deleteFuncionario(Integer id) {
        funcionarioRepository.deleteById(id);
    }

    public Optional<Funcionario> login(String email, String senha) {
        return funcionarioRepository.findByEmailAndSenha(email, senha);
    }
}