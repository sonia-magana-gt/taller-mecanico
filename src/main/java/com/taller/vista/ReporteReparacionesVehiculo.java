package com.taller.vista;

import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.Window;

public class ReporteReparacionesVehiculo extends ReporteBase {

    private final JTextField txtPlaca = new JTextField(12);

    public ReporteReparacionesVehiculo(Window propietario) {
        super(propietario, "Reparaciones por vehículo");
        panelFiltros.add(new JLabel("Placa:"));
        panelFiltros.add(txtPlaca);
    }

    @Override
    protected DefaultTableModel generar() throws ValidacionException, DatosException {
        if (txtPlaca.getText().trim().isEmpty()) {
            throw new ValidacionException("Ingrese la placa del vehículo");
        }
        return dao.reparacionesPorVehiculo(txtPlaca.getText());
    }
}
