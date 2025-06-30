package com.vocealuga.service;

import com.vocealuga.model.Reserva;
import com.vocealuga.dao.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<Reserva> getAllReservas() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> getReservaById(Integer id) {
        return reservaRepository.findById(id);
    }

    public Reserva createReserva(Reserva reserva) {
        if (reserva.getDataInicio().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("A data de início não pode ser anterior ao momento atual.");
        }
        if (reserva.getDataFim().isBefore(reserva.getDataInicio().plusDays(1))) {
            throw new RuntimeException("A data de fim deve ser pelo menos 1 dia após a data de início.");
        }
        return reservaRepository.save(reserva);
    }

    public Reserva updateReserva(Integer id, Reserva reservaDetails) {
        return reservaRepository.findById(id)
                .map(reserva -> {
                    reserva.setFuncionario(reservaDetails.getFuncionario());
                    reserva.setFilial(reservaDetails.getFilial());
                    reserva.setCliente(reservaDetails.getCliente());
                    reserva.setVeiculo(reservaDetails.getVeiculo());
                    reserva.setDataInicio(reservaDetails.getDataInicio());
                    reserva.setDataFim(reservaDetails.getDataFim());
                    reserva.setValor(reservaDetails.getValor());
                    reserva.setStatus(reservaDetails.getStatus());
                    return reservaRepository.save(reserva);
                }).orElseThrow(() -> new RuntimeException("Reserva not found with id " + id));
    }

    public void deleteReserva(Integer id) {
        reservaRepository.deleteById(id);
    }

    // NOVO MÉTODO: Busca todas as reservas associadas a um cliente específico.
    public List<Reserva> getReservasByCliente(Integer idCliente) {
        return reservaRepository.findByCliente_IdCliente(idCliente);
    }
}