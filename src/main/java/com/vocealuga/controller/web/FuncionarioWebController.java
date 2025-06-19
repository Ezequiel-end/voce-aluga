package com.vocealuga.controller.web;

import com.vocealuga.model.*;
import com.vocealuga.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("/funcionario")
public class FuncionarioWebController {

    private final FuncionarioService funcionarioService;
    private final VeiculoService veiculoService;
    private final EstoqueService estoqueService;
    private final ReservaService reservaService;
    private final ClienteService clienteService;
    private final FilialService filialService;
    private final GrupoVeiculoService grupoVeiculoService;


    @Autowired
    public FuncionarioWebController(FuncionarioService funcionarioService,
                                    VeiculoService veiculoService,
                                    EstoqueService estoqueService,
                                    ReservaService reservaService,
                                    ClienteService clienteService,
                                    FilialService filialService,
                                    GrupoVeiculoService service) {
        this.funcionarioService = funcionarioService;
        this.veiculoService = veiculoService;
        this.estoqueService = estoqueService;
        this.reservaService = reservaService;
        this.clienteService = clienteService;
        this.filialService = filialService;
        this.grupoVeiculoService = service;
    }

    @GetMapping("/dashboard")
    public String funcionarioDashboard(Model model) {
        model.addAttribute("activeContent", "home"); // Conteúdo inicial
        return "funcionario-dashboard";
    }

    // --- Funcionalidades do Menu ---

    // Cadastrar Funcionário
    @GetMapping("/cadastrar-funcionario")
    public String showRegisterFuncionarioForm(Model model) {
        model.addAttribute("activeContent", "register_funcionario");
        model.addAttribute("funcionario", new Funcionario());
        model.addAttribute("filiais", filialService.getAllFiliais());
        return "funcionario-dashboard";
    }

    @PostMapping("/cadastrar-funcionario")
    public String registerFuncionario(@ModelAttribute Funcionario funcionario, RedirectAttributes redirectAttributes) {
        try {
            funcionarioService.createFuncionario(funcionario);
            redirectAttributes.addFlashAttribute("successMessage", "Funcionário cadastrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cadastrar funcionário: " + e.getMessage());
        }
        return "redirect:/funcionario/cadastrar-funcionario";
    }

    // Gerenciar Estoque - Adicionar Veículo
    @GetMapping("/estoque/adicionar-veiculo")
    public String showAddVeiculoForm(Model model) {
        model.addAttribute("activeContent", "add_veiculo");
        model.addAttribute("veiculo", new Veiculo());
        model.addAttribute("gruposVeiculo", grupoVeiculoService.getAllGruposVeiculo());
        model.addAttribute("filiais", filialService.getAllFiliais()); // Para associar ao estoque
        model.addAttribute("funcionarios", funcionarioService.getAllFuncionarios()); // Para associar ao estoque
        return "funcionario-dashboard";
    }

    @PostMapping("/estoque/adicionar-veiculo")
    public String addVeiculo(@ModelAttribute Veiculo veiculo,
                             @RequestParam Integer filialId,
                             @RequestParam Integer funcionarioId,
                             RedirectAttributes redirectAttributes) {
        try {
            // Primeiro salva o veículo
            Veiculo savedVeiculo = veiculoService.createVeiculo(veiculo);

            // Depois cria a entrada no estoque
            Filial filial = filialService.getFilialById(filialId)
                    .orElseThrow(() -> new RuntimeException("Filial não encontrada"));
            Funcionario funcionario = funcionarioService.getFuncionarioById(funcionarioId)
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

            Estoque estoque = new Estoque(filial, savedVeiculo, funcionario, "Disponível"); // Ou outro status inicial
            estoqueService.createEstoque(estoque);

            redirectAttributes.addFlashAttribute("successMessage", "Veículo e estoque atualizados com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao adicionar veículo: " + e.getMessage());
        }
        return "redirect:/funcionario/estoque/adicionar-veiculo";
    }

