package com.jhonas.pageturner2.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jhonas.pageturner2.model.Venta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VentaRepositoryJson implements VentaRepository {

    private final File archivo = new File(
            System.getProperty("user.home") + "/PageTurner2/ventas.json");
    private final ObjectMapper mapper;

    public VentaRepositoryJson() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public List<Venta> listarTodos() {
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(archivo, new TypeReference<List<Venta>>() {});
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void guardar(Venta venta) {
        List<Venta> ventas = listarTodos();
        ventas.add(venta);
        escribir(ventas);
    }

    private void escribir(List<Venta> ventas) {
        try {
            archivo.getParentFile().mkdirs();
            mapper.writeValue(archivo, ventas);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar en " + archivo.getPath(), e);
        }
    }
}