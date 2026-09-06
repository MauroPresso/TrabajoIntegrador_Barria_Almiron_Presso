/**
 * @file ComparadorVueloPorDestino.java
 * @brief Declares ComparadorVueloPorDestino as an auxiliary utility or comparison strategy.
 * @details This source file belongs to the Programacion II academic project.
 */

package aerolinea.util;

import java.util.Comparator;

import aerolinea.dominio.Vuelo;

/**
 * @class ComparadorVueloPorDestino
 * @brief Comparator externo para ordenar vuelos por destino.
 *
 * Este comparador permite ordenar una lista de vuelos por destino
 * sin modificar el orden natural definido en la clase Vuelo.
 */
public class ComparadorVueloPorDestino implements Comparator<Vuelo> {

    /**
     * @brief Compara dos vuelos por destino.
     *
     * Si ambos destinos son iguales, se ordena por numero de vuelo.
     *
     * @param vuelo1 Primer vuelo.
     * @param vuelo2 Segundo vuelo.
     * @return Valor negativo, cero o positivo segun el orden.
     */
    @Override
    public int compare(Vuelo vuelo1, Vuelo vuelo2) {
        int comparacionDestino = vuelo1.getDestino().compareToIgnoreCase(vuelo2.getDestino());

        if (comparacionDestino != 0) {
            return comparacionDestino;
        }

        return vuelo1.getNumero().compareToIgnoreCase(vuelo2.getNumero());
    }
}
