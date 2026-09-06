/**
 * @file ResumenAerolinea.java
 * @brief Shared class provided by the JAR module in the Maven Reactor.
 */
package ar.edu.ifes.aerolinea.core;

/**
 * Exposes a minimal value consumed by the web module.
 */
public final class ResumenAerolinea {

    /**
     * Returns a message used by the JSP in the WAR module.
     *
     * @return system status
     */
    public String estadoGeneral() {
        return "Sistema de Aerolinea operativo - modulo core JAR";
    }
}