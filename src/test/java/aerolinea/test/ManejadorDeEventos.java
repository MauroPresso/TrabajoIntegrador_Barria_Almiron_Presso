/**
 * @file ManejadorDeEventos.java
 * @brief Declares ManejadorDeEventos as an executable Programacion II laboratory or integration check.
 * @details This source file belongs to the Programacion II academic project.
 */

package aerolinea.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Implementacion explicita de ActionListener.
 *
 * <p>Complementa los listeners expresados mediante lambdas en la interfaz
 * grafica y permite comparar ambas formas de programacion orientada a eventos.</p>
 */
public class ManejadorDeEventos implements ActionListener {

    private String ultimoComando;

    @Override
    public void actionPerformed(ActionEvent evento) {
        ultimoComando = evento.getActionCommand();

        System.out.println(
                "Evento disparado: " + ultimoComando);
    }

    public String getUltimoComando() {
        return ultimoComando;
    }
}
