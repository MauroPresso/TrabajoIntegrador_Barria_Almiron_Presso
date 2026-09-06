package aerolinea.ui;

import aerolinea.dominio.EnumPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Font;

/**
 * Panel principal de navegaciÃ³n del Sistema de AerolÃ­nea.
 *
 * <p>Es anÃ¡logo al PanelPrincipal del proyecto Biblioteca: utiliza botones
 * Swing y ActionListener expresados mediante lambdas para solicitar cambios
 * de vista al PanelManager.</p>
 */
public class PanelPrincipal extends JPanel {

    private final PanelManager manager;

    public PanelPrincipal(PanelManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("El PanelManager no puede ser nulo.");
        }

        this.manager = manager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        JLabel titulo = new JLabel("Sistema de AerolÃ­nea IFES");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 26f));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo =
                new JLabel("Interfaz grÃ¡fica - ProgramaciÃ³n II");
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton botonVuelos = crearBoton("Vuelos");
        JButton botonPasajeros = crearBoton("Pasajeros");
        JButton botonReservas = crearBoton("Reservas");

        botonVuelos.addActionListener(
                evento -> manager.mostrarPanel(EnumPanel.VUELOS));

        botonPasajeros.addActionListener(
                evento -> manager.mostrarPanel(EnumPanel.PASAJEROS));

        botonReservas.addActionListener(
                evento -> manager.mostrarPanel(EnumPanel.RESERVAS));

        add(Box.createVerticalGlue());
        add(titulo);
        add(Box.createVerticalStrut(8));
        add(subtitulo);
        add(Box.createVerticalStrut(35));
        add(botonVuelos);
        add(Box.createVerticalStrut(12));
        add(botonPasajeros);
        add(Box.createVerticalStrut(12));
        add(botonReservas);
        add(Box.createVerticalGlue());
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(boton.getPreferredSize());
        return boton;
    }

    public PanelManager getManager() {
        return manager;
    }
}