package aerolinea.test;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.EnumPanel;
import aerolinea.dominio.Vuelo;
import aerolinea.repositorio.RepositorioArchivo;
import aerolinea.servicio.Servicio;
import aerolinea.ui.PanelManager;
import aerolinea.ui.PanelPrincipal;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import java.awt.Component;

/**
 * Prueba ejecutable de la navegaciÃ³n Swing sin abrir una ventana real.
 *
 * <p>Se ejecuta en modo headless y verifica CardLayout y los eventos
 * de los botones del PanelPrincipal.</p>
 */
public class UiTest {

    public static void main(String[] args) throws Exception {

        System.setProperty("java.awt.headless", "true");

        SwingUtilities.invokeAndWait(() -> {

            Servicio<Vuelo> servicio = new Servicio<>(
                    new RepositorioArchivo<>("target/ui-test-vuelos.dat"));

            Aerolinea aerolinea =
                    new Aerolinea("AerolÃ­nea UI Test", servicio.listar());

            PanelManager manager =
                    new PanelManager(aerolinea, servicio);

            if (manager.getComponentCount() != 4) {
                throw new IllegalStateException(
                        "Se esperaban 4 paneles en CardLayout.");
            }

            if (manager.getPanelActual() != EnumPanel.PRINCIPAL) {
                throw new IllegalStateException(
                        "El panel inicial deberÃ­a ser PRINCIPAL.");
            }

            manager.mostrarPanel(EnumPanel.VUELOS);

            if (manager.getPanelActual() != EnumPanel.VUELOS) {
                throw new IllegalStateException(
                        "CardLayout no cambiÃ³ al panel VUELOS.");
            }

            manager.mostrarPanel(EnumPanel.PRINCIPAL);

            PanelPrincipal principal = manager.getPanelPrincipal();

            JButton botonPasajeros =
                    buscarBoton(principal, "Pasajeros");

            botonPasajeros.doClick();

            if (manager.getPanelActual() != EnumPanel.PASAJEROS) {
                throw new IllegalStateException(
                        "El ActionListener del botÃ³n Pasajeros no navegÃ³ correctamente.");
            }

            manager.mostrarPanel(EnumPanel.PRINCIPAL);

            JButton botonReservas =
                    buscarBoton(principal, "Reservas");

            botonReservas.doClick();

            if (manager.getPanelActual() != EnumPanel.RESERVAS) {
                throw new IllegalStateException(
                        "El ActionListener del botÃ³n Reservas no navegÃ³ correctamente.");
            }

            System.out.println("CardLayout: OK");
            System.out.println("ActionListener con lambdas: OK");
            System.out.println("Event Dispatch Thread: "
                    + SwingUtilities.isEventDispatchThread());
            System.out.println("UiTest finalizado correctamente.");
        });
    }

    private static JButton buscarBoton(
            PanelPrincipal panel,
            String texto) {

        for (Component componente : panel.getComponents()) {
            if (componente instanceof JButton) {
                JButton boton = (JButton) componente;

                if (texto.equals(boton.getText())) {
                    return boton;
                }
            }
        }

        throw new IllegalStateException(
                "No se encontrÃ³ el botÃ³n: " + texto);
    }
}