package aerolinea.dominio;

/**
 * @class VueloNacional
 * @brief Representa un vuelo realizado dentro del pais.
 */
public class VueloNacional extends Vuelo {

    private static final long serialVersionUID = 1L;

    private String provinciaDestino;

    /**
     * @brief Crea un vuelo nacional.
     * @param numero Numero del vuelo.
     * @param origen Origen del vuelo.
     * @param destino Destino del vuelo.
     * @param fecha Fecha del vuelo.
     * @param capacidad Capacidad maxima de pasajeros.
     * @param provinciaDestino Provincia de destino.
     */
    public VueloNacional(String numero, String origen, String destino, String fecha, int capacidad,
                         String provinciaDestino) {
        super(numero, origen, destino, fecha, capacidad);
        setProvinciaDestino(provinciaDestino);
    }

    public String getProvinciaDestino() {
        return provinciaDestino;
    }

    /**
     * @brief Establece la provincia de destino del vuelo nacional.
     * @param provinciaDestino Nombre de la provincia de destino.
     * @throws IllegalArgumentException Si el nombre de la provincia es invalido.
     */
    public void setProvinciaDestino(String provinciaDestino) {
        if (provinciaDestino == null || provinciaDestino.trim().isEmpty()) {
            throw new IllegalArgumentException("La provincia de destino no puede estar vacia.");
        }
        this.provinciaDestino = provinciaDestino.trim();
    }

    /**
     * @brief Devuelve el tipo concreto del vuelo.
     * @return Texto "Nacional".
     */
    @Override
    public String getTipo() {
        return "Nacional";
    }

    /**
     * @brief Devuelve informacion especifica del vuelo nacional.
     * @return Detalle del vuelo nacional.
     */
    @Override
    protected String obtenerDetalleAdicional() {
        return "Provincia destino: " + provinciaDestino;
    }
}
