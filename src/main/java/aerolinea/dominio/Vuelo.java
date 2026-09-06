/**
 * @file Vuelo.java
 * @brief Declares Vuelo as part of the airline domain model.
 * @details This source file belongs to the Programacion II academic project.
 */

package aerolinea.dominio;

import aerolinea.excepcion.VueloNoDisponibleException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @class Vuelo
 * @brief Clase abstracta base para todos los tipos de vuelo.
 *
 * Implementa IOperable para exponer operaciones comunes como embarcar y cancelar.
 * Tambien implementa Serializable para permitir la persistencia en archivo.
 */
public abstract class Vuelo implements IOperable, Comparable<Vuelo>, Serializable {

    private static final long serialVersionUID = 1L;

    private String numero;
    private String origen;
    private String destino;
    private String fecha;
    private int capacidad;
    private EstadoVuelo estado;
    private final ArrayList<Pasajero> pasajeros;
    private final ArrayList<Tripulante> tripulacion;

    /**
     * @brief Crea un vuelo con los datos comunes a todos los tipos.
     * @param numero Numero identificador del vuelo.
     * @param origen Ciudad o aeropuerto de origen.
     * @param destino Ciudad o aeropuerto de destino.
     * @param fecha Fecha del vuelo en formato texto.
     * @param capacidad Cantidad maxima de pasajeros.
     */
    public Vuelo(String numero, String origen, String destino, String fecha, int capacidad) {
        setNumero(numero);
        setOrigen(origen);
        setDestino(destino);
        setFecha(fecha);
        setCapacidad(capacidad);
        this.estado = EstadoVuelo.PROGRAMADO;
        this.pasajeros = new ArrayList<>();
        this.tripulacion = new ArrayList<>();
    }

