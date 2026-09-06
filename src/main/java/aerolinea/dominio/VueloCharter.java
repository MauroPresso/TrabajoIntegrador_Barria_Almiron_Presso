package aerolinea.dominio;

/**
 * @class VueloCharter
 * @brief Representa un vuelo contratado para una operacion especial.
 */
public class VueloCharter extends Vuelo {

    private static final long serialVersionUID = 1L;

    private String empresaContratante;
    private double costoTotal;

    /**
     * @brief Crea un vuelo charter.
     * @param numero Numero del vuelo.
     * @param origen Origen del vuelo.
     * @param destino Destino del vuelo.
     * @param fecha Fecha del vuelo.
     * @param capacidad Capacidad maxima de pasajeros.
     * @param empresaContratante Empresa o entidad contratante.
     * @param costoTotal Costo total del vuelo charter.
     */
    public VueloCharter(String numero, String origen, String destino, String fecha, int capacidad,
                        String empresaContratante, double costoTotal) {
        super(numero, origen, destino, fecha, capacidad);
        setEmpresaContratante(empresaContratante);
        setCostoTotal(costoTotal);
    }

    public String getEmpresaContratante() {
        return empresaContratante;
    }

    /**
     * @brief Establece la empresa contratante del vuelo charter.
     * @param empresaContratante Nombre de la empresa contratante.
     * @throws IllegalArgumentException Si el nombre de la empresa es invalido.
     */
    public void setEmpresaContratante(String empresaContratante) {
        if (empresaContratante == null || empresaContratante.trim().isEmpty()) {
            throw new IllegalArgumentException("La empresa contratante no puede estar vacia.");
        }
        this.empresaContratante = empresaContratante.trim();
    }

    public double getCostoTotal() {
        return costoTotal;
    }

    /**
     * @brief Establece el costo total del vuelo charter.
     * @param costoTotal Costo total del vuelo.
     * @throws IllegalArgumentException Si el costo total es negativo.
     */
    public void setCostoTotal(double costoTotal) {
        if (costoTotal < 0) {
            throw new IllegalArgumentException("El costo total no puede ser negativo.");
        }
        this.costoTotal = costoTotal;
    }

    /**
     * @brief Devuelve el tipo concreto del vuelo.
     * @return Texto "Charter".
     */
    @Override
    public String getTipo() {
        return "Charter";
    }

    /**
     * @brief Devuelve informacion especifica del vuelo charter.
     * @return Detalle del vuelo charter.
     */
    @Override
    protected String obtenerDetalleAdicional() {
        return "Empresa contratante: " + empresaContratante
                + " | Costo total: $" + costoTotal;
    }
}
