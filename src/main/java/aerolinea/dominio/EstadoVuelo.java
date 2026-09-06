/**
 * @file EstadoVuelo.java
 * @brief Declares EstadoVuelo as part of the airline domain model.
 * @details This source file belongs to the Programacion II academic project.
 */

package aerolinea.dominio;

/**
 * @enum EstadoVuelo
 * @brief Representa los estados posibles de un vuelo dentro del sistema.
 */
public enum EstadoVuelo {
    /** El vuelo esta disponible para reservas. */
    PROGRAMADO,

    /** El vuelo ya comenzo y no permite nuevas reservas. */
    EN_VUELO,

    /** El vuelo fue cancelado y no permite reservas. */
    CANCELADO
}
