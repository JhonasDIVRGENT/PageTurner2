package com.jhonas.pageturner2.repository;

import com.jhonas.pageturner2.model.Cliente;

import java.util.List;
import java.util.Optional;


public interface ClienteRepository {
    void guardar (Cliente cliente);
    List<Cliente> listarTodos ();
    Optional<Cliente> buscarPorDni (String dni);
    void eliminar  (String dni);


}
