package com.taller.vista;

import com.taller.dao.OrdenDAO;
import com.taller.excepciones.DatosException;
import com.taller.modelo.EstadoOrden;
import com.taller.modelo.OrdenTrabajo;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.Window;

public class ListadoOrdenes extends ListadoBase {

    private final OrdenDAO dao = new OrdenDAO();
    private final JComboBox<Object> cmbEstado = new JComboBox<>();

    public ListadoOrdenes(Window propietario) {
        super(propietario, "Órdenes de trabajo", "No. Orden", "Fecha", "Placa", "Vehículo", "Cliente", "Mecánico",
                "Estado", "Total");
        botonNuevo.setText("Nueva orden");
        botonEditar.setText("Abrir orden");
        cmbEstado.addItem("TODAS");
        for (EstadoOrden estado : EstadoOrden.values()) {
            cmbEstado.addItem(estado);
        }
        JButton botonFiltrar = new JButton("Filtrar");
        botonFiltrar.addActionListener(e -> cargarDatos());
        panelFiltros.add(new JLabel("Estado:"));
        panelFiltros.add(cmbEstado);
        panelFiltros.add(botonFiltrar);
    }

    @Override
    protected void cargarDatos() {
        Object seleccion = cmbEstado.getSelectedItem();
        EstadoOrden filtro = seleccion instanceof EstadoOrden ? (EstadoOrden) seleccion : null;
        try {
            modelo.setRowCount(0);
            for (OrdenTrabajo o : dao.listar(filtro)) {
                modelo.addRow(new Object[]{o.getIdOrden(), o.getFechaCreacion(), o.getVehiculo().getPlaca(),
                        o.getVehiculo().getMarca() + " " + o.getVehiculo().getModelo(), o.getCliente().getNombre(),
                        o.getMecanico().getNombre(), o.getEstado(), o.getTotal()});
            }
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    @Override
    protected void nuevo() {
        new DialogoNuevaOrden(this).setVisible(true);
        cargarDatos();
    }

    @Override
    protected void editar(int fila) {
        int idOrden = (Integer) modelo.getValueAt(fila, 0);
        new DialogoDetalleOrden(this, idOrden).setVisible(true);
        cargarDatos();
    }
}
