/**
 * @file CalculadoraTarifas.java
 * @brief Declares CalculadoraTarifas as an auxiliary utility or comparison strategy.
 * @details This source file belongs to the Programacion II academic project.
 */

package aerolinea.util;

/**
 * Utilidad didactica para demostrar sobrecarga de metodos.
 *
 * <p>La misma operacion conceptual, calcularTotal, posee distintas firmas.
 * Java decide que version invocar segun la cantidad y tipo de argumentos.</p>
 */
public class CalculadoraTarifas {

    /**
     * Calcula el costo total de varios pasajeros con una misma tarifa.
     */
    public double calcularTotal(double tarifaPorPasajero, int cantidadPasajeros) {
        if (tarifaPorPasajero < 0 || cantidadPasajeros < 0) {
            throw new IllegalArgumentException("La tarifa y la cantidad no pueden ser negativas.");
        }

        return tarifaPorPasajero * cantidadPasajeros;
    }

    /**
     * Calcula el costo total e incorpora un porcentaje adicional.
     */
    public double calcularTotal(double tarifaPorPasajero,
                                int cantidadPasajeros,
                                double porcentajeAdicional) {

        if (porcentajeAdicional < 0) {
            throw new IllegalArgumentException("El porcentaje adicional no puede ser negativo.");
        }

        double subtotal = calcularTotal(tarifaPorPasajero, cantidadPasajeros);
        return subtotal + subtotal * porcentajeAdicional / 100.0;
    }

    /**
     * Suma una cantidad variable de importes individuales.
     */
    public double calcularTotal(double... importes) {
        if (importes == null) {
            throw new IllegalArgumentException("Los importes no pueden ser nulos.");
        }

        double total = 0.0;

        for (double importe : importes) {
            if (importe < 0) {
                throw new IllegalArgumentException("Los importes no pueden ser negativos.");
            }

            total += importe;
        }

        return total;
    }
}
