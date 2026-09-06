/**
 * @file UiTest.java
 * @brief Declares UiTest as an executable Programacion II laboratory or integration check.
 * @details This source file belongs to the Programacion II academic project.
 */

package aerolinea.test;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.EnumPanel;
import aerolinea.dominio.Persona;
import aerolinea.dominio.Vuelo;
import aerolinea.repositorio.RepositorioArchivo;
import aerolinea.servicio.Servicio;
import aerolinea.ui.PanelManager;

import javax.swing.SwingUtilities;

/**
 * Prueba headless de CardLayout despues de incorporar formularios y tablas.
 */
public class UiTest {

    public static void main(String[] args) throws Exception {

        System.setProperty(
                "java.awt.headless",
                "true");

        SwingUtilities.invokeAndWait(() -> {

            Servicio<Vuelo> vuelos =
                    new Servicio<>(
                            new RepositorioArchivo<>(
                                    "target/ui-test-vuelos.dat"));

            Servicio<Persona> personas =
                    new Servicio<>(
                            new RepositorioArchivo<>(
                                    "target/ui-test-personas.dat"));

            Aerolinea aerolinea =
                    new Aerolinea(
                            "Aerolinea UI Test",
                            vuelos.listar(),
                            personas.listar());

            PanelManager manager =
                    new PanelManager(
                            aerolinea,
                            vuelos,
                            personas);

            if (manager.getComponentCount() != 6) {
                throw new IllegalStateException(
                        "Se esperaban 6 vistas en CardLayout.");
            }

            for (EnumPanel panel : EnumPanel.values()) {
                manager.mostrarPanel(panel);

                if (manager.getPanelActual() != panel) {
                    throw new IllegalStateException(
                            "Fallo navegacion a " + panel);
                }
            }

            System.out.println("CardLayout con 6 vistas: OK");
            System.out.println("EDT: "
                    + SwingUtilities.isEventDispatchThread());
            System.out.println("UiTest finalizado correctamente.");
        });
    }
}
