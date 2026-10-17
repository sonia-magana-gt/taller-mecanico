package com.taller.vista;

import com.taller.dao.ServicioDAO;
import com.taller.excepciones.DatosException;
import com.taller.modelo.Servicio;

import java.awt.Window;

public class ListadoServicios extends ListadoBase {

    private final ServicioDAO dao = new ServicioDAO();

    public ListadoServicios(Window propietario) {
        super(propietario, "Servicios", "ID", "Descripción", "Costo mano de obra");
    }

    @Override
    protected void cargarDatos() {
        try {
            modelo.setRowCount(0);
            for (Servicio s : dao.listar()) {
                modelo.addRow(new Object[]{s.getId(), s.getDescripcion(), s.getCostoManoObra()});
            }
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    @Override
    protected void nuevo() {
        new FormularioServicio(this, null).setVisible(true);
        cargarDatos();
    }

    @Override
    protected void editar(int fila) {
        try {
            Servicio servicio = dao.buscarPorId(String.valueOf(modelo.getValueAt(fila, 0)));
            if (servicio != null) {
                new FormularioServicio(this, servicio).setVisible(true);
            }
            cargarDatos();
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }
}
