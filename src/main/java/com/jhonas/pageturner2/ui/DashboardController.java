package com.jhonas.pageturner2.ui;

import com.jhonas.pageturner2.model.Libro;
import com.jhonas.pageturner2.service.ClienteService;
import com.jhonas.pageturner2.service.LibroService;
import com.jhonas.pageturner2.service.ReservaService;
import com.jhonas.pageturner2.service.VentaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DashboardController {

    @FXML private Label valorIngresos;
    @FXML private Label valorLibros;
    @FXML private Label valorClientes;
    @FXML private Label valorReservas;
    @FXML private ListView<String> listaMasVendidos;
    @FXML private ListView<String> listaSinStock;
    @FXML private Label resumenVentas;

    private final LibroService libroService;
    private final ClienteService clienteService;
    private final VentaService ventaService;
    private final ReservaService reservaService;

    public DashboardController(LibroService libroService,
                               ClienteService clienteService,
                               VentaService ventaService,
                               ReservaService reservaService) {
        this.libroService = libroService;
        this.clienteService = clienteService;
        this.ventaService = ventaService;
        this.reservaService = reservaService;
    }

    @FXML
    public void initialize() {
        valorIngresos.setText("S/ " + String.format("%.2f", ventaService.ingresosTotales()));
        valorLibros.setText(String.valueOf(libroService.listarTodos().size()));
        valorClientes.setText(String.valueOf(clienteService.listarTodos().size()));
        valorReservas.setText(String.valueOf(reservaService.listarTodos().size()));

        cargarMasVendidos();
        cargarSinStock();

        int totalVentas = ventaService.listarTodos().size();
        resumenVentas.setText(totalVentas == 0
                ? "Todavía no se ha registrado ninguna venta."
                : "Se han registrado " + totalVentas + " ventas en total.");
    }

    private void cargarMasVendidos() {
        Map<String, Integer> conteo = ventaService.unidadesPorLibro();

        List<String> filas = new ArrayList<>();
        conteo.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed())
                .limit(5)
                .forEach(e -> filas.add(e.getKey() + "   ·   " + e.getValue() + " uds."));

        if (filas.isEmpty()) {
            filas.add("Sin ventas todavía");
        }
        listaMasVendidos.setItems(FXCollections.observableArrayList(filas));
    }

    private void cargarSinStock() {
        List<String> filas = new ArrayList<>();
        for (Libro libro : libroService.listarTodos()) {
            if (libro.getStock() == 0) {
                filas.add(libro.getTitulo() + "   ·   " + libro.getAutor());
            }
        }
        if (filas.isEmpty()) {
            filas.add("Todo el catálogo tiene stock");
        }
        listaSinStock.setItems(FXCollections.observableArrayList(filas));
    }
}