package aerolinea.util;

import java.util.Comparator;

import aerolinea.dominio.Vuelo;

/**
 * @class ComparadorVueloPorNumero
 * @brief Comparator externo para ordenar vuelos por número.
 *
 * Aunque Vuelo ya posee orden natural por número mediante Comparable,
 * este comparador externo se incluye para demostrar explícitamente
 * el uso de Comparator.
 */
public class ComparadorVueloPorNumero implements Comparator<Vuelo> {

    /**
     * @brief Compara dos vuelos por número de vuelo.
     *
     * @param vuelo1 Primer vuelo.
     * @param vuelo2 Segundo vuelo.
     * @return Valor negativo, cero o positivo según el número de vuelo.
     */
    @Override
    public int compare(Vuelo vuelo1, Vuelo vuelo2) {
        return vuelo1.getNumero().compareToIgnoreCase(vuelo2.getNumero());
    }
}