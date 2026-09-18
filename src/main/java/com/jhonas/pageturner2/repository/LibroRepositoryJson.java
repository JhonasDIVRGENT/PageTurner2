package com.jhonas.pageturner2.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jhonas.pageturner2.model.Libro;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroRepositoryJson implements LibroRepository {

    private final File archivo = new File(
            System.getProperty("user.home") + "/PageTurner2/libros.json");
    private final ObjectMapper mapper;

    public LibroRepositoryJson() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public List<Libro> listarTodos() {
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(archivo, new TypeReference<List<Libro>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void guardar(Libro libro) {
        List<Libro> libros = listarTodos();
        libros.removeIf(l -> l.getIsbn().equals(libro.getIsbn()));
        libros.add(libro);
        escribir(libros);
    }

    @Override
    public Optional<Libro> buscarPorIsbn(String isbn) {
        return listarTodos().stream()
                .filter(l -> l.getIsbn().equals(isbn))
                .findFirst();
    }

    @Override
    public void eliminar(String isbn) {
        List<Libro> libros = listarTodos();
        libros.removeIf(l -> l.getIsbn().equals(isbn));
        escribir(libros);
    }

    private void escribir(List<Libro> libros) {
        try {
            archivo.getParentFile().mkdirs();
            mapper.writeValue(archivo, libros);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar en " + archivo.getPath(), e);
        }
    }
}