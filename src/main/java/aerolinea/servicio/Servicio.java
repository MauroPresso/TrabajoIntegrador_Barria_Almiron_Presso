package aerolinea.servicio;

import aerolinea.repositorio.IRepositorio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ImplementaciÃ³n genÃ©rica de servicio respaldada por un repositorio.
 *
 * <p>La clase aplica composiciÃ³n y genÃ©ricos: Servicio&lt;T&gt; trabaja contra
 * la abstracciÃ³n IRepositorio&lt;T&gt;, no contra una implementaciÃ³n concreta.</p>
 *
 * @param <T> tipo de elemento administrado
 */
public class Servicio<T> implements IServicio<T> {

    private final IRepositorio<T> repositorio;
    private final List<T> elementos;

    /**
     * Inicializa el servicio recuperando los elementos persistidos.
     *
     * <p>Si todavÃ­a no hay datos, comienza con una lista vacÃ­a. Si existe un
     * problema de lectura, se informa y se permite iniciar el servicio vacÃ­o,
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
     * Agrega un elemento a la colecciÃ³n administrada.
     *
     * <p>La persistencia explÃ­cita se realiza mediante {@link #guardar()}.
     * Esto es Ãºtil en el Sistema de AerolÃ­nea porque algunas operaciones
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
     * <p>Se conserva la misma instancia para que una capa de dominio pueda
     * operar sobre los objetos cargados. La encapsulaciÃ³n definitiva se
     * resolverÃ¡ en la Etapa 3B al separar Aerolinea de la persistencia.</p>
     */
    @Override
    public List<T> listar() {
        return elementos;
    }

    /**
     * Persiste el estado actual de la colecciÃ³n.
     */
    public void guardar() throws IOException {
        repositorio.guardar(new ArrayList<>(elementos));
    }

    /**
     * Reemplaza la colecciÃ³n administrada por una copia del estado recibido.
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