package com.taller.vista;

import com.taller.excepciones.DatosException;

import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import java.awt.Window;

public class ReporteBajoInventario extends ReporteBase {

    public ReporteBajoInventario(Window propietario) {
        super(propietario, "Repuestos con bajo inventario");
        panelFiltros.add(new JLabel("Muestra los repuestos con existencia de 5 o menos"));
        generarAlAbrir = true;
    }

    @Override
    protected DefaultTableModel generar() throws DatosException {
        return dao.repuestosBajoInventario();
    }
}
