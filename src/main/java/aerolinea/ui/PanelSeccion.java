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
 * Panel simple reutilizable para las secciones iniciales de la GUI.
 *
 * <p>En la Etapa 5B estos paneles serÃ¡n reemplazados o ampliados por
 * formularios y tablas concretas.</p>
 */
public class PanelSeccion extends JPanel {

    public PanelSeccion(String titulo, String descripcion, PanelManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("El PanelManager no puede ser nulo.");
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel etiquetaTitulo = new JLabel(titulo);
        etiquetaTitulo.setFont(etiquetaTitulo.getFont().deriveFont(Font.BOLD, 24f));
        etiquetaTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel etiquetaDescripcion = new JLabel(descripcion);
        etiquetaDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton botonVolver = new JButton("Volver al principal");
        botonVolver.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ProgramaciÃ³n orientada a eventos mediante expresiÃ³n lambda.
        botonVolver.addActionListener(
                evento -> manager.mostrarPanel(EnumPanel.PRINCIPAL));

        add(Box.createVerticalGlue());
        add(etiquetaTitulo);
        add(Box.createVerticalStrut(15));
        add(etiquetaDescripcion);
        add(Box.createVerticalStrut(25));
        add(botonVolver);
        add(Box.createVerticalGlue());
    }
}