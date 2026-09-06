package aerolinea.ui;

import aerolinea.dominio.EnumPanel;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Vista tabular de pasajeros.
 */
public class PanelPasajerosTabla extends JPanel {

    private final JTable tabla;

    public PanelPasajerosTabla(
            TablePasajerosModel modelo,
            PanelManager manager) {

        setLayout(new BorderLayout());

        tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT));

        JButton botonNuevo = new JButton("Registrar pasajero");
        JButton botonActualizar = new JButton("Actualizar");
        JButton botonVolver = new JButton("Principal");

        botones.add(botonNuevo);
        botones.add(botonActualizar);
        botones.add(botonVolver);

        botonNuevo.addActionListener(
                evento -> manager.mostrarPanel(
                        EnumPanel.FORMULARIO_PASAJERO));

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
