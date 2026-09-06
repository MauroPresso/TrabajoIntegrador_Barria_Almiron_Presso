package aerolinea.repositorio;

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
 * Repositorio generico basado en serializacion Java.
 *
 * <p>La clase no conoce el tipo concreto que persiste. Puede utilizarse,
 * por ejemplo, como {@code RepositorioArchivo<Vuelo>} o
 * {@code RepositorioArchivo<Persona>}.</p>
 *
 * @param <T> tipo de elemento administrado por el repositorio
 */
public class RepositorioArchivo<T> implements IRepositorio<T> {

    private final Path rutaArchivo;

    /**
     * Crea un repositorio para una ruta de archivo determinada.
     *
     * @param rutaArchivo ruta del archivo de persistencia
     */
    public RepositorioArchivo(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("La ruta del archivo no puede estar vacia.");
        }

        this.rutaArchivo = Path.of(rutaArchivo);
    }

    /**
     * Guarda la lista completa de elementos mediante serializacion.
     */
    @Override
    public void guardar(List<T> elementos) throws IOException {
        if (elementos == null) {
            throw new IllegalArgumentException("La lista de elementos no puede ser nula.");
        }

        Path carpeta = rutaArchivo.getParent();

        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }

        try (ObjectOutputStream salida = new ObjectOutputStream(
                new FileOutputStream(rutaArchivo.toFile()))) {

            salida.writeObject(new ArrayList<>(elementos));
        }
    }

    /**
     * Recupera la lista serializada.
     *
     * <p>Por el borrado de tipos (type erasure) de Java, el tipo generico T
     * no puede verificarse directamente en tiempo de ejecucion. La coherencia
     * del tipo queda garantizada por el uso consistente del repositorio.</p>
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> consultar() throws IOException, ClassNotFoundException {
        if (!Files.exists(rutaArchivo)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream entrada = new ObjectInputStream(
                new FileInputStream(rutaArchivo.toFile()))) {

            Object objetoLeido = entrada.readObject();

            if (!(objetoLeido instanceof List<?>)) {
                throw new IOException("El archivo no contiene una lista valida.");
            }

            return new ArrayList<>((List<T>) objetoLeido);
        }
    }

    public Path getRutaArchivo() {
        return rutaArchivo;
    }
}