    // Gerenciar Estoque - Remover Veículo
    @GetMapping("/estoque/remover-veiculo")
    public String showRemoveVeiculoForm(Model model) {
        model.addAttribute("activeContent", "remove_veiculo");
        model.addAttribute("veiculos", veiculoService.getAllVeiculos()); // Para seleção
        return "funcionario-dashboard";
    }

    @PostMapping("/estoque/remover-veiculo")
    public String removeVeiculo(@RequestParam Integer veiculoId, RedirectAttributes redirectAttributes) {
        try {
            // Poderíamos adicionar lógica para verificar se o veículo não está em uma reserva ativa
            veiculoService.deleteVeiculo(veiculoId);
            redirectAttributes.addFlashAttribute("successMessage", "Veículo removido com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao remover veículo: " + e.getMessage());
        }
        return "redirect:/funcionario/estoque/remover-veiculo";
    }

    // Gerenciar Estoque - Verificar Disponibilidade
    @GetMapping("/estoque/verificar-disponibilidade")
    public String showCheckAvailabilityForm(Model model) {
        model.addAttribute("activeContent", "check_availability");
        List<Estoque> veiculosEmEstoque = estoqueService.getAllEstoques();
        model.addAttribute("veiculosEmEstoque", veiculosEmEstoque);
        return "funcionario-dashboard";
    }


    // Gerenciar Reservas - Criar Reserva
    @GetMapping("/reservas/criar-reserva")
    public String showCreateReservaForm(Model model) {
        model.addAttribute("activeContent", "create_reserva");
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("funcionarios", funcionarioService.getAllFuncionarios());
        model.addAttribute("filiais", filialService.getAllFiliais());
        model.addAttribute("clientes", clienteService.getAllClientes());
        model.addAttribute("veiculos", veiculoService.getAllVeiculos());
        return "funcionario-dashboard";
    }

    @PostMapping("/reservas/criar-reserva")
    public String createReserva(@ModelAttribute Reserva reserva,
                                 @RequestParam Integer funcionarioId,
                                 @RequestParam Integer filialId,
                                 @RequestParam Integer clienteId,
                                 @RequestParam Integer veiculoId,
                                 RedirectAttributes redirectAttributes) {
        try {
            Funcionario funcionario = funcionarioService.getFuncionarioById(funcionarioId)
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
            Filial filial = filialService.getFilialById(filialId)
                    .orElseThrow(() -> new RuntimeException("Filial não encontrada"));
            Cliente cliente = clienteService.getClienteById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            Veiculo veiculo = veiculoService.getVeiculoById(veiculoId)
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

            reserva.setFuncionario(funcionario);
            reserva.setFilial(filial);
            reserva.setCliente(cliente);
            reserva.setVeiculo(veiculo);
            reserva.setStatus("Ativa"); // Status inicial da reserva

            reservaService.createReserva(reserva);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva criada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao criar reserva: " + e.getMessage());
        }
        return "redirect:/funcionario/reservas/criar-reserva";
    }


    // Gerenciar Reservas - Cancelar Reserva
    @GetMapping("/reservas/cancelar-reserva")
    public String showCancelReservaForm(Model model) {
        model.addAttribute("activeContent", "cancel_reserva");
        model.addAttribute("reservas", reservaService.getAllReservas()); // Para seleção
        return "funcionario-dashboard";
    }

    @PostMapping("/reservas/cancelar-reserva")
    public String cancelReserva(@RequestParam Integer reservaId, RedirectAttributes redirectAttributes) {
        try {
            Reserva reserva = reservaService.getReservaById(reservaId)
                    .orElseThrow(() -> new RuntimeException("Reserva não encontrada com o ID: " + reservaId));
            reserva.setStatus("Cancelada"); // Altera o status da reserva
            reservaService.updateReserva(reservaId, reserva);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva cancelada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cancelar reserva: " + e.getMessage());
        }
        return "redirect:/funcionario/reservas/cancelar-reserva";
    }

    // Gerenciar Reservas - Consultar Reservas
    @GetMapping("/reservas/consultar-reservas")
    public String showConsultReservasForm(Model model) {
        model.addAttribute("activeContent", "consult_reservas");
        model.addAttribute("reservas", reservaService.getAllReservas());
        return "funcionario-dashboard";
    }
}