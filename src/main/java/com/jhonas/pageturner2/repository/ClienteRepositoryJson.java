package com.jhonas.pageturner2.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jhonas.pageturner2.model.Cliente;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepositoryJson implements ClienteRepository {

    private final File archivo = new File(
            System.getProperty("user.home") + "/PageTurner2/clientes.json");
    private final ObjectMapper mapper;

    public ClienteRepositoryJson() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public List<Cliente> listarTodos() {
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(archivo, new TypeReference<List<Cliente>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void guardar(Cliente cliente) {
        List<Cliente> clientes = listarTodos();
        clientes.removeIf(c -> c.getDni().equals(cliente.getDni()));
        clientes.add(cliente);
        escribir(clientes);
    }

    @Override
    public Optional<Cliente> buscarPorDni(String dni) {
        return listarTodos().stream()
                .filter(c -> c.getDni().equals(dni))
                .findFirst();
    }

    @Override
    public void eliminar(String dni) {
        List<Cliente> clientes = listarTodos();
        clientes.removeIf(c -> c.getDni().equals(dni));
        escribir(clientes);
    }

    private void escribir(List<Cliente> clientes) {
        try {
            archivo.getParentFile().mkdirs();
            mapper.writeValue(archivo, clientes);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar en " + archivo.getPath(), e);
        }
    }
}