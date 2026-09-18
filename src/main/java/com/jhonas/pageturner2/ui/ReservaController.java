package com.jhonas.pageturner2.ui;

import com.jhonas.pageturner2.model.Cliente;
import com.jhonas.pageturner2.model.Libro;
import com.jhonas.pageturner2.model.Reserva;
import com.jhonas.pageturner2.service.ClienteService;
import com.jhonas.pageturner2.service.LibroService;
import com.jhonas.pageturner2.service.ReservaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.util.List;

public class ReservaController {

    @FXML private TableView<Reserva> tablaReservas;
    @FXML private TableColumn<Reserva, String> colFecha;
    @FXML private TableColumn<Reserva, String> colLibro;
    @FXML private TableColumn<Reserva, String> colCliente;
    @FXML private TableColumn<Reserva, String> colDni;

    @FXML private ComboBox<Libro> comboLibro;
    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private Label mensaje;

    private final ReservaService reservaService;
    private final LibroService libroService;
    private final ClienteService clienteService;

    public ReservaController(ReservaService reservaService,
                             LibroService libroService,
                             ClienteService clienteService) {
        this.reservaService = reservaService;
        this.libroService = libroService;
        this.clienteService = clienteService;
    }

    @FXML
    public void initialize() {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        colLibro.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getLibro().getTitulo()));
        colCliente.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getCliente().getNombre()));
        colDni.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getCliente().getDni()));

        configurarCombos();
        refrescar();
    }

    @FXML
    public void registrarReserva() {
        try {
            Libro libro = comboLibro.getValue();
            Cliente cliente = comboCliente.getValue();

            if (libro == null) {
                mostrar("Selecciona un libro", true);
                return;
            }
            if (cliente == null) {
                mostrar("Selecciona un cliente", true);
                return;
            }

            reservaService.reservar(libro.getIsbn(), cliente);

            refrescar();
            mostrar("Reserva registrada para " + cliente.getNombre(), false);

        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrar(e.getMessage(), true);
        }
    }

    private void configurarCombos() {
        comboLibro.setConverter(new StringConverter<Libro>() {
            @Override public String toString(Libro libro) {
                return libro == null ? "" : libro.getTitulo() + "  -  " + libro.getAutor();
            }
            @Override public Libro fromString(String s) { return null; }
        });

        comboCliente.setConverter(new StringConverter<Cliente>() {
            @Override public String toString(Cliente cliente) {
                return cliente == null ? "" : cliente.getNombre() + "  -  " + cliente.getDni();
            }
            @Override public Cliente fromString(String s) { return null; }
        });
    }

    private void refrescar() {
        List<Libro> agotados = libroService.listarTodos().stream()
                .filter(libro -> libro.getStock() == 0)
                .toList();

        comboLibro.setItems(FXCollections.observableArrayList(agotados));
        comboCliente.setItems(FXCollections.observableArrayList(clienteService.listarTodos()));
        tablaReservas.setItems(FXCollections.observableArrayList(reservaService.listarTodos()));
    }

    private void mostrar(String texto, boolean esError) {
        mensaje.setText(texto);
        mensaje.getStyleClass().removeAll("mensaje-ok", "mensaje-error");
        mensaje.getStyleClass().add(esError ? "mensaje-error" : "mensaje-ok");
    }
}