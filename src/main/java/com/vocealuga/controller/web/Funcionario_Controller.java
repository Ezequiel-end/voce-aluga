package com.vocealuga.controller.web;

import com.vocealuga.model.*;
import com.vocealuga.service.*;
import com.vocealuga.utils.ValidationsUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/funcionario")
public class Funcionario_Controller {

    private final FuncionarioService funcionarioService;
    private final VeiculoService veiculoService;
    private final EstoqueService estoqueService;
    private final ReservaService reservaService;
    private final ClienteService clienteService;
    private final FilialService filialService;
    private final GrupoVeiculoService grupoVeiculoService;
    private final ManutencaoService manutencaoService;
    private final ValidationsUtils validations;

    @Autowired
    public Funcionario_Controller(FuncionarioService funcionarioService,
                                    VeiculoService veiculoService,
                                    EstoqueService estoqueService,
                                    ReservaService reservaService,
                                    ClienteService clienteService,
                                    FilialService filialService,
                                    GrupoVeiculoService grupoVeiculoService,
                                    ManutencaoService manutencaoService,
                                    ValidationsUtils validations) {
        this.funcionarioService = funcionarioService;
        this.veiculoService = veiculoService;
        this.estoqueService = estoqueService;
        this.reservaService = reservaService;
        this.clienteService = clienteService;
        this.filialService = filialService;
        this.grupoVeiculoService = grupoVeiculoService;
        this.manutencaoService = manutencaoService;
        this.validations = validations;
    }

    // --- Login Funcionario ---
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        if (!model.containsAttribute("funcionario")){
            model.addAttribute("funcionario", new Funcionario());
        }
        return "funcionario/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, @RequestParam String senha, Model model, HttpSession session, RedirectAttributes redirectAtributes) {
        Optional<Funcionario> funcionario = funcionarioService.login(email, senha);

