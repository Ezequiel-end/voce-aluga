package com.vocealuga.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardFuncionario {

    @GetMapping("/dashboard-funcionario")
    public String showDashboardFuncionario() {
        return "dashboard-funcionario";
    }
}