    /**
     * @brief Devuelve el tipo concreto de vuelo.
     * @return Tipo de vuelo como texto.
     */
    public abstract String getTipo();

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = validarTextoObligatorio(numero, "numero");
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = validarTextoObligatorio(origen, "origen");
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = validarTextoObligatorio(destino, "destino");
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = validarTextoObligatorio(fecha, "fecha");
    }

    public int getCapacidad() {
        return capacidad;
    }

    /**
     * @brief Establece la capacidad del vuelo asegurando que no sea menor a los pasajeros ya reservados.
     * @param capacidad Nueva capacidad del vuelo.
     * @throws IllegalArgumentException Si la capacidad es invalida o menor a los pasajeros actuales.
     */
    public void setCapacidad(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        }
        if (pasajeros != null && capacidad < pasajeros.size()) {
            throw new IllegalArgumentException("La capacidad no puede ser menor a los asientos ya ocupados.");
        }
        this.capacidad = capacidad;
    }

    public EstadoVuelo getEstado() {
        return estado;
    }

    /**
     * @brief Cambia el estado del vuelo asegurando que no sea nulo.
     * @param estado Nuevo estado del vuelo.
     * @throws IllegalArgumentException Si el estado es nulo.
     */
    public void setEstado(EstadoVuelo estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }
        this.estado = estado;
    }

    public List<Pasajero> getPasajeros() {
        return Collections.unmodifiableList(pasajeros);
    }

    public List<Tripulante> getTripulacion() {
        return Collections.unmodifiableList(tripulacion);
    }

    /**
     * @brief Reserva un asiento para un pasajero.
     * @param pasajero Pasajero que desea reservar.
     * @throws VueloNoDisponibleException Si el vuelo esta en vuelo, cancelado o sin asientos.
     */
    public void reservarPasajero(Pasajero pasajero) throws VueloNoDisponibleException {
        if (pasajero == null) {
            throw new IllegalArgumentException("El pasajero no puede ser nulo.");
        }
        if (pasajeros.contains(pasajero)) {
            return;
        }
        validarDisponibilidadParaReserva();
        pasajeros.add(pasajero);
        pasajero.agregarVueloReservado(this);
    }

    /**
     * @brief Cancela la reserva de un pasajero en este vuelo.
     * @param pasajero Pasajero cuya reserva se desea cancelar.
     * @return true si la reserva existia y fue cancelada.
     */
    public boolean cancelarReserva(Pasajero pasajero) {
        if (pasajero == null) {
            throw new IllegalArgumentException("El pasajero no puede ser nulo.");
        }
        boolean eliminado = pasajeros.remove(pasajero);
        if (eliminado) {
            pasajero.quitarVueloReservado(this);
        }
        return eliminado;
    }

    /**
     * @brief Agrega un tripulante al vuelo evitando duplicados por DNI.
     * @param tripulante Tripulante que se desea agregar.
     */
    public void agregarTripulante(Tripulante tripulante) {
        if (tripulante == null) {
            throw new IllegalArgumentException("El tripulante no puede ser nulo.");
        }
        if (!tripulacion.contains(tripulante)) {
            tripulacion.add(tripulante);
        }
    }

    public int getAsientosOcupados() {
        return pasajeros.size();
    }

    public int getAsientosDisponibles() {
        return capacidad - pasajeros.size();
    }

    public boolean hayAsientosDisponibles() {
        return getAsientosDisponibles() > 0;
    }

    /**
     * @brief Cambia el estado del vuelo a EN_VUELO cuando esta programado.
     */
    @Override
    public void embarcar() {
        if (estado == EstadoVuelo.PROGRAMADO) {
            estado = EstadoVuelo.EN_VUELO;
            System.out.println("Embarque iniciado para el vuelo " + numero + ".");
        } else {
            System.out.println("No se puede embarcar el vuelo " + numero + " porque esta " + estado + ".");
        }
    }

    /**
     * @brief Cancela el vuelo cambiando su estado a CANCELADO.
     */
    @Override
    public void cancelar() {
        estado = EstadoVuelo.CANCELADO;
        System.out.println("Vuelo " + numero + " cancelado.");
    }

    /**
     * @brief Muestra la informacion comun y especifica del vuelo.
     */
    public void mostrarInfo() {
        System.out.println("Vuelo " + numero
                + " | Tipo: " + getTipo()
                + " | Origen: " + origen
                + " | Destino: " + destino
                + " | Fecha: " + fecha
                + " | Estado: " + estado
                + " | Ocupados: " + getAsientosOcupados() + "/" + capacidad
                + " | Disponibles: " + getAsientosDisponibles());

        String detalle = obtenerDetalleAdicional();
        if (!detalle.isEmpty()) {
            System.out.println("Detalle: " + detalle);
        }
    }

    /**
     * @brief Permite a las subclases agregar datos propios al mostrar informacion.
     * @return Detalle especifico del tipo de vuelo.
     */
    protected String obtenerDetalleAdicional() {
        return "";
    }

    /**
     * @brief Valida si el vuelo esta disponible para reservar.
     * @throws VueloNoDisponibleException Si el vuelo no permite reservas.
     */
    private void validarDisponibilidadParaReserva() throws VueloNoDisponibleException {
        if (estado == EstadoVuelo.EN_VUELO) {
            throw new VueloNoDisponibleException("El vuelo " + numero + " ya esta en vuelo.");
        }
        if (estado == EstadoVuelo.CANCELADO) {
            throw new VueloNoDisponibleException("El vuelo " + numero + " esta cancelado.");
        }
        if (!hayAsientosDisponibles()) {
            throw new VueloNoDisponibleException("El vuelo " + numero + " no tiene asientos disponibles.");
        }
    }

    private String validarTextoObligatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede estar vacio.");
        }
        return valor.trim();
    }

    /**
     * @brief Compara vuelos por numero de vuelo.
     *
     * Define el orden natural de los vuelos usando el campo numero.
     * Esto permite ordenar una lista de vuelos mediante Collections.sort().
     *
     * @param otro Otro vuelo a comparar.
     * @return Valor negativo, cero o positivo segun el orden alfabetico del numero.
     */
    @Override
    public int compareTo(Vuelo otro) {
        return this.numero.compareToIgnoreCase(otro.numero);
    }

    /**
     * @brief Compara vuelos por numero.
     * @param obj Objeto a comparar.
     * @return true si ambos vuelos tienen el mismo numero.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vuelo)) {
            return false;
        }
        Vuelo otro = (Vuelo) obj;
        return numero.equalsIgnoreCase(otro.numero);
    }

    /**
     * @brief Genera el codigo hash del vuelo usando su numero.
     * @return Codigo hash del vuelo.
     */
    @Override
    public int hashCode() {
        return Objects.hash(numero.toUpperCase());
    }
}
