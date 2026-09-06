package aerolinea.dominio;

/**
 * @interface IOperable
 * @brief Define operaciones básicas que puede realizar un vuelo.
 */
public interface IOperable {

    /**
     * @brief Inicia el embarque o cambia el estado operativo del vuelo.
     */
    void embarcar();

    /**
     * @brief Cancela la operación del vuelo.
     */
    void cancelar();
}