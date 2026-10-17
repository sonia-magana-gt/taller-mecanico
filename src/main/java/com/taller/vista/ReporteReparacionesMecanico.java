package com.taller.vista;

import com.taller.dao.MecanicoDAO;
import com.taller.excepciones.DatosException;
import com.taller.modelo.Mecanico;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import java.awt.Window;

public class ReporteReparacionesMecanico extends ReporteBase {

    private final JComboBox<Object> cmbMecanico = new JComboBox<>();

    public ReporteReparacionesMecanico(Window propietario) {
        super(propietario, "Reparaciones por mecánico");
        cmbMecanico.addItem("Todos");
        try {
            for (Mecanico m : new MecanicoDAO().listar()) {
                cmbMecanico.addItem(m);
            }
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
        panelFiltros.add(new JLabel("Mecánico:"));
        panelFiltros.add(cmbMecanico);
    }

    @Override
    protected DefaultTableModel generar() throws DatosException {
        Object seleccion = cmbMecanico.getSelectedItem();
        return dao.reparacionesPorMecanico(seleccion instanceof Mecanico ? ((Mecanico) seleccion).getId() : null);
    }
}
