package com.vocealuga.service;

import com.vocealuga.model.Filial;
import com.vocealuga.dao.FilialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FilialService {

    private final FilialRepository filialRepository;

    @Autowired
    public FilialService(FilialRepository filialRepository) {
        this.filialRepository = filialRepository;
    }

    public List<Filial> getAllFiliais() {
        return filialRepository.findAll();
    }

    public Optional<Filial> getFilialById(Integer id) {
        return filialRepository.findById(id);
    }

    public Filial createFilial(Filial filial) {
        return filialRepository.save(filial);
    }

    public Filial updateFilial(Integer id, Filial filialDetails) {
        return filialRepository.findById(id)
                .map(filial -> {
                    filial.setNome(filialDetails.getNome());
                    filial.setEndereco(filialDetails.getEndereco());
                    filial.setCapacidade(filialDetails.getCapacidade());
                    return filialRepository.save(filial);
                }).orElseThrow(() -> new RuntimeException("Filial not found with id " + id));
    }

    public void deleteFilial(Integer id) {
        filialRepository.deleteById(id);
    }
}