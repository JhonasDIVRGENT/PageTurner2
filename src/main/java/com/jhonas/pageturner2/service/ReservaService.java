package com.jhonas.pageturner2.service;

import com.jhonas.pageturner2.model.Cliente;
import com.jhonas.pageturner2.model.Libro;
import com.jhonas.pageturner2.model.Reserva;
import com.jhonas.pageturner2.repository.LibroRepository;
import com.jhonas.pageturner2.repository.ReservaRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final LibroRepository libroRepository;

    public ReservaService(ReservaRepository reservaRepository, LibroRepository libroRepository) {
        this.reservaRepository = reservaRepository;
        this.libroRepository = libroRepository;
    }

    public Reserva reservar(String isbn, Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }

        Libro libro = libroRepository.buscarPorIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un libro con ISBN " + isbn));

        if (libro.getStock() > 0) {
            throw new IllegalStateException(
                    "No es necesario reservar: hay " + libro.getStock() + " en stock");
        }

        Reserva reserva = new Reserva(LocalDate.now(), cliente, libro);
        reservaRepository.guardar(reserva);
        return reserva;
    }

    public List<Reserva> listarTodos() {
        return reservaRepository.listarTodos();
    }

    public List<Reserva> reservasDe(String isbn) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservaRepository.listarTodos()) {
            if (r.getLibro().getIsbn().equals(isbn)) {
                resultado.add(r);
            }
        }
        return resultado;
    }
}