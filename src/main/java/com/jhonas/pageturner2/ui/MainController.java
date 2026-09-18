package com.jhonas.pageturner2.ui;

import com.jhonas.pageturner2.service.ClienteService;
import com.jhonas.pageturner2.service.LibroService;
import com.jhonas.pageturner2.service.ReservaService;
import com.jhonas.pageturner2.service.VentaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML private StackPane contenido;

    private final LibroService libroService;
    private final ClienteService clienteService;
    private final VentaService ventaService;
    private final ReservaService reservaService;

    public MainController(LibroService libroService,
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
        irADashboard();    }

    @FXML
    public void irALibros() {
        cargar("libro-view.fxml", new LibroController(libroService));
    }

    @FXML
    public void irAClientes() {
        cargar("cliente-view.fxml", new ClienteController(clienteService));
    }

    @FXML
    public void irAVentas() {
        cargar("venta-view.fxml",
                new VentaController(ventaService, libroService, clienteService));
    }

    @FXML
    public void irAReservas() {
        cargar("reserva-view.fxml",
                new ReservaController(reservaService, libroService, clienteService));
    }

    @FXML
    public void irADashboard() {
        cargar("dashboard-view.fxml", new DashboardController(
                libroService, clienteService, ventaService, reservaService));
    }

    private void cargar(String archivo, Object controller) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/jhonas/pageturner2/view/" + archivo));
            loader.setControllerFactory(param -> controller);

            Parent vista = loader.load();
            contenido.getChildren().setAll(vista);

        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar " + archivo, e);
        }
    }
}