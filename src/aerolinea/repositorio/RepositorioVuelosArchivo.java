package aerolinea.repositorio;

import aerolinea.dominio.Vuelo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @file RepositorioVuelosArchivo.java
 * @brief Implementa la persistencia de vuelos mediante serialización.
 */

/**
 * @class RepositorioVuelosArchivo
 * @brief Repositorio encargado de guardar y recuperar vuelos desde un archivo binario.
 *
 * Esta clase implementa la interfaz IRepositorio<Vuelo> y encapsula la lógica
 * de persistencia usando ObjectOutputStream y ObjectInputStream.
 *
 * El archivo utilizado por defecto es data/vuelos.dat.
 */
public class RepositorioVuelosArchivo implements IRepositorio<Vuelo> {

    /**
     * @brief Ruta por defecto del archivo de vuelos serializados.
     */
    private static final String RUTA_POR_DEFECTO = "data/vuelos.dat";

    /**
     * @brief Ruta del archivo donde se guardan y leen los vuelos.
     */
    private final Path rutaArchivo;

    /**
     * @brief Crea un repositorio usando la ruta por defecto data/vuelos.dat.
     */
    public RepositorioVuelosArchivo() {
        this(RUTA_POR_DEFECTO);
    }

    /**
     * @brief Crea un repositorio usando una ruta específica.
     *
     * @param rutaArchivo Ruta del archivo de persistencia.
     */
    public RepositorioVuelosArchivo(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacía.");
        }

        this.rutaArchivo = Path.of(rutaArchivo);
    }

    /**
     * @brief Guarda la lista de vuelos en un archivo binario.
     *
     * Si la carpeta data no existe, se crea automáticamente antes de escribir
     * el archivo vuelos.dat.
     *
     * @param vuelos Lista de vuelos a guardar.
     * @throws IOException Si ocurre un error al crear la carpeta o escribir el archivo.
     */
    @Override
    public void guardar(List<Vuelo> vuelos) throws IOException {
        if (vuelos == null) {
            throw new IllegalArgumentException("La lista de vuelos no puede ser nula.");
        }

        Path carpeta = rutaArchivo.getParent();

        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }

        try (ObjectOutputStream salida = new ObjectOutputStream(
                new FileOutputStream(rutaArchivo.toFile()))) {

            salida.writeObject(vuelos);
        }
    }

    /**
     * @brief Recupera la lista de vuelos desde el archivo binario.
     *
     * Si el archivo todavía no existe, devuelve una lista vacía para permitir
     * que el sistema inicie normalmente.
     *
     * @return Lista de vuelos recuperados desde el archivo.
     * @throws IOException Si ocurre un error al leer el archivo.
     * @throws ClassNotFoundException Si el archivo contiene clases no reconocidas.
     */
    @Override
    public List<Vuelo> consultar() throws IOException, ClassNotFoundException {
        if (!Files.exists(rutaArchivo)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream entrada = new ObjectInputStream(
                new FileInputStream(rutaArchivo.toFile()))) {

            Object objetoLeido = entrada.readObject();

            if (!(objetoLeido instanceof List<?>)) {
                throw new IOException("El archivo no contiene una lista válida.");
            }

            List<?> listaLeida = (List<?>) objetoLeido;
            List<Vuelo> vuelos = new ArrayList<>();

            for (Object elemento : listaLeida) {
                if (!(elemento instanceof Vuelo)) {
                    throw new IOException("El archivo contiene elementos que no son vuelos.");
                }

                vuelos.add((Vuelo) elemento);
            }

            return vuelos;
        }
    }
}