        if (funcionario.isPresent()) {
            session.setAttribute("loggedInFuncionario", funcionario.get());
            return "redirect:/funcionario/dashboard";
        } else {
            redirectAtributes.addFlashAttribute("erro", "E-mail ou senha inválidos.");
            return "redirect:/funcionario/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loggedInFuncionario");
        return "redirect:/funcionario/login";
    }

    // --- Funcionalidades do Dashboard ---
    @GetMapping("/dashboard")
    public String funcionarioDashboard(Model model) {
        model.addAttribute("activeContent", "home");
        return "funcionario/funcionario-dashboard";
    }

    // Cadastrar Funcionário
    @GetMapping("/cadastrar-funcionario")
    public String showRegisterFuncionarioForm(Model model) {
        model.addAttribute("activeContent", "register_funcionario");
        model.addAttribute("funcionario", new Funcionario());
        model.addAttribute("filiais", filialService.getAllFiliais());
        return "funcionario/funcionario-dashboard";
    }

    @PostMapping("/cadastrar-funcionario")
    public String registerFuncionario(@ModelAttribute Funcionario funcionario, RedirectAttributes redirectAttributes) {
        try {
            if (!ValidationsUtils.isValidCPF(funcionario.getCpf())) {
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
        model.addAttribute("filiais", filialService.getAllFiliais());
        model.addAttribute("funcionarios", funcionarioService.getAllFuncionarios());
        return "funcionario/funcionario-dashboard";
    }

    @PostMapping("/estoque/adicionar-veiculo")
    public String addVeiculo(@ModelAttribute Veiculo veiculo,
                             @RequestParam Integer filialId,
                             @RequestParam Integer funcionarioId,
                             RedirectAttributes redirectAttributes) {
        try {
            veiculo.setStatus("Disponível");

            Veiculo savedVeiculo = veiculoService.createVeiculo(veiculo);

            Filial filial = filialService.getFilialById(filialId)
                    .orElseThrow(() -> new RuntimeException("Filial não encontrada"));
            Funcionario funcionario = funcionarioService.getFuncionarioById(funcionarioId)
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

            Estoque estoque = new Estoque(filial, savedVeiculo, funcionario, "Disponível");
            estoqueService.createEstoque(estoque);

            redirectAttributes.addFlashAttribute("successMessage", "Veículo adicionado com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao adicionar veículo: " + e.getMessage());
        }
        return "redirect:/funcionario/estoque/adicionar-veiculo";
    }

    // Gerenciar Estoque - Remover Veículo
    @GetMapping("/estoque/remover-veiculo")
    public String showRemoveVeiculoForm(Model model) {
        model.addAttribute("activeContent", "remove_veiculo");
        model.addAttribute("veiculos", veiculoService.getAllVeiculosAtivos());
        return "funcionario/funcionario-dashboard";
    }

    @PostMapping("/estoque/remover-veiculo")
    public String removeVeiculo(@RequestParam Integer veiculoId, RedirectAttributes redirectAttributes) {
        try {
            veiculoService.deleteVeiculo(veiculoId);
            redirectAttributes.addFlashAttribute("successMessage", "Veículo removido com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/funcionario/estoque/remover-veiculo";
    }

    // Gerenciar Estoque - Verificar Disponibilidade
    @GetMapping("/estoque/verificar-disponibilidade")
    public String showCheckAvailabilityForm(Model model) {
        model.addAttribute("activeContent", "check_availability");
        List<Estoque> veiculosEmEstoque = estoqueService.getAllEstoques();
        model.addAttribute("veiculosEmEstoque", veiculosEmEstoque);
        return "funcionario/funcionario-dashboard";
    }

    // Gerenciar Reservas - Criar Reserva
    @GetMapping("/reservas/criar-reserva")
    public String showCreateReservaForm(
        @RequestParam(value = "filialId", required = false) Integer filialId,
        Model model) {

        model.addAttribute("activeContent", "create_reserva");
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("funcionarios", funcionarioService.getAllFuncionarios());
        model.addAttribute("filiais", filialService.getAllFiliais());

        if (filialId != null) {
            List<Veiculo> veiculosDisponiveis = estoqueService.getVeiculosDisponiveisPorFilial(filialId);
            model.addAttribute("veiculos", veiculosDisponiveis);
            model.addAttribute("selectedFilialId", filialId);
        } else {
            model.addAttribute("veiculos", Collections.emptyList());
        }

        return "funcionario/funcionario-dashboard";
    }

    @PostMapping("/reservas/criar-reserva")
    public String createReserva(@ModelAttribute Reserva reserva,
                                @RequestParam Integer funcionarioId,
                                @RequestParam Integer filialId,
                                @RequestParam String clienteCpf,
                                @RequestParam Integer veiculoId,
                                RedirectAttributes redirectAttributes) {
        try {
            Funcionario funcionario = funcionarioService.getFuncionarioById(funcionarioId)
                    .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
            Filial filial = filialService.getFilialById(filialId)
                    .orElseThrow(() -> new RuntimeException("Filial não encontrada"));
            Veiculo veiculo = veiculoService.getVeiculoById(veiculoId)
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

            Cliente cliente = clienteService.findClienteByCpf(clienteCpf)
                    .orElseThrow(() -> new RuntimeException("Cliente não registrado com CPF: " + clienteCpf));

            // Atualizar status do veículo
            veiculo.setStatus("Em Reserva");
            veiculoService.updateVeiculo(veiculo.getIdVeiculo(), veiculo);

            // Atualizar situação do estoque para "Indisponível"
            Optional<Estoque> estoqueOptional = estoqueService.getEstoqueByVeiculoId(veiculoId);
            if (estoqueOptional.isPresent()) {
                Estoque estoque = estoqueOptional.get();
                estoque.setSituacao("Indisponível");
                estoqueService.updateEstoque(estoque.getIdEstoque(), estoque);
            } else {
                throw new RuntimeException("Estoque do veículo não encontrado");
            }

            // Preencher a reserva
            reserva.setFuncionario(funcionario);
            reserva.setFilial(filial);
            reserva.setCliente(cliente);
            reserva.setVeiculo(veiculo);
            reserva.setStatus("Ativa");

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
        model.addAttribute("reservas", reservaService.getAllReservasAtivas());
        return "funcionario/funcionario-dashboard";
    }

    @PostMapping("/reservas/cancelar-reserva")
    public String cancelReserva(@RequestParam Integer reservaId, RedirectAttributes redirectAttributes) {
        try {
            Reserva reserva = reservaService.getReservaById(reservaId)
                    .orElseThrow(() -> new RuntimeException("Reserva não encontrada com o ID: " + reservaId));

            // Cancelar a reserva
            reserva.setStatus("Cancelada");
            reservaService.updateReserva(reservaId, reserva);

            // Liberar o veículo
            Veiculo veiculo = veiculoService.getVeiculoById(reserva.getVeiculo().getIdVeiculo())
                    .orElseThrow(() -> new RuntimeException("Veículo da reserva não encontrado."));
            veiculo.setStatus("Disponível");
            veiculoService.updateVeiculo(veiculo.getIdVeiculo(), veiculo);

            // Liberar o estoque
            Estoque estoque = estoqueService.getEstoqueByVeiculoId(veiculo.getIdVeiculo())
                    .orElseThrow(() -> new RuntimeException("Estoque do veículo não encontrado."));
            estoque.setSituacao("Disponível");
            estoqueService.updateEstoque(estoque.getIdEstoque(), estoque);

            redirectAttributes.addFlashAttribute("successMessage", "Reserva cancelada e veículo disponível novamente!");

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
        return "funcionario/funcionario-dashboard";
    }

    // --- Gerenciar Manutenção ---

    // Formulário para Cadastrar Manutenção
    @GetMapping("/manutencao/cadastrar")
    public String showCadastrarManutencaoForm(Model model) {
        model.addAttribute("activeContent", "cadastrar_manutencao");
        model.addAttribute("manutencao", new Manutencao());
        model.addAttribute("veiculos", veiculoService.getAllVeiculos());
        model.addAttribute("funcionarios", funcionarioService.getAllFuncionarios());
        return "funcionario/funcionario-dashboard";
    }

    @PostMapping("/manutencao/cadastrar")
    public String cadastrarManutencao(@ModelAttribute Manutencao manutencao,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro de validação: " + bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/funcionario/manutencao/cadastrar";
        }

        try {
            if (manutencao.getVeiculo() == null || manutencao.getVeiculo().getIdVeiculo() == null) {
                throw new RuntimeException("Veículo não selecionado.");
            }
            if (manutencao.getFuncionario() == null || manutencao.getFuncionario().getIdFuncionario() == null) {
                throw new RuntimeException("Funcionário não selecionado.");
            }

            if (manutencao.getDataFim() == null) {
                manutencao.setDataFim(null);
            }
            if (manutencao.getDataInicio() == null) {
                manutencao.setDataInicio(LocalDateTime.now());
            }

            manutencao.setStatus("Em Curso");

            // Buscar o veículo original
            Veiculo veiculo = veiculoService.getVeiculoById(manutencao.getVeiculo().getIdVeiculo())
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado."));

            // Alterar o status do veículo
            veiculo.setStatus("Em Manutenção");
            veiculoService.updateVeiculo(veiculo.getIdVeiculo(), veiculo);

            // Buscar o estoque pelo id do veículo
            Estoque estoque = estoqueService.getEstoqueByVeiculoId(veiculo.getIdVeiculo())
                    .orElseThrow(() -> new RuntimeException("Estoque do veículo não encontrado."));

            // Alterar a situação do estoque
            estoque.setSituacao("Em Manutenção");
            estoqueService.updateEstoque(estoque.getIdEstoque(), estoque);

            // Criar a manutenção
            manutencaoService.createManutencao(manutencao);

            redirectAttributes.addFlashAttribute("successMessage", "Manutenção cadastrada com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cadastrar manutenção: " + e.getMessage());
        }

        return "redirect:/funcionario/manutencao/cadastrar";
    }



    @GetMapping("/manutencao/suspender")
    public String showSuspenderManutencaoForm(Model model) {
        model.addAttribute("activeContent", "suspender_manutencao");
        return "funcionario/funcionario-dashboard";
    }

    @PostMapping("/manutencao/suspender")
    public String suspenderManutencao(@RequestParam Integer manutencaoId, RedirectAttributes redirectAttributes) {
        try {
            Manutencao manutencao = manutencaoService.getManutencaoById(manutencaoId)
                    .orElseThrow(() -> new RuntimeException("Manutenção não encontrada com o ID: " + manutencaoId));

            // Suspender a manutenção
            manutencao.setStatus("Suspensa");
            manutencaoService.updateManutencao(manutencaoId, manutencao);

            // Liberar o veículo
            Veiculo veiculo = veiculoService.getVeiculoById(manutencao.getVeiculo().getIdVeiculo())
                    .orElseThrow(() -> new RuntimeException("Veículo da manutenção não encontrado."));
            veiculo.setStatus("Disponível");
            veiculoService.updateVeiculo(veiculo.getIdVeiculo(), veiculo);

            // Liberar o estoque
            Estoque estoque = estoqueService.getEstoqueByVeiculoId(veiculo.getIdVeiculo())
                    .orElseThrow(() -> new RuntimeException("Estoque do veículo não encontrado."));
            estoque.setSituacao("Disponível");
            estoqueService.updateEstoque(estoque.getIdEstoque(), estoque);

            redirectAttributes.addFlashAttribute("successMessage", "Manutenção suspensa e veículo disponível novamente!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao suspender manutenção: " + e.getMessage());
        }

        return "redirect:/funcionario/manutencao/suspender";
    }

    // Formulário para Listar e Gerenciar Manutenções (Atualizar/Excluir)
    @GetMapping("/manutencao/gerenciar")
    public String showGerenciarManutencaoForm(Model model) {
        model.addAttribute("activeContent", "gerenciar_manutencao");
        model.addAttribute("manutencoes", manutencaoService.getAllManutencoes());
        model.addAttribute("veiculos", veiculoService.getAllVeiculos());
        model.addAttribute("funcionarios", funcionarioService.getAllFuncionarios());
        return "funcionario/funcionario-dashboard";
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

    // --- Alterar Reserva ---

    // Formulário para Alterar Reserva (TRATA A BUSCA POR ID)
    
    @GetMapping("/reservas/alterar")
    public String showAlterarReservaForm(@RequestParam(value = "id", required = false) Integer id, Model model) {
        model.addAttribute("activeContent", "alterar_reserva");

        if (id != null) {
            Optional<Reserva> reservaOptional = reservaService.getReservaById(id);
            if (reservaOptional.isPresent()) {
                model.addAttribute("reserva", reservaOptional.get());
                model.addAttribute("activeContent", "alterar_reserva_detalhes");
            } else {
                model.addAttribute("errorMessage", "Reserva com ID '" + id + "' não encontrada.");
            }
        } else {
            // Caso id não seja fornecido, exibe o formulário de busca ou uma lista (ajuste conforme necessário)
            model.addAttribute("reservas", reservaService.getAllReservas()); // Opcional: lista todas as reservas
        }

        return "funcionario/funcionario-dashboard";
    }

    // Lógica para processar a Alteração da Reserva
    @PostMapping("/reservas/alterar/{id}")
    public String alterarReserva(@PathVariable Integer id,
                                @ModelAttribute Reserva reserva,
                                RedirectAttributes redirectAttributes) {
        try {
            Reserva reservaExistente = reservaService.getReservaById(id)
                    .orElseThrow(() -> new RuntimeException("Reserva não encontrada com id " + id));

            if (reserva.getDataInicio() == null || reserva.getDataFim() == null) {
                throw new RuntimeException("Datas de início e fim são obrigatórias.");
            }
            if (reserva.getDataInicio().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("A data de início não pode ser anterior ao momento atual.");
            }
            if (reserva.getDataFim().isBefore(reserva.getDataInicio().plusDays(1))) {
                throw new RuntimeException("A data de fim deve ser pelo menos 1 dia após a data de início.");
            }

            reserva.setFuncionario(reservaExistente.getFuncionario());
            reserva.setFilial(reservaExistente.getFilial());
            reserva.setCliente(reservaExistente.getCliente());
            reserva.setVeiculo(reservaExistente.getVeiculo());
            reserva.setStatus(reservaExistente.getStatus());

            reservaService.updateReserva(id, reserva);

            redirectAttributes.addFlashAttribute("successMessage", "Reserva alterada com sucesso! Valor alterado: R$" + String.format("%.2f", reserva.getValor()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao alterar reserva: " + e.getMessage());
        }

        return "redirect:/funcionario/reservas/alterar?id=" + id;
    }

    // Formulário para Transferir Veículo
    @GetMapping("/estoque/transferir-veiculo")
    public String showTransferirVeiculoForm(Model model) {
        model.addAttribute("activeContent", "transferir_veiculo");
        model.addAttribute("estoques", estoqueService.getAllEstoques());
        model.addAttribute("filiais", filialService.getAllFiliais());
        return "funcionario/funcionario-dashboard";
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
        return "redirect:/funcionario/estoque/verificar-disponibilidade";
    }

    // Cadastro de Filial
   @GetMapping("/cadastrar-filial")
    public String showRegisterFilialForm(Model model) {
        model.addAttribute("activeContent", "register_filial");
        model.addAttribute("filial", new Filial());
        return "funcionario/funcionario-dashboard";
    }

    @PostMapping("/cadastrar-filial")
    public String registerFilial(@ModelAttribute Filial filial, RedirectAttributes redirectAttributes) {
        try {
            filialService.createFilial(filial);
            redirectAttributes.addFlashAttribute("successMessage", "Filial cadastrada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao cadastrar filial: " + e.getMessage());
        }
        return "redirect:/funcionario/cadastrar-filial";
    }
}