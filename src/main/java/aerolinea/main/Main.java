package aerolinea.main;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.Vuelo;
import aerolinea.repositorio.IRepositorio;
import aerolinea.repositorio.RepositorioArchivo;
import aerolinea.servicio.Servicio;
import aerolinea.ui.Menu;

/**
 * Punto de entrada principal del Sistema de AerolÃ­nea.
 *
 * <p>Main ensambla las capas de la aplicaciÃ³n:</p>
 *
 * <pre>
 * RepositorioArchivo&lt;Vuelo&gt;
 *          â†“
 *     Servicio&lt;Vuelo&gt;
 *          â†“
 *       Aerolinea
 *          â†“
 *         Menu
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

        Menu menu =
                new Menu(aerolinea, servicioVuelos);

        menu.iniciar();
    }
}