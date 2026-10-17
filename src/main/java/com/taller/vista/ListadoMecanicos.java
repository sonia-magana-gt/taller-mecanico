package com.taller.vista;

import com.taller.dao.MecanicoDAO;
import com.taller.excepciones.DatosException;
import com.taller.modelo.Mecanico;

import java.awt.Window;

public class ListadoMecanicos extends ListadoBase {

    private final MecanicoDAO dao = new MecanicoDAO();

    public ListadoMecanicos(Window propietario) {
        super(propietario, "Mecánicos", "ID", "Nombre", "Teléfono", "Especialidad");
    }

    @Override
    protected void cargarDatos() {
        try {
            modelo.setRowCount(0);
            for (Mecanico m : dao.listar()) {
                modelo.addRow(new Object[]{m.getId(), m.getNombre(), m.getTelefono(), m.getEspecialidad()});
            }
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    @Override
    protected void nuevo() {
        new FormularioMecanico(this, null).setVisible(true);
        cargarDatos();
    }

    @Override
    protected void editar(int fila) {
        try {
            Mecanico mecanico = dao.buscarPorId(String.valueOf(modelo.getValueAt(fila, 0)));
            if (mecanico != null) {
                new FormularioMecanico(this, mecanico).setVisible(true);
            }
            cargarDatos();
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }
}
