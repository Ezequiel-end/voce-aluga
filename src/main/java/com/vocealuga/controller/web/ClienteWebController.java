// src/main/java/com/vocealuga/controller/web/ClienteWebController.java
package com.vocealuga.controller.web;

import com.vocealuga.model.Cliente;
import com.vocealuga.model.Reserva;
import com.vocealuga.model.Pagamento;


import com.vocealuga.service.ClienteService;
import com.vocealuga.service.ReservaService;
import com.vocealuga.service.VeiculoService;
import com.vocealuga.service.FilialService;
import com.vocealuga.service.GrupoVeiculoService;
import com.vocealuga.service.PagamentoService;
import com.vocealuga.service.FormaPagamentoService;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cliente")
public class ClienteWebController {

    private final ClienteService clienteService;
    private final ReservaService reservaService;
    private final VeiculoService veiculoService;
    private final FilialService filialService;
    private final GrupoVeiculoService grupoVeiculoService;
    private final PagamentoService pagamentoService;
    private final FormaPagamentoService formaPagamentoService;
    // O FuncionarioService não é estritamente necessário aqui, a menos que você precise
    // autenticar funcionários ou redirecionar para o dashboard de funcionário.
    // Mantenha apenas se for realmente utilizado nesta classe.
    // private final FuncionarioService funcionarioService;


    @Autowired
    public ClienteWebController(ClienteService clienteService, ReservaService reservaService,
                                VeiculoService veiculoService, FilialService filialService,
                                GrupoVeiculoService grupoVeiculoService, PagamentoService pagamentoService,
                                FormaPagamentoService formaPagamentoService /*, FuncionarioService funcionarioService */) {
        this.clienteService = clienteService;
        this.reservaService = reservaService;
        this.veiculoService = veiculoService;
        this.filialService = filialService;
        this.grupoVeiculoService = grupoVeiculoService;
        this.pagamentoService = pagamentoService;
        this.formaPagamentoService = formaPagamentoService;
        // this.funcionarioService = funcionarioService;
    }

