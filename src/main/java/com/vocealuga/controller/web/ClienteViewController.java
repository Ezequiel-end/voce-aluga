package com.vocealuga.controller.web;

import org.springframework.context.annotation.Profile;
import com.vocealuga.model.Cliente;
import com.vocealuga.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Profile("web")
@Controller
@RequestMapping("/clientes")
public class ClienteViewController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteViewController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    
    @GetMapping
    public String listClientes(Model model) {
        model.addAttribute("clientes", clienteService.getAllClientes());
        return "clientes";  
    }

    
    @GetMapping("/{id}")
    public String getClienteById(@PathVariable Integer id, Model model) {
        Cliente cliente = clienteService.getClienteById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
        model.addAttribute("cliente", cliente);
        return "form-cliente";  
    }

    
    @GetMapping("/form")
    public String showForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "form-cliente";  
    }

        @PostMapping("/save")
    public String saveCliente(@ModelAttribute("cliente") Cliente cliente) {
        if (cliente.getIdCliente() != null) {
            clienteService.updateCliente(cliente.getIdCliente(), cliente);
        } else {
            clienteService.createCliente(cliente);
        }
        return "redirect:/clientes";  
    }

    
    @GetMapping("/delete/{id}")
    public String deleteCliente(@PathVariable Integer id) {
        clienteService.deleteCliente(id);
        return "redirect:/clientes";  
    }
}
