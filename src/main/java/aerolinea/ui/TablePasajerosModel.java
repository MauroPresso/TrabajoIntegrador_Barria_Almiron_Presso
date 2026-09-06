/**
 * @file TablePasajerosModel.java
 * @brief Declares TablePasajerosModel as part of the Swing or console user interface.
 * @details This source file belongs to the Programacion II academic project.
 */

package aerolinea.ui;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.Pasajero;
import aerolinea.dominio.Persona;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Modelo de tabla Swing para pasajeros.
 */
public class TablePasajerosModel extends AbstractTableModel {

    private final String[] columnas = {
            "DNI", "Nombre", "Apellido", "Pasaporte", "Reservas"
    };

    private final Aerolinea aerolinea;

    public TablePasajerosModel(Aerolinea aerolinea) {
        if (aerolinea == null) {
            throw new IllegalArgumentException("La aerolinea no puede ser nula.");
        }

        this.aerolinea = aerolinea;
    }

    private List<Pasajero> obtenerPasajeros() {
        return aerolinea.getPersonasPorDni()
                .values()
                .stream()
                .filter(persona -> persona instanceof Pasajero)
                .map(persona -> (Pasajero) persona)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public int getRowCount() {
        return obtenerPasajeros().size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public String getColumnName(int columna) {
        return columnas[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Pasajero pasajero = obtenerPasajeros().get(fila);

        switch (columna) {
            case 0:
                return pasajero.getDni();
            case 1:
                return pasajero.getNombre();
            case 2:
                return pasajero.getApellido();
            case 3:
                return pasajero.getNumeroPasaporte().isEmpty()
                        ? "No informado"
                        : pasajero.getNumeroPasaporte();
            case 4:
                return pasajero.getVuelosReservados().size();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columna) {
        if (columna == 0 || columna == 4) {
            return Integer.class;
        }

        return String.class;
    }

    public void refrescar() {
        fireTableDataChanged();
    }
}
