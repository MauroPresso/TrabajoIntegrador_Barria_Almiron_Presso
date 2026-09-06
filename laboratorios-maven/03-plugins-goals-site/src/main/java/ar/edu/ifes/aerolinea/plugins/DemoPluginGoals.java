/**
 * @file DemoPluginGoals.java
 * @brief Small application executed through exec-maven-plugin.
 */
package ar.edu.ifes.aerolinea.plugins;

/**
 * Demonstrates explicit execution of a Maven plugin goal.
 */
public final class DemoPluginGoals {

    private DemoPluginGoals() {
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        System.out.println("Goal exec:java ejecutado correctamente.");
    }
}