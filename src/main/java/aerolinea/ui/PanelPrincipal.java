/**
 * @file PanelPrincipal.java
 * @brief Declares PanelPrincipal as part of the Swing or console user interface.
 * @details This source file belongs to the Programacion II academic project.
 */

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
 * Menu principal grafico.
 */
public class PanelPrincipal extends JPanel {

    private final PanelManager manager;

    public PanelPrincipal(PanelManager manager) {
        this.manager = manager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(
                30, 30, 30, 30));

        JLabel titulo =
                new JLabel("Sistema de Aerolinea IFES");

        titulo.setFont(
                titulo.getFont().deriveFont(Font.BOLD, 26f));

        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(titulo);
        add(Box.createVerticalStrut(30));

        agregarBoton(
                "Ingresar vuelo",
                EnumPanel.FORMULARIO_VUELO);

        agregarBoton(
                "Listar vuelos",
                EnumPanel.TABLA_VUELOS);

        agregarBoton(
                "Registrar pasajero",
                EnumPanel.FORMULARIO_PASAJERO);

        agregarBoton(
                "Listar pasajeros",
                EnumPanel.TABLA_PASAJEROS);

        agregarBoton(
                "Reservas",
                EnumPanel.RESERVAS);

        add(Box.createVerticalGlue());
    }

    private void agregarBoton(
            String texto,
            EnumPanel destino) {

        JButton boton = new JButton(texto);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);

        boton.addActionListener(
                evento -> manager.mostrarPanel(destino));

        add(boton);
        add(Box.createVerticalStrut(10));
    }
}
