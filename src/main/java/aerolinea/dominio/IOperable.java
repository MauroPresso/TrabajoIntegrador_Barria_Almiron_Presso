/**
 * @file IOperable.java
 * @brief Declares IOperable as part of the airline domain model.
 * @details This source file belongs to the Programacion II academic project.
 */

package aerolinea.dominio;

/**
 * @interface IOperable
 * @brief Define operaciones basicas que puede realizar un vuelo.
 */
public interface IOperable {

    /**
     * @brief Inicia el embarque o cambia el estado operativo del vuelo.
     */
    void embarcar();

    /**
     * @brief Cancela la operacion del vuelo.
     */
    void cancelar();
}
