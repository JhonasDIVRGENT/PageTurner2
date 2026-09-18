package com.jhonas.pageturner2.ui;

import com.jhonas.pageturner2.model.Cliente;
import com.jhonas.pageturner2.model.Libro;
import com.jhonas.pageturner2.model.Venta;
import com.jhonas.pageturner2.service.ClienteService;
import com.jhonas.pageturner2.service.LibroService;
import com.jhonas.pageturner2.service.VentaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
public class VentaController {

    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, String> colLibro;
    @FXML private TableColumn<Venta, String> colCliente;
    @FXML private TableColumn<Venta, Integer> colCantidad;
    @FXML private TableColumn<Venta, String> colTotal;

    @FXML private ComboBox<Libro> comboLibro;
    @FXML private ComboBox<Cliente> comboCliente;
    @FXML private TextField campoCantidad;
    @FXML private Label mensaje;

    private final VentaService ventaService;
    private final LibroService libroService;
    private final ClienteService clienteService;

    public VentaController(VentaService ventaService,
                           LibroService libroService,
                           ClienteService clienteService) {
        this.ventaService = ventaService;
        this.libroService = libroService;
        this.clienteService = clienteService;
    }

    @FXML
    public void initialize() {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        colLibro.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getLibro().getTitulo()));
        colCliente.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getCliente().getNombre()));
        colTotal.setCellValueFactory(dato ->
                new SimpleStringProperty(String.format("%.2f", dato.getValue().calcularTotal())));

        configurarCombos();
        refrescar();
    }

    @FXML
    public void registrarVenta() {
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

            int cantidad = Integer.parseInt(campoCantidad.getText());

            Venta venta = ventaService.vender(libro.getIsbn(), cliente, cantidad);

            campoCantidad.clear();
            refrescar();
            mostrar("Venta registrada. Total: S/ "
                    + String.format("%.2f", venta.calcularTotal()), false);

        } catch (NumberFormatException e) {
            mostrar("La cantidad debe ser un número", true);
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrar(e.getMessage(), true);
        }
    }

    private void configurarCombos() {
        comboLibro.setConverter(new StringConverter<Libro>() {
            @Override public String toString(Libro libro) {
                return libro == null ? "" : libro.getTitulo() + "  (stock: " + libro.getStock() + ")";
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
        comboLibro.setItems(FXCollections.observableArrayList(libroService.listarTodos()));
        comboCliente.setItems(FXCollections.observableArrayList(clienteService.listarTodos()));
        tablaVentas.setItems(FXCollections.observableArrayList(ventaService.listarTodos()));
    }

    private void mostrar(String texto, boolean esError) {
        mensaje.setText(texto);
        mensaje.getStyleClass().removeAll("mensaje-ok", "mensaje-error");
        mensaje.getStyleClass().add(esError ? "mensaje-error" : "mensaje-ok");
    }
}