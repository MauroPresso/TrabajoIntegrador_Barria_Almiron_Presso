package aerolinea.ui;

import aerolinea.dominio.Aerolinea;
import aerolinea.dominio.Vuelo;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Modelo de tabla Swing para representar vuelos.
 *
 * <p>AbstractTableModel adapta objetos del dominio a filas y columnas
 * consumibles por JTable.</p>
 */
public class TableVuelosModel extends AbstractTableModel {

    private final String[] columnas = {
            "Numero", "Tipo", "Origen", "Destino",
            "Fecha", "Estado", "Ocupados", "Capacidad"
    };

    private final Aerolinea aerolinea;

    public TableVuelosModel(Aerolinea aerolinea) {
        if (aerolinea == null) {
            throw new IllegalArgumentException("La aerolinea no puede ser nula.");
        }

        this.aerolinea = aerolinea;
    }

    @Override
    public int getRowCount() {
        return aerolinea.getVuelos().size();
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
        List<Vuelo> vuelos = aerolinea.getVuelos();
        Vuelo vuelo = vuelos.get(fila);

        switch (columna) {
            case 0:
                return vuelo.getNumero();
            case 1:
                return vuelo.getTipo();
            case 2:
                return vuelo.getOrigen();
            case 3:
                return vuelo.getDestino();
            case 4:
                return vuelo.getFecha();
            case 5:
                return vuelo.getEstado();
            case 6:
                return vuelo.getAsientosOcupados();
            case 7:
                return vuelo.getCapacidad();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columna) {
        if (columna == 6 || columna == 7) {
            return Integer.class;
        }

        return Object.class;
    }

    public void refrescar() {
        fireTableDataChanged();
    }
}
