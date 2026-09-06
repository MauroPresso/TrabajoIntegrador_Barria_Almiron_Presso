/**
 * @file PanelVuelosTabla.java
 * @brief Declares PanelVuelosTabla as part of the Swing or console user interface.
 * @details This source file belongs to the Programacion II academic project.
 */

package aerolinea.ui;

import aerolinea.dominio.EnumPanel;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Vista tabular de vuelos.
 */
public class PanelVuelosTabla extends JPanel {

    private final JTable tabla;

    public PanelVuelosTabla(
            TableVuelosModel modelo,
            PanelManager manager) {

        setLayout(new BorderLayout());

        tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT));

        JButton botonNuevo = new JButton("Nuevo vuelo");
        JButton botonActualizar = new JButton("Actualizar");
        JButton botonVolver = new JButton("Principal");

        botones.add(botonNuevo);
        botones.add(botonActualizar);
        botones.add(botonVolver);

        botonNuevo.addActionListener(
                evento -> manager.mostrarPanel(
                        EnumPanel.FORMULARIO_VUELO));

        botonActualizar.addActionListener(
                evento -> modelo.refrescar());

        botonVolver.addActionListener(
                evento -> manager.mostrarPanel(
                        EnumPanel.PRINCIPAL));

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    public JTable getTabla() {
        return tabla;
    }
}
