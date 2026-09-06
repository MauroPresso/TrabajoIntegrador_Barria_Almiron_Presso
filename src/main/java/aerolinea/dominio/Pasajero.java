package aerolinea.dominio;

import aerolinea.excepcion.VueloNoDisponibleException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @class Pasajero
 * @brief Representa a una persona que puede reservar vuelos.
 */
public class Pasajero extends Persona {

    private static final long serialVersionUID = 1L;

    private String numeroPasaporte;
    private final ArrayList<Vuelo> vuelosReservados;

    /**
     * @brief Crea un pasajero.
     * @param dni DNI del pasajero.
     * @param nombre Nombre del pasajero.
     * @param apellido Apellido del pasajero.
     * @param numeroPasaporte Numero de pasaporte. Puede quedar vacio en vuelos nacionales.
     */
    public Pasajero(int dni, String nombre, String apellido, String numeroPasaporte) {
        super(dni, nombre, apellido);
        this.numeroPasaporte = normalizarTextoOpcional(numeroPasaporte);
        this.vuelosReservados = new ArrayList<>();
    }

    /**
     * @brief Obtiene el numero de pasaporte del pasajero.
     * @return Numero de pasaporte.
     */
    public String getNumeroPasaporte() {
        return numeroPasaporte;
    }

    /**
     * @brief Modifica el numero de pasaporte del pasajero.
     * @param numeroPasaporte Nuevo numero de pasaporte.
     */
    public void setNumeroPasaporte(String numeroPasaporte) {
        this.numeroPasaporte = normalizarTextoOpcional(numeroPasaporte);
    }

    /**
     * @brief Obtiene una vista de solo lectura de los vuelos reservados.
     * @return Lista no modificable de vuelos reservados.
     */
    public List<Vuelo> getVuelosReservados() {
        return Collections.unmodifiableList(vuelosReservados);
    }

    /**
     * @brief Reserva un vuelo para este pasajero.
     * @param vuelo Vuelo que se desea reservar.
     * @throws VueloNoDisponibleException Si el vuelo no permite reservas.
     */
    public void reservarVuelo(Vuelo vuelo) throws VueloNoDisponibleException {
        if (vuelo == null) {
            throw new IllegalArgumentException("El vuelo no puede ser nulo.");
        }
        vuelo.reservarPasajero(this);
    }

    /**
     * @brief Cancela una reserva del pasajero.
     * @param vuelo Vuelo cuya reserva se desea cancelar.
     */
    public void cancelarReserva(Vuelo vuelo) {
        if (vuelo == null) {
            throw new IllegalArgumentException("El vuelo no puede ser nulo.");
        }
        vuelo.cancelarReserva(this);
    }

    /**
     * @brief Indica si el pasajero tiene una reserva en el vuelo recibido.
     * @param vuelo Vuelo a consultar.
     * @return true si el pasajero tiene reservado ese vuelo.
     */
    public boolean tieneVueloReservado(Vuelo vuelo) {
        return vuelosReservados.contains(vuelo);
    }

    /**
     * @brief Indica si el pasajero posee al menos una reserva activa.
     * @return true si tiene una reserva en un vuelo no cancelado.
     */
    public boolean tieneReservaActiva() {
        return vuelosReservados.stream()
                .anyMatch(vuelo -> vuelo.getEstado() != EstadoVuelo.CANCELADO);
    }

    /**
     * @brief Muestra las reservas del pasajero usando referencia a metodo.
     */
    public void mostrarReservas() {
        System.out.println("Reservas de " + getNombreCompleto() + ":");
        if (vuelosReservados.isEmpty()) {
            System.out.println("No registra reservas.");
            return;
        }
        vuelosReservados.forEach(Vuelo::mostrarInfo);
    }

    /**
     * @brief Agrega un vuelo reservado evitando duplicados.
     * @param vuelo Vuelo reservado.
     */
    void agregarVueloReservado(Vuelo vuelo) {
        if (!vuelosReservados.contains(vuelo)) {
            vuelosReservados.add(vuelo);
        }
    }

    /**
     * @brief Quita un vuelo reservado.
     * @param vuelo Vuelo a quitar.
     */
    void quitarVueloReservado(Vuelo vuelo) {
        vuelosReservados.remove(vuelo);
    }

    /**
     * @brief Muestra por consola los datos del pasajero.
     */
    @Override
    public void mostrarInfo() {
        System.out.println("Pasajero: " + getNombreCompleto()
                + " | DNI: " + getDni()
                + " | Pasaporte: " + (numeroPasaporte.isEmpty() ? "No informado" : numeroPasaporte)
                + " | Reservas: " + vuelosReservados.size());
    }

    /**
     * @brief Normaliza campos de texto opcionales.
     * @param texto Texto recibido.
     * @return Texto sin espacios laterales o cadena vacia.
     */
    private String normalizarTextoOpcional(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
