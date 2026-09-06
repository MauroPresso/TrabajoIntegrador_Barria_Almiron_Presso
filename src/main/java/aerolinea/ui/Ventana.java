package aerolinea.ui;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.EnumPanel;
import aerolinea.dominio.Persona;
import aerolinea.dominio.Vuelo;
import aerolinea.servicio.Servicio;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;

/**
 * Ventana principal Swing del Sistema de AerolÃ­nea.
 */
public class Ventana extends JFrame {

    private final PanelManager panelManager;

    public Ventana(
            Aerolinea aerolinea,
            Servicio<Vuelo> servicioVuelos,
            Servicio<Persona> servicioPersonas) {

        super("Sistema de AerolÃ­nea IFES");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);

        this.panelManager = new PanelManager(
                aerolinea,
                servicioVuelos,
                servicioPersonas);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(
                panelManager,
                BorderLayout.CENTER);

        setJMenuBar(crearMenu());
    }

    private JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();
        JMenu menu = new JMenu("Opciones");

        agregarItem(
                menu,
                "Principal",
                EnumPanel.PRINCIPAL);

        menu.addSeparator();

        agregarItem(
                menu,
                "Ingresar vuelo",
                EnumPanel.FORMULARIO_VUELO);

        agregarItem(
                menu,
                "Listar vuelos",
                EnumPanel.TABLA_VUELOS);

        agregarItem(
                menu,
                "Registrar pasajero",
                EnumPanel.FORMULARIO_PASAJERO);

        agregarItem(
                menu,
                "Listar pasajeros",
                EnumPanel.TABLA_PASAJEROS);

        agregarItem(
                menu,
                "Reservas",
                EnumPanel.RESERVAS);

        menu.addSeparator();

        JMenuItem itemSalir =
                new JMenuItem("Salir");

        itemSalir.addActionListener(
                evento -> dispose());

        menu.add(itemSalir);
        barra.add(menu);

        return barra;
    }

    private void agregarItem(
            JMenu menu,
            String texto,
            EnumPanel destino) {

        JMenuItem item =
                new JMenuItem(texto);

        item.addActionListener(
                evento -> panelManager.mostrarPanel(destino));

        menu.add(item);
    }

    public PanelManager getPanelManager() {
        return panelManager;
    }
}