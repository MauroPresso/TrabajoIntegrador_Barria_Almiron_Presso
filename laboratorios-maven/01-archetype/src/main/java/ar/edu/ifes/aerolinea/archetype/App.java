/**
 * @file App.java
 * @brief Minimal application used to demonstrate a Maven Quickstart layout.
 */
package ar.edu.ifes.aerolinea.archetype;

/**
 * Minimal executable class for the archetype laboratory.
 */
public final class App {

    private App() {
    }

    /**
     * Returns a fixed laboratory message.
     *
     * @return message used by the unit test
     */
    public static String mensaje() {
        return "SistemaDeAerolinea - Maven Archetype";
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println(mensaje());
    }
}