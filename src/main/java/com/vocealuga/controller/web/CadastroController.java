package com.vocealuga.controller.web;

import com.vocealuga.model.Cliente;
import com.vocealuga.service.ClienteService;
import com.vocealuga.utils.ValidationsUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CadastroController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ValidationsUtils validation;

    @GetMapping("/cadastro")
    public String showCadastroForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String processCadastro(@ModelAttribute Cliente cliente, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (!validation.isValidCPF(cliente.getCpf())) {
                redirectAttributes.addFlashAttribute("errorMessage", "CPF inválido!");
                return "redirect:/cadastro";
            }

            if (!validation.isEmailGloballyUnique(cliente.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "E-mail já cadastrado!");
                return "redirect:/cadastro";
            }

            if (!validation.isValidCNH(cliente.getCnh())) {
                redirectAttributes.addFlashAttribute("errorMessage", "CNH inválida!");
                return "redirect:/cadastro";
            } 

            if (!validation.isMaiorDeIdade(cliente.getDataNascimento())) {
                redirectAttributes.addFlashAttribute("errorMessage", "É necessário ter pelo menos 18 anos.");
                return "redirect:/cadastro";
            }

            clienteService.createCliente(cliente);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente cadastrado com sucesso!");
            
            return "redirect:/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cadastrar Cliente: " + e.getMessage());
            return "redirect:/cadastro";
        }
    }

}
