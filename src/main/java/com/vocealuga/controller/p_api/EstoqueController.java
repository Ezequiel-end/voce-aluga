package com.vocealuga.controller.p_api;

import com.vocealuga.model.Estoque;
import com.vocealuga.service.EstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoques")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @Autowired
    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping
    public List<Estoque> getAllEstoques() {
        return estoqueService.getAllEstoques();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estoque> getEstoqueById(@PathVariable Integer id) {
        return estoqueService.getEstoqueById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Estoque> createEstoque(@RequestBody Estoque estoque) {
        Estoque savedEstoque = estoqueService.createEstoque(estoque);
        return new ResponseEntity<>(savedEstoque, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estoque> updateEstoque(@PathVariable Integer id, @RequestBody Estoque estoque) {
        try {
            Estoque updatedEstoque = estoqueService.updateEstoque(id, estoque);
            return ResponseEntity.ok(updatedEstoque);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstoque(@PathVariable Integer id) {
        try {
            estoqueService.deleteEstoque(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}