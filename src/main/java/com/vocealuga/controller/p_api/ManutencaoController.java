package com.vocealuga.controller.p_api;

import com.vocealuga.model.Manutencao;
import com.vocealuga.service.ManutencaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manutencoes")
public class ManutencaoController {

    private final ManutencaoService manutencaoService;

    @Autowired
    public ManutencaoController(ManutencaoService manutencaoService) {
        this.manutencaoService = manutencaoService;
    }

    @GetMapping
    public List<Manutencao> getAllManutencoes() {
        return manutencaoService.getAllManutencoes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Manutencao> getManutencaoById(@PathVariable Integer id) {
        return manutencaoService.getManutencaoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Manutencao> createManutencao(@RequestBody Manutencao manutencao) {
        Manutencao savedManutencao = manutencaoService.createManutencao(manutencao);
        return new ResponseEntity<>(savedManutencao, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Manutencao> updateManutencao(@PathVariable Integer id, @RequestBody Manutencao manutencao) {
        try {
            Manutencao updatedManutencao = manutencaoService.updateManutencao(id, manutencao);
            return ResponseEntity.ok(updatedManutencao);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteManutencao(@PathVariable Integer id) {
        try {
            manutencaoService.deleteManutencao(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}