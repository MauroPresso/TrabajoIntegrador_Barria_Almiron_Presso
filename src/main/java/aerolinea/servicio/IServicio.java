package aerolinea.servicio;

import java.util.List;

/**
 * Contrato genÃ©rico de una capa de servicio.
 *
 * <p>Es anÃ¡logo a IServicio&lt;T&gt; del proyecto Biblioteca del profesor:
 * permite agregar elementos y obtener la colecciÃ³n administrada sin depender
 * de una clase concreta.</p>
 *
 * @param <T> tipo de elemento administrado
 */
public interface IServicio<T> {

    void agregar(T elemento);

    List<T> listar();
}