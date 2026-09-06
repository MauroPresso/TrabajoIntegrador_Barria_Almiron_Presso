package aerolinea.dominio;

/**
 * @enum EstadoVuelo
 * @brief Representa los estados posibles de un vuelo dentro del sistema.
 */
public enum EstadoVuelo {
    /** El vuelo está disponible para reservas. */
    PROGRAMADO,

    /** El vuelo ya comenzó y no permite nuevas reservas. */
    EN_VUELO,

    /** El vuelo fue cancelado y no permite reservas. */
    CANCELADO
}