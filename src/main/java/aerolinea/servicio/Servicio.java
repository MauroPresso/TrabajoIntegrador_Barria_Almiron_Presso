/**
 * @file Servicio.java
 * @brief Declares Servicio as part of the generic service layer.
 * @details This source file belongs to the Programacion II academic project.
 */

package aerolinea.servicio;

import aerolinea.repositorio.IRepositorio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion generica de servicio respaldada por un repositorio.
 *
 * <p>La clase aplica composicion y genericos: Servicio&lt;T&gt; trabaja contra
 * la abstraccion IRepositorio&lt;T&gt;, no contra una implementacion concreta.</p>
 *
 * @param <T> tipo de elemento administrado
 */
public class Servicio<T> implements IServicio<T> {

    private final IRepositorio<T> repositorio;
    private final List<T> elementos;

    /**
     * Inicializa el servicio recuperando los elementos persistidos.
     *
     * <p>Si todavia no hay datos, comienza con una lista vacia. Si existe un
     * problema de lectura, se informa y se permite iniciar el servicio vacio,
     * manteniendo el criterio tolerante utilizado por el sistema actual.</p>
     */
    public Servicio(IRepositorio<T> repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo.");
        }

        this.repositorio = repositorio;
        this.elementos = new ArrayList<>();

        try {
            List<T> recuperados = repositorio.consultar();

            if (recuperados != null) {
                elementos.addAll(recuperados);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(
                    "No se pudieron recuperar los elementos persistidos: " + e.getMessage());
        }
    }

    /**
     * Agrega un elemento a la coleccion administrada.
     *
     * <p>La persistencia explicita se realiza mediante {@link #guardar()}.
     * Esto es util en el Sistema de Aerolinea porque algunas operaciones
     * modifican objetos ya existentes, por ejemplo una reserva dentro de un vuelo.</p>
     */
    @Override
    public void agregar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("El elemento no puede ser nulo.");
        }

        elementos.add(elemento);
    }

    /**
     * Devuelve una vista de la lista administrada.
     *
     * <p>La lista representa el estado administrado por el servicio. La capa de
     * dominio recibe una copia al inicializarse y se sincroniza explicitamente
     * mediante reemplazarTodos() antes de persistir.</p>
     */
    @Override
    public List<T> listar() {
        return elementos;
    }

    /**
     * Persiste el estado actual de la coleccion.
     */
    public void guardar() throws IOException {
        repositorio.guardar(new ArrayList<>(elementos));
    }

    /**
     * Reemplaza la coleccion administrada por una copia del estado recibido.
     *
     * <p>Permite sincronizar una capa de dominio con el servicio sin hacer que
     * el dominio conozca repositorios o archivos.</p>
     */
    public void reemplazarTodos(List<T> nuevosElementos) {
        if (nuevosElementos == null) {
            throw new IllegalArgumentException("La lista de elementos no puede ser nula.");
        }

        elementos.clear();
        elementos.addAll(nuevosElementos);
    }

    public IRepositorio<T> getRepositorio() {
        return repositorio;
    }
}
