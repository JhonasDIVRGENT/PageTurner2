package com.jhonas.pageturner2.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jhonas.pageturner2.model.Reserva;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReservaRepositoryJson implements ReservaRepository {

    private final File archivo = new File(
            System.getProperty("user.home") + "/PageTurner2/reservas.json");
    private final ObjectMapper mapper;

    public ReservaRepositoryJson() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public List<Reserva> listarTodos() {
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(archivo, new TypeReference<List<Reserva>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void guardar(Reserva reserva) {
        List<Reserva> reservas = listarTodos();
        reservas.add(reserva);
        escribir(reservas);
    }

    private void escribir(List<Reserva> reservas) {
        try {
            archivo.getParentFile().mkdirs();
            mapper.writeValue(archivo, reservas);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar en " + archivo.getPath(), e);
        }
    }
}