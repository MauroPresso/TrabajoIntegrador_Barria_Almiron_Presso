package aerolinea.ui;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.EnumPanel;
import aerolinea.dominio.Vuelo;
import aerolinea.servicio.Servicio;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;

/**
 * Ventana principal Swing del Sistema de AerolÃ­nea.
 *
 * <p>La clase es anÃ¡loga a Ventana del proyecto Biblioteca: extiende JFrame,
 * contiene un PanelManager y conecta JMenuItem con ActionListener mediante
 * expresiones lambda.</p>
 */
public class Ventana extends JFrame {

    private final PanelManager panelManager;

    public Ventana(Aerolinea aerolinea, Servicio<Vuelo> servicioVuelos) {
        super("Sistema de AerolÃ­nea IFES");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);

        this.panelManager = new PanelManager(aerolinea, servicioVuelos);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelManager, BorderLayout.CENTER);

        setJMenuBar(crearMenu());
    }

    private JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();
        JMenu menu = new JMenu("Opciones");

        JMenuItem itemPrincipal = new JMenuItem("Principal");
        JMenuItem itemVuelos = new JMenuItem("Vuelos");
        JMenuItem itemPasajeros = new JMenuItem("Pasajeros");
        JMenuItem itemReservas = new JMenuItem("Reservas");
        JMenuItem itemSalir = new JMenuItem("Salir");

        itemPrincipal.addActionListener(
                evento -> panelManager.mostrarPanel(EnumPanel.PRINCIPAL));

        itemVuelos.addActionListener(
                evento -> panelManager.mostrarPanel(EnumPanel.VUELOS));

        itemPasajeros.addActionListener(
                evento -> panelManager.mostrarPanel(EnumPanel.PASAJEROS));

        itemReservas.addActionListener(
                evento -> panelManager.mostrarPanel(EnumPanel.RESERVAS));

        itemSalir.addActionListener(
                evento -> dispose());

        menu.add(itemPrincipal);
        menu.addSeparator();
        menu.add(itemVuelos);
        menu.add(itemPasajeros);
        menu.add(itemReservas);
        menu.addSeparator();
        menu.add(itemSalir);

        barra.add(menu);

        return barra;
    }

    public PanelManager getPanelManager() {
        return panelManager;
    }
}