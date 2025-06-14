package com.vocealuga.controller;

import com.vocealuga.model.Filial;
import com.vocealuga.service.FilialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filiais")
public class FilialController {

    private final FilialService filialService;

    @Autowired
    public FilialController(FilialService filialService) {
        this.filialService = filialService;
    }

    @GetMapping
    public List<Filial> getAllFiliais() {
        return filialService.getAllFiliais();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Filial> getFilialById(@PathVariable Integer id) {
        return filialService.getFilialById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Filial> createFilial(@RequestBody Filial filial) {
        Filial savedFilial = filialService.createFilial(filial);
        return new ResponseEntity<>(savedFilial, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Filial> updateFilial(@PathVariable Integer id, @RequestBody Filial filial) {
        try {
            Filial updatedFilial = filialService.updateFilial(id, filial);
            return ResponseEntity.ok(updatedFilial);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilial(@PathVariable Integer id) {
        try {
            filialService.deleteFilial(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}