    // --- Dashboard ---
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Cliente loggedInClient = (Cliente) session.getAttribute("loggedInClient");
        if (loggedInClient == null) {
            redirectAttributes.addFlashAttribute("error", "Você precisa estar logado para acessar o dashboard.");
            return "redirect:/login"; // Redireciona para o login genérico
        }
        // Recarrega o cliente do banco de dados para garantir que os dados, incluindo pontos, estejam atualizados
        Optional<Cliente> updatedClient = clienteService.getClienteById(loggedInClient.getIdCliente());
        if (updatedClient.isPresent()) {
            model.addAttribute("cliente", updatedClient.get());
            session.setAttribute("loggedInClient", updatedClient.get()); // Atualiza a sessão
        } else {
            redirectAttributes.addFlashAttribute("error", "Erro ao carregar dados do cliente.");
            return "redirect:/logout"; // Força logout se o cliente não for encontrado
        }
        return "cliente/dashboard"; // CORRIGIDO
    }

    // --- Perfil ---
    @GetMapping("/perfil/editar")
    public String showEditProfileForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Cliente loggedInClient = (Cliente) session.getAttribute("loggedInClient");
        if (loggedInClient == null) {
            redirectAttributes.addFlashAttribute("error", "Você precisa estar logado para editar o perfil.");
            return "redirect:/login"; // Redireciona para o login genérico
        }
        model.addAttribute("cliente", loggedInClient);
        return "cliente/perfil-editar"; // CORRIGIDO
    }

    @PostMapping("/perfil/editar")
    public String updateProfile(@ModelAttribute Cliente clienteDetails, HttpSession session, RedirectAttributes redirectAttributes) {
        Cliente loggedInClient = (Cliente) session.getAttribute("loggedInClient");
        if (loggedInClient == null) {
            redirectAttributes.addFlashAttribute("error", "Você precisa estar logado para editar o perfil.");
            return "redirect:/login"; // Redireciona para o login genérico
        }
        try {
            // O updateCliente no serviço já lida com a lógica de não alterar a senha se vazia
            Cliente updatedClient = clienteService.updateCliente(loggedInClient.getIdCliente(), clienteDetails);
            session.setAttribute("loggedInClient", updatedClient); // Atualiza o cliente na sessão
            redirectAttributes.addFlashAttribute("success", "Perfil atualizado com sucesso!");
            return "redirect:/cliente/dashboard";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao atualizar perfil: " + e.getMessage());
            return "redirect:/cliente/perfil/editar";
        }
    }

    // --- Reservas ---
    @GetMapping("/reservas")
    public String listReservas(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Cliente loggedInClient = (Cliente) session.getAttribute("loggedInClient");
        if (loggedInClient == null) {
            redirectAttributes.addFlashAttribute("error", "Você precisa estar logado para ver suas reservas.");
            return "redirect:/login"; // Redireciona para o login genérico
        }
        List<Reserva> reservas = reservaService.getReservasByCliente(loggedInClient.getIdCliente());
        model.addAttribute("reservas", reservas);
        return "cliente/reservas"; // CORRIGIDO
    }

    @GetMapping("/reservas/nova")
    public String showNewReservaForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Cliente loggedInClient = (Cliente) session.getAttribute("loggedInClient");
        if (loggedInClient == null) {
            redirectAttributes.addFlashAttribute("error", "Você precisa estar logado para fazer uma reserva.");
            return "redirect:/login"; // Redireciona para o login genérico
        }

        model.addAttribute("reserva", new Reserva());
        model.addAttribute("veiculos", veiculoService.getAllVeiculos());
        model.addAttribute("filiais", filialService.getAllFiliais());
        model.addAttribute("gruposVeiculo", grupoVeiculoService.getAllGruposVeiculo());
        return "cliente/reserva-form"; // CORRIGIDO
    }

    @PostMapping("/reservas/nova")
    public String createReserva(@ModelAttribute Reserva reserva, HttpSession session, RedirectAttributes redirectAttributes) {
        Cliente loggedInClient = (Cliente) session.getAttribute("loggedInClient");
        if (loggedInClient == null) {
            redirectAttributes.addFlashAttribute("error", "Você precisa estar logado para fazer uma reserva.");
            return "redirect:/login"; // Redireciona para o login genérico
        }

        try {
            reserva.setCliente(loggedInClient); // Associa o cliente logado à reserva
            reserva.setFuncionario(null); // Pode ser definido por um admin posteriormente ou via lógica de negócio
            reserva.setStatus("PENDENTE"); // Status inicial

            // Carregar entidades relacionadas para evitar transient errors
            if (reserva.getFilial() != null && reserva.getFilial().getIdFilial() != null) {
                reserva.setFilial(filialService.getFilialById(reserva.getFilial().getIdFilial())
                    .orElseThrow(() -> new RuntimeException("Filial não encontrada.")));
            } else {
                throw new IllegalArgumentException("Filial é obrigatória para a reserva.");
            }
            if (reserva.getVeiculo() != null && reserva.getVeiculo().getIdVeiculo() != null) {
                reserva.setVeiculo(veiculoService.getVeiculoById(reserva.getVeiculo().getIdVeiculo())
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado.")));
            } else {
                throw new IllegalArgumentException("Veículo é obrigatório para a reserva.");
            }

            reservaService.createReserva(reserva);
            redirectAttributes.addFlashAttribute("success", "Reserva criada com sucesso! Por favor, realize o pagamento.");
            return "redirect:/cliente/reservas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao criar reserva: " + e.getMessage());
            return "redirect:/cliente/reservas/nova";
        }
    }

    // --- Pagamento de Reserva ---
    @GetMapping("/reservas/{reservaId}/pagar")
    public String showPagamentoForm(@PathVariable Integer reservaId, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Cliente loggedInClient = (Cliente) session.getAttribute("loggedInClient");
        if (loggedInClient == null) {
            redirectAttributes.addFlashAttribute("error", "Você precisa estar logado para acessar esta página.");
            return "redirect:/login"; // Redireciona para o login genérico
        }

        Optional<Reserva> reservaOptional = reservaService.getReservaById(reservaId);
        if (!reservaOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Reserva não encontrada.");
            return "redirect:/cliente/reservas";
        }

        Reserva reserva = reservaOptional.get();

        // Verifica se a reserva pertence ao cliente logado
        if (!reserva.getCliente().getIdCliente().equals(loggedInClient.getIdCliente())) {
            redirectAttributes.addFlashAttribute("error", "Você não tem permissão para acessar esta reserva.");
            return "redirect:/cliente/reservas";
        }

        // Evita múltiplos pagamentos
        if (!"PENDENTE".equalsIgnoreCase(reserva.getStatus()) && !"CONFIRMADA".equalsIgnoreCase(reserva.getStatus())) {
            redirectAttributes.addFlashAttribute("error", "Esta reserva não está em um status que permite pagamento (Status atual: " + reserva.getStatus() + ").");
            return "redirect:/cliente/reservas";
        }

        model.addAttribute("reserva", reserva);
        model.addAttribute("pagamento", new Pagamento());
        model.addAttribute("formasPagamento", formaPagamentoService.getAllFormasPagamento());
        return "cliente/pagamento-form"; // CORRIGIDO
    }

    @PostMapping("/reservas/{reservaId}/pagar")
    public String processarPagamento(@PathVariable Integer reservaId, @ModelAttribute Pagamento pagamento,
                                     HttpSession session, RedirectAttributes redirectAttributes) {
        Cliente loggedInClient = (Cliente) session.getAttribute("loggedInClient");
        if (loggedInClient == null) {
            redirectAttributes.addFlashAttribute("error", "Você precisa estar logado para processar o pagamento.");
            return "redirect:/login"; // Redireciona para o login genérico
        }

        try {
            Optional<Reserva> reservaOptional = reservaService.getReservaById(reservaId);
            if (!reservaOptional.isPresent()) {
                throw new RuntimeException("Reserva não encontrada.");
            }
            Reserva reserva = reservaOptional.get();

            if (!"PENDENTE".equalsIgnoreCase(reserva.getStatus()) && !"CONFIRMADA".equalsIgnoreCase(reserva.getStatus())) {
                redirectAttributes.addFlashAttribute("error", "Esta reserva já foi paga ou está em um status inválido.");
                return "redirect:/cliente/reservas";
            }

            // Associa a reserva ao pagamento
            pagamento.setReserva(reserva);
            pagamento.setDataPagamento(LocalDate.now()); // Data do pagamento

            // Carrega a FormaPagamento completa
            if (pagamento.getFormaPagamento() != null && pagamento.getFormaPagamento().getIdFormaPagamento() != null) {
                pagamento.setFormaPagamento(formaPagamentoService.getFormaPagamentoById(pagamento.getFormaPagamento().getIdFormaPagamento())
                    .orElseThrow(() -> new RuntimeException("Forma de Pagamento não encontrada.")));
            } else {
                throw new IllegalArgumentException("Forma de Pagamento é obrigatória.");
            }

            // Salva o pagamento
            pagamentoService.createPagamento(pagamento);

            // Atualiza o status da reserva para "PAGA"
            reserva.setStatus("PAGA");
            reservaService.updateReserva(reserva.getIdReserva(), reserva);

            // --- Lógica de Fidelidade: Adicionar Pontos ao Cliente ---
            // Regra: 1 ponto para cada R$10,00 de valor da reserva
            if (reserva.getValor() != null && reserva.getValor() > 0) {
                Integer pontosParaAdicionar = (int) (reserva.getValor() / 10.0f);
                if (pontosParaAdicionar > 0) {
                    clienteService.addPontosFidelidade(reserva.getCliente().getIdCliente(), pontosParaAdicionar);
                    redirectAttributes.addFlashAttribute("success", "Pagamento realizado com sucesso para a reserva #" + reserva.getIdReserva() + "! Você ganhou " + pontosParaAdicionar + " pontos de fidelidade!");
                } else {
                    redirectAttributes.addFlashAttribute("success", "Pagamento realizado com sucesso para a reserva #" + reserva.getIdReserva() + ".");
                }
            } else {
                redirectAttributes.addFlashAttribute("success", "Pagamento realizado com sucesso para a reserva #" + reserva.getIdReserva() + ".");
            }
            // Fim da Lógica de Fidelidade

            // Atualiza o cliente na sessão para refletir os novos pontos
            Optional<Cliente> updatedClient = clienteService.getClienteById(loggedInClient.getIdCliente());
            updatedClient.ifPresent(client -> session.setAttribute("loggedInClient", client));


            return "redirect:/cliente/reservas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao processar pagamento: " + e.getMessage());
            return "redirect:/cliente/reservas/" + reservaId + "/pagar";
        }
    }

    // --- Cancelar Reserva ---
    @PostMapping("/reservas/cancelar/{id}")
    public String cancelarReserva(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        Cliente loggedInClient = (Cliente) session.getAttribute("loggedInClient");
        if (loggedInClient == null) {
            redirectAttributes.addFlashAttribute("error", "Você precisa estar logado para cancelar uma reserva.");
            return "redirect:/login"; // Redireciona para o login genérico
        }

        try {
            Optional<Reserva> reservaOptional = reservaService.getReservaById(id);
            if (!reservaOptional.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Reserva não encontrada.");
                return "redirect:/cliente/reservas";
            }

            Reserva reserva = reservaOptional.get();

            // Verifica se a reserva pertence ao cliente logado
            if (!reserva.getCliente().getIdCliente().equals(loggedInClient.getIdCliente())) {
                redirectAttributes.addFlashAttribute("error", "Você não tem permissão para cancelar esta reserva.");
                return "redirect:/cliente/reservas";
            }

            // Lógica para permitir cancelamento apenas se o status for PENDENTE ou CONFIRMADA
            if ("PENDENTE".equalsIgnoreCase(reserva.getStatus()) || "CONFIRMADA".equalsIgnoreCase(reserva.getStatus())) {
                reserva.setStatus("CANCELADA_CLIENTE");
                reservaService.updateReserva(reserva.getIdReserva(), reserva);
                redirectAttributes.addFlashAttribute("success", "Reserva #" + id + " cancelada com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Não é possível cancelar a reserva #" + id + ". Status atual: " + reserva.getStatus());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao cancelar reserva: " + e.getMessage());
        }
        return "redirect:/cliente/reservas";
    }

    // --- Logout ---
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("loggedInClient"); // Remove o atributo da sessão
        redirectAttributes.addFlashAttribute("success", "Você foi desconectado com sucesso.");
        return "redirect:/cliente/login"; // Redireciona para a página de login genérica
    }
}