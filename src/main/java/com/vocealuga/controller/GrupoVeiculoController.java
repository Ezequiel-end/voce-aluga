package com.vocealuga.controller;

import com.vocealuga.model.GrupoVeiculo;
import com.vocealuga.service.GrupoVeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grupos-veiculo")
public class GrupoVeiculoController {

    private final GrupoVeiculoService grupoVeiculoService;

    @Autowired
    public GrupoVeiculoController(GrupoVeiculoService grupoVeiculoService) {
        this.grupoVeiculoService = grupoVeiculoService;
    }

    @GetMapping
    public List<GrupoVeiculo> getAllGruposVeiculo() {
        return grupoVeiculoService.getAllGruposVeiculo();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoVeiculo> getGrupoVeiculoById(@PathVariable Integer id) {
        return grupoVeiculoService.getGrupoVeiculoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GrupoVeiculo> createGrupoVeiculo(@RequestBody GrupoVeiculo grupoVeiculo) {
        GrupoVeiculo savedGrupoVeiculo = grupoVeiculoService.createGrupoVeiculo(grupoVeiculo);
        return new ResponseEntity<>(savedGrupoVeiculo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrupoVeiculo> updateGrupoVeiculo(@PathVariable Integer id, @RequestBody GrupoVeiculo grupoVeiculo) {
        try {
            GrupoVeiculo updatedGrupoVeiculo = grupoVeiculoService.updateGrupoVeiculo(id, grupoVeiculo);
            return ResponseEntity.ok(updatedGrupoVeiculo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrupoVeiculo(@PathVariable Integer id) {
        try {
            grupoVeiculoService.deleteGrupoVeiculo(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}