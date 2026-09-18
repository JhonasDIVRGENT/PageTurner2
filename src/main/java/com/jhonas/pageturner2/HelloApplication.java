package com.jhonas.pageturner2;

import com.jhonas.pageturner2.repository.ClienteRepositoryJson;
import com.jhonas.pageturner2.repository.LibroRepositoryJson;
import com.jhonas.pageturner2.repository.ReservaRepositoryJson;
import com.jhonas.pageturner2.repository.VentaRepositoryJson;
import com.jhonas.pageturner2.service.ClienteService;
import com.jhonas.pageturner2.service.LibroService;
import com.jhonas.pageturner2.service.ReservaService;
import com.jhonas.pageturner2.service.VentaService;
import com.jhonas.pageturner2.ui.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // Un solo repositorio de libros compartido: ventas y reservas
        // necesitan tocar el mismo stock que la pantalla de libros.
        LibroRepositoryJson libroRepo = new LibroRepositoryJson();

        LibroService libroService = new LibroService(libroRepo);
        ClienteService clienteService = new ClienteService(new ClienteRepositoryJson());
        VentaService ventaService = new VentaService(new VentaRepositoryJson(), libroRepo);
        ReservaService reservaService = new ReservaService(new ReservaRepositoryJson(), libroRepo);

        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("view/main-view.fxml"));
        loader.setControllerFactory(param -> new MainController(
                libroService, clienteService, ventaService, reservaService));

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
                HelloApplication.class.getResource("css/styles.css").toExternalForm());

        stage.setTitle("PageTurner 2.0");
        stage.setMinWidth(1000);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }
}