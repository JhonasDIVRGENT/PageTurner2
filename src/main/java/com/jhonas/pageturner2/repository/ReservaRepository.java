package com.jhonas.pageturner2.repository;

import com.jhonas.pageturner2.model.Reserva;

import java.util.List;


public interface ReservaRepository {
    void guardar (Reserva reserva);
    List<Reserva> listarTodos ();
}
