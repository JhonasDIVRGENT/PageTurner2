package com.jhonas.pageturner2.ui;

import com.jhonas.pageturner2.model.Cliente;
import com.jhonas.pageturner2.service.ClienteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class ClienteController {

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colDni;
    @FXML private TableColumn<Cliente, String> colCorreo;

    @FXML private TextField campoNombre;
    @FXML private TextField campoDni;
    @FXML private TextField campoCorreo;

    @FXML private Label mensaje;

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        refrescarTabla();
    }

    @FXML
    public void guardarCliente() {
        try {
            Cliente cliente = new Cliente(
                    campoNombre.getText(),
                    campoDni.getText(),
                    campoCorreo.getText());

            clienteService.registrar(cliente);

            limpiarCampos();
            refrescarTabla();
            mostrar("Cliente guardado correctamente", false);

        } catch (IllegalArgumentException e) {
            mostrar(e.getMessage(), true);
        }
    }

    @FXML
    public void eliminarCliente() {
        Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrar("Selecciona un cliente de la tabla", true);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Eliminar a " + seleccionado.getNombre() + "?");

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        clienteService.eliminar(seleccionado.getDni());
        refrescarTabla();
        mostrar("Cliente eliminado", false);
    }

    private void refrescarTabla() {
        ObservableList<Cliente> datos =
                FXCollections.observableArrayList(clienteService.listarTodos());
        tablaClientes.setItems(datos);
    }

    private void limpiarCampos() {
        campoNombre.clear();
        campoDni.clear();
        campoCorreo.clear();
    }

    private void mostrar(String texto, boolean esError) {
        mensaje.setText(texto);
        mensaje.getStyleClass().removeAll("mensaje-ok", "mensaje-error");
        mensaje.getStyleClass().add(esError ? "mensaje-error" : "mensaje-ok");
    }
}