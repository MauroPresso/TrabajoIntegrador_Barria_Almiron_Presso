package aerolinea.main;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.Persona;
import aerolinea.dominio.Vuelo;
import aerolinea.repositorio.IRepositorio;
import aerolinea.repositorio.RepositorioArchivo;
import aerolinea.servicio.Servicio;
import aerolinea.ui.Menu;
import aerolinea.ui.Ventana;

import javax.swing.SwingUtilities;
import java.util.Arrays;

/**
 * Punto de entrada del Sistema de Aerolinea.
 */
public class Main {

    public static void main(String[] args) {

        IRepositorio<Vuelo> repositorioVuelos =
                new RepositorioArchivo<>(
                        "data/vuelos.dat");

        IRepositorio<Persona> repositorioPersonas =
                new RepositorioArchivo<>(
                        "data/personas.dat");

        Servicio<Vuelo> servicioVuelos =
                new Servicio<>(repositorioVuelos);

        Servicio<Persona> servicioPersonas =
                new Servicio<>(repositorioPersonas);

        Aerolinea aerolinea =
                new Aerolinea(
                        "Aerolinea IFES",
                        servicioVuelos.listar(),
                        servicioPersonas.listar());

        boolean modoConsola =
                Arrays.stream(args)
                        .anyMatch(
                                "--consola"::equalsIgnoreCase);

        if (modoConsola) {
            Menu menu = new Menu(
                    aerolinea,
                    servicioVuelos,
                    servicioPersonas);

            menu.iniciar();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            Ventana ventana = new Ventana(
                    aerolinea,
                    servicioVuelos,
                    servicioPersonas);

            ventana.setVisible(true);
        });
    }
}
