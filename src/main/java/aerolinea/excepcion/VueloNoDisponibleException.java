package aerolinea.excepcion;

/**
 * @file VueloNoDisponibleException.java
 * @brief Define una excepcion checked para vuelos no disponibles.
 */

/**
 * @class VueloNoDisponibleException
 * @brief Excepcion personalizada para indicar que un vuelo no puede ser reservado.
 *
 * Esta excepcion se lanza cuando se intenta reservar un vuelo que se encuentra
 * en vuelo, cancelado o sin asientos disponibles.
 *
 * Al extender de Exception, se trata de una excepcion checked, por lo que debe
 * ser declarada con throws o manejada con try-catch.
 */
public class VueloNoDisponibleException extends Exception {

    /**
     * Para compatibilidad de serializacion
     */
    private static final long serialVersionUID = 1L;

    /**
     * @brief Constructor con mensaje descriptivo.
     *
     * @param mensaje Mensaje que explica el motivo por el cual el vuelo no esta disponible.
     */
    public VueloNoDisponibleException(String mensaje) {
        super(mensaje);
    }

    /**
     * @brief Constructor con mensaje y causa original.
     *
     * @param mensaje Mensaje que explica el motivo de la excepcion.
     * @param causa Excepcion original que produjo este error.
     */
    public VueloNoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
