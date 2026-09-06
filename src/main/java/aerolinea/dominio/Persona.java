package aerolinea.dominio;

import java.io.Serializable;
import java.util.Objects;

/**
 * @class Persona
 * @brief Clase base abstracta para representar personas dentro del sistema.
 *
 * La clase implementa Comparable para permitir ordenar personas por apellido.
 * Tambien redefine equals() y hashCode() tomando como identidad unica el DNI.
 */
public abstract class Persona implements Comparable<Persona>, Serializable {

    private static final long serialVersionUID = 1L;

    private final int dni;
    private String nombre;
    private String apellido;

    /**
     * @brief Crea una persona con sus datos principales.
     * @param dni DNI de la persona. Se usa como identificador unico.
     * @param nombre Nombre de la persona.
     * @param apellido Apellido de la persona.
     */
    public Persona(int dni, String nombre, String apellido) {
        if (dni <= 0) {
            throw new IllegalArgumentException("El DNI debe ser mayor que cero.");
        }
        this.dni = dni;
        setNombre(nombre);
        setApellido(apellido);
    }

    /**
     * @brief Obtiene el DNI de la persona.
     * @return DNI de la persona.
     */
    public int getDni() {
        return dni;
    }

    /**
     * @brief Obtiene el nombre de la persona.
     * @return Nombre de la persona.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @brief Modifica el nombre de la persona.
     * @param nombre Nuevo nombre.
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        }
        this.nombre = nombre.trim();
    }

    /**
     * @brief Obtiene el apellido de la persona.
     * @return Apellido de la persona.
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * @brief Modifica el apellido de la persona.
     * @param apellido Nuevo apellido.
     */
    public void setApellido(String apellido) {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede estar vacio.");
        }
        this.apellido = apellido.trim();
    }

    /**
     * @brief Devuelve el nombre completo de la persona.
     * @return Apellido y nombre en formato legible.
     */
    public String getNombreCompleto() {
        return apellido + ", " + nombre;
    }

    /**
     * @brief Muestra por consola la informacion de la persona.
     */
    public abstract void mostrarInfo();

    /**
     * @brief Compara personas por apellido, luego por nombre y finalmente por DNI.
     * @param otra Otra persona a comparar.
     * @return Valor negativo, cero o positivo segun el orden alfabetico.
     */
    @Override
    public int compareTo(Persona otra) {
        int comparacionApellido = this.apellido.compareToIgnoreCase(otra.apellido);
        if (comparacionApellido != 0) {
            return comparacionApellido;
        }

        int comparacionNombre = this.nombre.compareToIgnoreCase(otra.nombre);
        if (comparacionNombre != 0) {
            return comparacionNombre;
        }

        return Integer.compare(this.dni, otra.dni);
    }

    /**
     * @brief Compara personas usando el DNI como identidad unica.
     * @param obj Objeto a comparar.
     * @return true si ambos objetos representan a la misma persona por DNI.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Persona)) {
            return false;
        }
        Persona otra = (Persona) obj;
        return dni == otra.dni;
    }

    /**
     * @brief Genera el codigo hash usando el DNI.
     * @return Codigo hash de la persona.
     */
    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }
}
