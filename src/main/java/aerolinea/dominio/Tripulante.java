package aerolinea.dominio;

/**
 * @class Tripulante
 * @brief Representa a una persona que forma parte de la tripulación.
 */
public class Tripulante extends Persona {

    private static final long serialVersionUID = 1L;

    private int legajo;
    private String rol;

    /**
     * @brief Crea un tripulante.
     * @param dni DNI del tripulante.
     * @param nombre Nombre del tripulante.
     * @param apellido Apellido del tripulante.
     * @param legajo Legajo interno del tripulante.
     * @param rol Rol o función dentro del vuelo.
     */
    public Tripulante(int dni, String nombre, String apellido, int legajo, String rol) {
        super(dni, nombre, apellido);
        setLegajo(legajo);
        setRol(rol);
    }

    /**
     * @brief Obtiene el legajo del tripulante.
     * @return Legajo del tripulante.
     */
    public int getLegajo() {
        return legajo;
    }

    /**
     * @brief Modifica el legajo del tripulante.
     * @param legajo Nuevo legajo.
     */
    public void setLegajo(int legajo) {
        if (legajo <= 0) {
            throw new IllegalArgumentException("El legajo debe ser mayor que cero.");
        }
        this.legajo = legajo;
    }

    /**
     * @brief Obtiene el rol del tripulante.
     * @return Rol del tripulante.
     */
    public String getRol() {
        return rol;
    }

    /**
     * @brief Modifica el rol del tripulante.
     * @param rol Nuevo rol.
     */
    public void setRol(String rol) {
        if (rol == null || rol.trim().isEmpty()) {
            throw new IllegalArgumentException("El rol no puede estar vacío.");
        }
        this.rol = rol.trim();
    }

    /**
     * @brief Muestra por consola los datos del tripulante.
     */
    @Override
    public void mostrarInfo() {
        System.out.println("Tripulante: " + getNombreCompleto()
                + " | DNI: " + getDni()
                + " | Legajo: " + legajo
                + " | Rol: " + rol);
    }
}