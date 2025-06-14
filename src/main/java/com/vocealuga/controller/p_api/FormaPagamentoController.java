package com.vocealuga.controller.p_api;

import com.vocealuga.model.FormaPagamento;
import com.vocealuga.service.FormaPagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formas-pagamento")
public class FormaPagamentoController {

    private final FormaPagamentoService formaPagamentoService;

    @Autowired
    public FormaPagamentoController(FormaPagamentoService formaPagamentoService) {
        this.formaPagamentoService = formaPagamentoService;
    }

    @GetMapping
    public List<FormaPagamento> getAllFormasPagamento() {
        return formaPagamentoService.getAllFormasPagamento();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormaPagamento> getFormaPagamentoById(@PathVariable Integer id) {
        return formaPagamentoService.getFormaPagamentoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FormaPagamento> createFormaPagamento(@RequestBody FormaPagamento formaPagamento) {
        FormaPagamento savedFormaPagamento = formaPagamentoService.createFormaPagamento(formaPagamento);
        return new ResponseEntity<>(savedFormaPagamento, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormaPagamento> updateFormaPagamento(@PathVariable Integer id, @RequestBody FormaPagamento formaPagamento) {
        try {
            FormaPagamento updatedFormaPagamento = formaPagamentoService.updateFormaPagamento(id, formaPagamento);
            return ResponseEntity.ok(updatedFormaPagamento);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormaPagamento(@PathVariable Integer id) {
        try {
            formaPagamentoService.deleteFormaPagamento(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}