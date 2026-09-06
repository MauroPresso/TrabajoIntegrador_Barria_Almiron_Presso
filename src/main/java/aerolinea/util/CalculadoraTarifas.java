package aerolinea.util;

/**
 * Utilidad didÃ¡ctica para demostrar sobrecarga de mÃ©todos.
 *
 * <p>La misma operaciÃ³n conceptual, calcularTotal, posee distintas firmas.
 * Java decide quÃ© versiÃ³n invocar segÃºn la cantidad y tipo de argumentos.</p>
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