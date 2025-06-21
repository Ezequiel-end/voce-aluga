package com.vocealuga.controller.web;

import com.vocealuga.model.*;
import com.vocealuga.service.*;
import com.vocealuga.utils.ValidationsUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional; // Import para Optional
import java.time.LocalDate; // Import para LocalDate, se usado em formulários de data

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
    private final ManutencaoService manutencaoService; // Adicionado ManutencaoService
    private final ValidationsUtils validations;

    @Autowired
    public FuncionarioWebController(FuncionarioService funcionarioService,
                                    VeiculoService veiculoService,
                                    EstoqueService estoqueService,
                                    ReservaService reservaService,
                                    ClienteService clienteService,
                                    FilialService filialService,
                                    GrupoVeiculoService grupoVeiculoService, // Nome da variável ajustado
                                    ManutencaoService manutencaoService, // Injetado ManutencaoService
                                    ValidationsUtils validations) {
        this.funcionarioService = funcionarioService;
        this.veiculoService = veiculoService;
        this.estoqueService = estoqueService;
        this.reservaService = reservaService;
        this.clienteService = clienteService;
        this.filialService = filialService;
        this.grupoVeiculoService = grupoVeiculoService;
        this.manutencaoService = manutencaoService; // Atribuído ManutencaoService
        this.validations = validations;
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
            if (!validations.isValidCPF(funcionario.getCpf())) {
                redirectAttributes.addFlashAttribute("errorMessage", "CPF inválido!");
                return "redirect:/funcionario/cadastrar-funcionario";
            }

            if (!validations.isEmailGloballyUnique(funcionario.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "E-mail já cadastrado!");
                return "redirect:/funcionario/cadastrar-funcionario";
            }

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

    // --- Nova Funcionalidade: Gerenciar Manutenção ---

    // Formulário para Cadastrar Manutenção
    @GetMapping("/manutencao/cadastrar")
    public String showCadastrarManutencaoForm(Model model) {
        model.addAttribute("activeContent", "cadastrar_manutencao");
        model.addAttribute("manutencao", new Manutencao());
        model.addAttribute("veiculos", veiculoService.getAllVeiculos());
        model.addAttribute("funcionarios", funcionarioService.getAllFuncionarios());
        return "funcionario-dashboard";
    }

    // Lógica para Cadastrar Manutenção
    @PostMapping("/manutencao/cadastrar")
    public String cadastrarManutencao(@ModelAttribute Manutencao manutencao,
                                      @RequestParam Integer veiculoId,
                                      @RequestParam Integer funcionarioId,
                                      RedirectAttributes redirectAttributes) {
        try {
            Veiculo veiculo = veiculoService.getVeiculoById(veiculoId)
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado."));
            Funcionario funcionario = funcionarioService.getFuncionarioById(funcionarioId)
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado."));

            manutencao.setVeiculo(veiculo);
            manutencao.setFuncionario(funcionario);
            manutencaoService.createManutencao(manutencao);
            redirectAttributes.addFlashAttribute("successMessage", "Manutenção cadastrada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cadastrar manutenção: " + e.getMessage());
        }
        return "redirect:/funcionario/manutencao/cadastrar";
    }

    // Formulário para Listar e Gerenciar Manutenções (Atualizar/Excluir)
    @GetMapping("/manutencao/gerenciar")
    public String showGerenciarManutencaoForm(Model model) {
        model.addAttribute("activeContent", "gerenciar_manutencao");
        model.addAttribute("manutencoes", manutencaoService.getAllManutencoes());
        model.addAttribute("veiculos", veiculoService.getAllVeiculos()); // Para o formulário de edição
        model.addAttribute("funcionarios", funcionarioService.getAllFuncionarios()); // Para o formulário de edição
        return "funcionario-dashboard";
    }

    // Lógica para Atualizar Manutenção
    @PostMapping("/manutencao/atualizar/{id}")
    public String updateManutencao(@PathVariable Integer id,
                                   @ModelAttribute Manutencao manutencaoDetails,
                                   @RequestParam Integer veiculoId,
                                   @RequestParam Integer funcionarioId,
                                   RedirectAttributes redirectAttributes) {
        try {
            Veiculo veiculo = veiculoService.getVeiculoById(veiculoId)
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado."));
            Funcionario funcionario = funcionarioService.getFuncionarioById(funcionarioId)
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado."));

            manutencaoDetails.setVeiculo(veiculo);
            manutencaoDetails.setFuncionario(funcionario);

            manutencaoService.updateManutencao(id, manutencaoDetails);
            redirectAttributes.addFlashAttribute("successMessage", "Manutenção atualizada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar manutenção: " + e.getMessage());
        }
        return "redirect:/funcionario/manutencao/gerenciar";
    }

    // Lógica para Excluir Manutenção
    @PostMapping("/manutencao/excluir/{id}")
    public String deleteManutencao(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            manutencaoService.deleteManutencao(id);
            redirectAttributes.addFlashAttribute("successMessage", "Manutenção excluída com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao excluir manutenção: " + e.getMessage());
        }
        return "redirect:/funcionario/manutencao/gerenciar";
    }

    // --- Nova Funcionalidade: Alterar Reserva ---

    // Formulário para Alterar Reserva
    @GetMapping("/reservas/alterar")
    public String showAlterarReservaForm(Model model) {
        model.addAttribute("activeContent", "alterar_reserva");
        model.addAttribute("reservas", reservaService.getAllReservas()); // Para selecionar a reserva a ser alterada
        model.addAttribute("funcionarios", funcionarioService.getAllFuncionarios());
        model.addAttribute("filiais", filialService.getAllFiliais());
        model.addAttribute("clientes", clienteService.getAllClientes());
        model.addAttribute("veiculos", veiculoService.getAllVeiculos());
        return "funcionario-dashboard";
    }

    // Lógica para carregar detalhes da reserva para edição (via AJAX ou GET com ID)
    @GetMapping("/reservas/alterar/{id}")
    public String loadReservaForEdit(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Reserva> reservaOptional = reservaService.getReservaById(id);
        if (reservaOptional.isPresent()) {
            model.addAttribute("activeContent", "alterar_reserva_detalhes"); // Um template para edição
            model.addAttribute("reserva", reservaOptional.get());
            model.addAttribute("funcionarios", funcionarioService.getAllFuncionarios());
            model.addAttribute("filiais", filialService.getAllFiliais());
            model.addAttribute("clientes", clienteService.getAllClientes());
            model.addAttribute("veiculos", veiculoService.getAllVeiculos());
            return "funcionario-dashboard";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Reserva não encontrada para alteração.");
            return "redirect:/funcionario/reservas/alterar";
        }
    }


    // Lógica para processar a Alteração da Reserva
    @PostMapping("/reservas/alterar/{id}")
    public String alterarReserva(@PathVariable Integer id,
                                 @ModelAttribute Reserva reservaDetails,
                                 @RequestParam Integer funcionarioId,
                                 @RequestParam Integer filialId,
                                 @RequestParam Integer clienteId,
                                 @RequestParam Integer veiculoId,
                                 RedirectAttributes redirectAttributes) {
        try {
            Funcionario funcionario = funcionarioService.getFuncionarioById(funcionarioId)
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado."));
            Filial filial = filialService.getFilialById(filialId)
                    .orElseThrow(() -> new RuntimeException("Filial não encontrada."));
            Cliente cliente = clienteService.getClienteById(clienteId)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
            Veiculo veiculo = veiculoService.getVeiculoById(veiculoId)
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado."));

            reservaDetails.setFuncionario(funcionario);
            reservaDetails.setFilial(filial);
            reservaDetails.setCliente(cliente);
            reservaDetails.setVeiculo(veiculo);

            reservaService.updateReserva(id, reservaDetails);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva alterada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao alterar reserva: " + e.getMessage());
        }
        return "redirect:/funcionario/reservas/consultar-reservas"; // Redireciona para a lista de reservas
    }

    // --- Nova Funcionalidade: Transferir Veículo de Filial ---

    // Formulário para Transferir Veículo
    @GetMapping("/estoque/transferir-veiculo")
    public String showTransferirVeiculoForm(Model model) {
        model.addAttribute("activeContent", "transferir_veiculo");
        model.addAttribute("estoques", estoqueService.getAllEstoques()); // Para listar veículos em estoque
        model.addAttribute("filiais", filialService.getAllFiliais()); // Para selecionar filial de destino
        return "funcionario-dashboard";
    }

    // Lógica para Transferir Veículo
    @PostMapping("/estoque/transferir-veiculo")
    public String transferirVeiculo(@RequestParam Integer veiculoId,
                                    @RequestParam Integer filialOrigemId,
                                    @RequestParam Integer filialDestinoId,
                                    RedirectAttributes redirectAttributes) {
        try {
            estoqueService.transferirVeiculoParaFilial(veiculoId, filialOrigemId, filialDestinoId);
            redirectAttributes.addFlashAttribute("successMessage", "Veículo transferido com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao transferir veículo: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado ao transferir veículo: " + e.getMessage());
        }
        return "redirect:/funcionario/estoque/verificar-disponibilidade"; // Redireciona para a disponibilidade do estoque
    }
}
