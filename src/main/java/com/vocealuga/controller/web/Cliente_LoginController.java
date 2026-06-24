// src/main/java/com/vocealuga/controller/web/Cliente_LoginController.java
package com.vocealuga.controller.web;

import org.springframework.context.annotation.Profile;
import com.vocealuga.model.Cliente;
import com.vocealuga.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Profile("web")
@Controller
@RequestMapping("/cliente")
public class Cliente_LoginController {
    @Autowired
    private ClienteService clienteService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        if (!model.containsAttribute("cliente")) {
            model.addAttribute("cliente", new Cliente());
        }
        return "cliente/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                            @RequestParam String senha,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Optional<Cliente> cliente = clienteService.login(email, senha);

        if (cliente.isPresent()) {
            session.setAttribute("loggedInClient", cliente.get());
            return "redirect:/cliente/dashboard";
        } else {
            redirectAttributes.addFlashAttribute("error", "E-mail ou senha inválidos.");
            return "redirect:/cliente/login";
        }
    }
}