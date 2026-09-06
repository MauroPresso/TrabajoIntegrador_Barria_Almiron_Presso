/**
 * @file DemoDeploy.java
 * @brief Minimal artifact used to demonstrate Maven install and deploy phases.
 */
package ar.edu.ifes.aerolinea.deploy;

/**
 * Small class packaged into the deploy-demo JAR.
 */
public final class DemoDeploy {

    private DemoDeploy() {
    }

    /**
     * Returns a description of the laboratory.
     *
     * @return laboratory description
     */
    public static String descripcion() {
        return "Artefacto Maven para laboratorio install y deploy";
    }
}