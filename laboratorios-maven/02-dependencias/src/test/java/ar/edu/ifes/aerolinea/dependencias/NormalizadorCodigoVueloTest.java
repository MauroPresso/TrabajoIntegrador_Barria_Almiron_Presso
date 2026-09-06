/**
 * @file NormalizadorCodigoVueloTest.java
 * @brief JUnit test for an external Maven dependency.
 */
package ar.edu.ifes.aerolinea.dependencias;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Verifies flight-code normalization.
 */
class NormalizadorCodigoVueloTest {

    /**
     * Confirms that spaces and case are normalized.
     */
    @Test
    void normalizaCodigo() {
        assertEquals("AR 1234", NormalizadorCodigoVuelo.normalizar("  ar   1234  "));
    }

    /**
     * Confirms the behavior for blank input.
     */
    @Test
    void normalizaBlanco() {
        assertEquals("", NormalizadorCodigoVuelo.normalizar("   "));
    }
}