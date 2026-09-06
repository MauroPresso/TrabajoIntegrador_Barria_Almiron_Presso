/**
 * @file AppTest.java
 * @brief JUnit test for the Maven archetype laboratory.
 */
package ar.edu.ifes.aerolinea.archetype;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Verifies the minimal Quickstart-style application.
 */
class AppTest {

    /**
     * Confirms that the application exposes the expected message.
     */
    @Test
    void mensajeContieneMaven() {
        assertTrue(App.mensaje().contains("Maven"));
    }
}