package aerolinea.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ImplementaciÃ³n explÃ­cita de ActionListener.
 *
 * <p>Complementa los listeners expresados mediante lambdas en la interfaz
 * grÃ¡fica y permite comparar ambas formas de programaciÃ³n orientada a eventos.</p>
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