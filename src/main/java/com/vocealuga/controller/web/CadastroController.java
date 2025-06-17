package com.vocealuga.controller.web;

import com.vocealuga.model.Cliente;
import com.vocealuga.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CadastroController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/cadastro")
    public String showCadastroForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String processCadastro(@ModelAttribute Cliente cliente, Model model) {
        // Verifica se e-mail já está cadastrado
        boolean emailJaCadastrado = clienteService
            .getAllClientes()
            .stream()
            .anyMatch(c -> c.getEmail().equalsIgnoreCase(cliente.getEmail()));

        if (emailJaCadastrado) {
            model.addAttribute("erro", "Este e-mail já está cadastrado.");
            return "cadastro";
        }

        clienteService.createCliente(cliente);
        return "redirect:/login";
    }
}
