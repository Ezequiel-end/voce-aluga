package com.vocealuga.controller.web;

import com.vocealuga.model.Cliente;
import com.vocealuga.model.Funcionario;
import com.vocealuga.service.ClienteService;
import com.vocealuga.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class LoginController {
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    @PostMapping("/login")
    public String processLogin(@RequestParam String email, @RequestParam String senha, Model model) {
        Optional<Funcionario> funcionario = funcionarioService.login(email, senha);
        Optional<Cliente> cliente = clienteService.login(email, senha);

        if (funcionario.isPresent()) {
            return "redirect:/funcionario/dashboard";
        } else if (cliente.isPresent()) {
            return "redirect:/dashboard-cliente";
        } else {
            model.addAttribute("erro", "E-mail ou senha inválidos.");
            return "login";
        }
    }
}
