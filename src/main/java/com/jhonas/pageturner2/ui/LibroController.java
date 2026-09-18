package com.jhonas.pageturner2.ui;

import com.jhonas.pageturner2.model.Libro;
import com.jhonas.pageturner2.service.LibroService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class LibroController {

    @FXML private TableView<Libro> tablaLibros;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colIsbn;
    @FXML private TableColumn<Libro, Double> colPrecio;
    @FXML private TableColumn<Libro, Integer> colStock;

    @FXML private TextField campoTitulo;
    @FXML private TextField campoAutor;
    @FXML private TextField campoIsbn;
    @FXML private TextField campoPrecio;
    @FXML private TextField campoStock;

    @FXML private Label mensaje;

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @FXML
    public void initialize() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        refrescarTabla();
    }

    @FXML
    public void guardarLibro() {
        try {
            String titulo = campoTitulo.getText();
            String autor = campoAutor.getText();
            String isbn = campoIsbn.getText();
            double precio = Double.parseDouble(campoPrecio.getText());
            int stock = Integer.parseInt(campoStock.getText());

            libroService.registrar(new Libro(titulo, autor, isbn, precio, stock));

            limpiarCampos();
            refrescarTabla();
            mostrar("Libro guardado correctamente", false);

        } catch (NumberFormatException e) {
            mostrar("Precio y stock deben ser números", true);
        } catch (IllegalArgumentException e) {
            mostrar(e.getMessage(), true);
        }
    }

    @FXML
    public void eliminarLibro() {
        Libro seleccionado = tablaLibros.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrar("Selecciona un libro de la tabla", true);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Eliminar \"" + seleccionado.getTitulo() + "\"?");

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        libroService.eliminar(seleccionado.getIsbn());
        refrescarTabla();
        mostrar("Libro eliminado", false);
    }

    private void refrescarTabla() {
        ObservableList<Libro> datos = FXCollections.observableArrayList(libroService.listarTodos());
        tablaLibros.setItems(datos);
    }

    private void limpiarCampos() {
        campoTitulo.clear();
        campoAutor.clear();
        campoIsbn.clear();
        campoPrecio.clear();
        campoStock.clear();
    }

    private void mostrar(String texto, boolean esError) {
        mensaje.setText(texto);
        mensaje.getStyleClass().removeAll("mensaje-ok", "mensaje-error");
        mensaje.getStyleClass().add(esError ? "mensaje-error" : "mensaje-ok");
    }
}