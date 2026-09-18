package com.jhonas.pageturner2.service;

import com.jhonas.pageturner2.model.Cliente;
import com.jhonas.pageturner2.model.Libro;
import com.jhonas.pageturner2.model.Venta;
import com.jhonas.pageturner2.repository.LibroRepository;
import com.jhonas.pageturner2.repository.VentaRepository;



import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



public class VentaService {

    private final VentaRepository ventaRepository;
    private final LibroRepository libroRepository;

    public VentaService(VentaRepository ventaRepository, LibroRepository libroRepository) {
        this.ventaRepository = ventaRepository;
        this.libroRepository = libroRepository;
    }

    public Venta vender(String isbn, Cliente cliente, int cantidad) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        Libro libro = libroRepository.buscarPorIsbn(isbn)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un libro con ISBN " + isbn));

        libro.descontarStock(cantidad);
        libroRepository.guardar(libro);

        Venta venta = new Venta(LocalDate.now(), cantidad, cliente, libro);
        ventaRepository.guardar(venta);

        return venta;
    }

    public List<Venta> listarTodos() {
        return ventaRepository.listarTodos();
    }

    public double ingresosTotales() {
        double total = 0;
        for (Venta v : ventaRepository.listarTodos()) {
            total += v.calcularTotal();
        }
        return total;
    }

    public int unidadesVendidasDe(String isbn) {
        int unidades = 0;
        for (Venta v : ventaRepository.listarTodos()) {
            if (v.getLibro().getIsbn().equals(isbn)) {
                unidades += v.getCantidad();
            }
        }
        return unidades;
    }
    public Map<String, Integer> unidadesPorLibro() {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        for (Venta v : ventaRepository.listarTodos()) {
            String titulo = v.getLibro().getTitulo();
            conteo.put(titulo, conteo.getOrDefault(titulo, 0) + v.getCantidad());
        }
        return conteo;
    }
}