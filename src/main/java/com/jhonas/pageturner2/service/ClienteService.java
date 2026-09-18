package com.jhonas.pageturner2.service;

import com.jhonas.pageturner2.model.Cliente;
import com.jhonas.pageturner2.repository.ClienteRepository;

import java.util.List;
import java.util.Optional;

public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public void registrar(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (cliente.getDni() == null || !cliente.getDni().matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos");
        }
        if (cliente.getCorreo() == null || !cliente.getCorreo().contains("@")) {
            throw new IllegalArgumentException("El correo no es válido");
        }
        clienteRepository.guardar(cliente);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.listarTodos();
    }

    public Optional<Cliente> buscarPorDni(String dni) {
        return clienteRepository.buscarPorDni(dni);
    }

    public void eliminar(String dni) {
        clienteRepository.eliminar(dni);
    }
}