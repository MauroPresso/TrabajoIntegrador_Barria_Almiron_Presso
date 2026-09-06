package aerolinea.test;

import aerolinea.util.CalculadoraTarifas;

/**
 * Laboratorio de sobrecarga de metodos.
 */
public class TestSobrecargaMetodos {

    public static void main(String[] args) {

        CalculadoraTarifas calculadora = new CalculadoraTarifas();

        double grupo = calculadora.calcularTotal(125000.0, 3);
        double grupoConTasa = calculadora.calcularTotal(125000.0, 3, 12.5);
        double importesIndividuales = calculadora.calcularTotal(
                100000.0,
                125000.0,
                98000.0,
                150000.0);

        System.out.println("Tarifa x cantidad: " + grupo);
        System.out.println("Tarifa x cantidad + porcentaje: " + grupoConTasa);
        System.out.println("Varargs de importes: " + importesIndividuales);

        if (grupo != 375000.0) {
            throw new IllegalStateException("Resultado inesperado en sobrecarga de 2 parametros.");
        }

        if (importesIndividuales != 473000.0) {
            throw new IllegalStateException("Resultado inesperado en sobrecarga varargs.");
        }

        System.out.println("TestSobrecargaMetodos finalizado correctamente.");
    }
}
