package com.jhonas.pageturner2.repository;

import com.jhonas.pageturner2.model.Libro;
import java.util.List;
import java.util.Optional;

public interface LibroRepository {
    void guardar(Libro libro);
    List<Libro> listarTodos();
    Optional<Libro> buscarPorIsbn(String isbn);
    void eliminar(String isbn);
}