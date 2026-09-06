package aerolinea.dominio;

/**
 * @class VueloInternacional
 * @brief Representa un vuelo realizado hacia otro país.
 */
public class VueloInternacional extends Vuelo {

    private static final long serialVersionUID = 1L;

    private String paisDestino;
    private boolean requierePasaporte;

    /**
     * @brief Crea un vuelo internacional.
     * @param numero Número del vuelo.
     * @param origen Origen del vuelo.
     * @param destino Destino del vuelo.
     * @param fecha Fecha del vuelo.
     * @param capacidad Capacidad máxima de pasajeros.
     * @param paisDestino País de destino.
     * @param requierePasaporte Indica si se requiere pasaporte.
     */
    public VueloInternacional(String numero, String origen, String destino, String fecha, int capacidad,
                              String paisDestino, boolean requierePasaporte) {
        super(numero, origen, destino, fecha, capacidad);
        setPaisDestino(paisDestino);
        this.requierePasaporte = requierePasaporte;
    }

    public String getPaisDestino() {
        return paisDestino;
    }

    /**
     * @brief Establece el país de destino del vuelo internacional.
     * @param paisDestino Nombre del país de destino.
     * @throws IllegalArgumentException Si el nombre del país es inválido.
     */
    public void setPaisDestino(String paisDestino) {
        if (paisDestino == null || paisDestino.trim().isEmpty()) {
            throw new IllegalArgumentException("El país de destino no puede estar vacío.");
        }
        this.paisDestino = paisDestino.trim();
    }

    public boolean isRequierePasaporte() {
        return requierePasaporte;
    }

    public void setRequierePasaporte(boolean requierePasaporte) {
        this.requierePasaporte = requierePasaporte;
    }

    /**
     * @brief Devuelve el tipo concreto del vuelo.
     * @return Texto "Internacional".
     */
    @Override
    public String getTipo() {
        return "Internacional";
    }

    /**
     * @brief Devuelve información específica del vuelo internacional.
     * @return Detalle del vuelo internacional.
     */
    @Override
    protected String obtenerDetalleAdicional() {
        return "País destino: " + paisDestino
                + " | Requiere pasaporte: " + (requierePasaporte ? "Sí" : "No");
    }
}