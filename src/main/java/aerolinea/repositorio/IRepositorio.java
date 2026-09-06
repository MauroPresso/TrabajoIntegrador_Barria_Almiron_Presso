package aerolinea.repositorio;

import java.io.IOException;
import java.util.List;

/**
 * @file IRepositorio.java
 * @brief Define una interfaz generica para operaciones de persistencia.
 */

/**
 * @interface IRepositorio
 * @brief Interfaz generica para guardar y consultar datos persistidos.
 *
 * Esta interfaz permite desacoplar las clases de servicio del mecanismo
 * concreto de persistencia. De esta forma, Aerolinea puede trabajar contra
 * una abstraccion sin saber si los datos se guardan en archivos, base de datos
 * u otro medio.
 *
 * @param <T> Tipo de objeto administrado por el repositorio.
 */
public interface IRepositorio<T> {

    /**
     * @brief Guarda una lista de elementos en el medio de persistencia.
     *
     * @param elementos Lista de elementos a guardar.
     * @throws IOException Si ocurre un error durante la escritura.
     */
    void guardar(List<T> elementos) throws IOException;

    /**
     * @brief Consulta una lista de elementos desde el medio de persistencia.
     *
     * @return Lista de elementos recuperados.
     * @throws IOException Si ocurre un error durante la lectura.
     * @throws ClassNotFoundException Si el archivo contiene una clase no reconocida.
     */
    List<T> consultar() throws IOException, ClassNotFoundException;
}
