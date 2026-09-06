package aerolinea.test;

import javax.swing.JButton;

/**
 * Laboratorio de programacion orientada a eventos.
 */
public class TestManejadorDeEventos {

    public static void main(String[] args) {

        System.setProperty(
                "java.awt.headless",
                "true");

        JButton boton =
                new JButton("Confirmar reserva");

        ManejadorDeEventos manejador =
                new ManejadorDeEventos();

        /*
         * Forma clasica:
         * una clase implementa ActionListener y se registra como listener.
         */
        boton.addActionListener(manejador);

        boton.doClick();

        if (!"Confirmar reserva".equals(
                manejador.getUltimoComando())) {

            throw new IllegalStateException(
                    "El ActionListener no recibio el evento esperado.");
        }

        /*
         * El proyecto tambien utiliza la forma lambda:
         *
         * boton.addActionListener(
         *     evento -> System.out.println(evento.getActionCommand())
         * );
         */

        System.out.println(
                "ActionListener explicito: OK");

        System.out.println(
                "TestManejadorDeEventos finalizado correctamente.");
    }
}
