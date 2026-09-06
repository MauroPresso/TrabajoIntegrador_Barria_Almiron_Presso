/**
 * @file NormalizadorCodigoVuelo.java
 * @brief Demonstrates an external Maven dependency with Apache Commons Lang.
 */
package ar.edu.ifes.aerolinea.dependencias;

import org.apache.commons.lang3.StringUtils;

/**
 * Normalizes flight codes using StringUtils resolved by Maven.
 */
public final class NormalizadorCodigoVuelo {

    private NormalizadorCodigoVuelo() {
    }

    /**
     * Removes surrounding spaces, collapses repeated spaces and converts to upper case.
     *
     * @param codigo raw flight code
     * @return normalized code, or an empty string when input is blank
     */
    public static String normalizar(String codigo) {
        if (StringUtils.isBlank(codigo)) {
            return "";
        }
        return StringUtils.upperCase(StringUtils.normalizeSpace(codigo));
    }
}