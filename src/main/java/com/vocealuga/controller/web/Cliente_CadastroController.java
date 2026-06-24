package com.vocealuga.controller.web;

import org.springframework.context.annotation.Profile;
import com.vocealuga.model.Cliente;
import com.vocealuga.service.ClienteService;
import com.vocealuga.utils.ValidationsUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Profile("web")
@Controller
@RequestMapping("/cliente")
public class Cliente_CadastroController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ValidationsUtils validation;

    @GetMapping("/cadastro")
    public String showCadastroForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente/cadastro";
    }

    @PostMapping("/cadastro")
    public String processCadastro(@ModelAttribute Cliente cliente, Model model, RedirectAttributes redirectAttributes) {
        try {
            String cpfLimpo = cliente.getCpf().replaceAll("[^0-9]", "");
            cliente.setCpf(cpfLimpo);

            if (!ValidationsUtils.isValidCPF(cliente.getCpf())) {
                redirectAttributes.addFlashAttribute("errorMessage", "CPF inválido!");
                return "redirect:/cliente/cadastro";
            }

            if (!validation.isCpfGloballyUnique(cliente.getCpf())) {
                redirectAttributes.addFlashAttribute("errorMessage", "CPF já cadastrado no sistema!");
                return "redirect:/cliente/cadastro";
            }

            if (!validation.isEmailGloballyUnique(cliente.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "E-mail já cadastrado!");
                return "redirect:/c liente/cadastro";
            }

            String cnhLimpa = cliente.getCnh().replaceAll("[^0-9]", "");
            cliente.setCnh(cnhLimpa);

            if (!ValidationsUtils.isValidCNH(cliente.getCnh())) {
                redirectAttributes.addFlashAttribute("errorMessage", "CNH inválida!");
                return "redirect:/cliente/cadastro";
            }

            if (!validation.isMaiorDeIdade(cliente.getDataNascimento())) {
                redirectAttributes.addFlashAttribute("errorMessage", "É necessário ter pelo menos 18 anos.");
                return "redirect:/cliente/cadastro";
            }

            clienteService.createCliente(cliente);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente cadastrado com sucesso!");

            return "redirect:/cliente/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cadastrar Cliente: " + e.getMessage());
            return "redirect:/cliente/cadastro";
        }
    }

}