package com.jhonas.pageturner2.service;

import com.jhonas.pageturner2.model.Libro;
import com.jhonas.pageturner2.repository.LibroRepository;

import java.util.List;
import java.util.Optional;

public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public void registrar(Libro libro) {
        if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        if (libro.getAutor() == null || libro.getAutor().isBlank()) {
            throw new IllegalArgumentException("El autor es obligatorio");
        }
        if (libro.getIsbn() == null || libro.getIsbn().isBlank()) {
            throw new IllegalArgumentException("El ISBN es obligatorio");
        }
        if (libro.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        if (libro.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        libroRepository.guardar(libro);
    }

    public List<Libro> listarTodos() {
        return libroRepository.listarTodos();
    }

    public Optional<Libro> buscarPorIsbn(String isbn) {
        return libroRepository.buscarPorIsbn(isbn);
    }

    public void reponerStock(String isbn, int cantidad) {
        Libro libro = libroRepository.buscarPorIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un libro con ISBN " + isbn));
        libro.aumentarStock(cantidad);
        libroRepository.guardar(libro);
    }

    public void eliminar(String isbn) {
        libroRepository.eliminar(isbn);
    }
}