package aerolinea.main;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.Vuelo;
import aerolinea.repositorio.IRepositorio;
import aerolinea.repositorio.RepositorioArchivo;
import aerolinea.servicio.Servicio;
import aerolinea.ui.Menu;
import aerolinea.ui.Ventana;

import javax.swing.SwingUtilities;
import java.util.Arrays;

/**
 * Punto de entrada del Sistema de AerolÃ­nea.
 *
 * <p>Por defecto inicia la interfaz grÃ¡fica Swing. Para conservar el menÃº
 * de consola desarrollado en las etapas anteriores puede ejecutarse:</p>
 *
 * <pre>
 * java -jar SistemaDeAerolinea-1.0-SNAPSHOT.jar --consola
 * </pre>
 */
public class Main {

    public static void main(String[] args) {

        IRepositorio<Vuelo> repositorioVuelos =
                new RepositorioArchivo<>("data/vuelos.dat");

        Servicio<Vuelo> servicioVuelos =
                new Servicio<>(repositorioVuelos);

        Aerolinea aerolinea =
                new Aerolinea("AerolÃ­nea IFES", servicioVuelos.listar());

        boolean modoConsola =
                Arrays.stream(args)
                        .anyMatch("--consola"::equalsIgnoreCase);

        if (modoConsola) {
            Menu menu = new Menu(aerolinea, servicioVuelos);
            menu.iniciar();
            return;
        }

        /*
         * Swing debe crear y modificar componentes grÃ¡ficos en el
         * Event Dispatch Thread (EDT).
         */
        SwingUtilities.invokeLater(() -> {
            Ventana ventana = new Ventana(aerolinea, servicioVuelos);
            ventana.setVisible(true);
        });
    }
}