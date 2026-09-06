package aerolinea.main;

import aerolinea.repositorio.IRepositorio;
import aerolinea.ui.Menu;
import aerolinea.dominio.Vuelo;
import aerolinea.repositorio.RepositorioArchivo;
import aerolinea.servicio.Aerolinea;

/**
 * @file Main.java
 * @brief Punto de entrada principal del sistema de aerolínea.
 */

/**
 * @class Main
 * @brief Clase principal desde donde inicia la aplicación.
 *
 * Crea el repositorio de vuelos, inicializa la aerolínea con persistencia
 * y ejecuta el menú interactivo por consola.
 */
public class Main {

    /**
     * @brief Método principal del programa.
     *
     * Inicializa un repositorio basado en archivo para permitir que la lista
     * de vuelos se cargue desde data/vuelos.dat al iniciar el sistema.
     *
     * @param args Argumentos de consola no utilizados.
     */
    public static void main(String[] args) {
        IRepositorio<Vuelo> repositorioVuelos = new RepositorioArchivo<Vuelo>("data/vuelos.dat");

        Aerolinea aerolinea = new Aerolinea("Aerolínea IFES", repositorioVuelos);

        Menu menu = new Menu(aerolinea);
        menu.iniciar();
    }
}