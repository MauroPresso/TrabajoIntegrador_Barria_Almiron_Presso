/**
 * @file IServicio.java
 * @brief Declares IServicio as part of the generic service layer.
 * @details This source file belongs to the Programacion II academic project.
 */

package aerolinea.servicio;

import java.util.List;

/**
 * Contrato generico de una capa de servicio.
 *
 * <p>Es analogo a IServicio&lt;T&gt; del proyecto Biblioteca del profesor:
 * permite agregar elementos y obtener la coleccion administrada sin depender
 * de una clase concreta.</p>
 *
 * @param <T> tipo de elemento administrado
 */
public interface IServicio<T> {

    void agregar(T elemento);

    List<T> listar();
}
