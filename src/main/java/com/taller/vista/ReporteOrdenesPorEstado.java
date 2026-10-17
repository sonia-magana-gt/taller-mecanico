package com.taller.vista;

import com.taller.excepciones.DatosException;
import com.taller.modelo.EstadoOrden;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import java.awt.Window;

public class ReporteOrdenesPorEstado extends ReporteBase {

    private final JComboBox<Object> cmbEstado = new JComboBox<>();

    public ReporteOrdenesPorEstado(Window propietario) {
        super(propietario, "Órdenes por estado");
        cmbEstado.addItem("TODAS");
        for (EstadoOrden estado : EstadoOrden.values()) {
            cmbEstado.addItem(estado);
        }
        panelFiltros.add(new JLabel("Estado:"));
        panelFiltros.add(cmbEstado);
    }

    @Override
    protected DefaultTableModel generar() throws DatosException {
        Object seleccion = cmbEstado.getSelectedItem();
        return dao.ordenesPorEstado(seleccion instanceof EstadoOrden ? (EstadoOrden) seleccion : null);
    }
}
