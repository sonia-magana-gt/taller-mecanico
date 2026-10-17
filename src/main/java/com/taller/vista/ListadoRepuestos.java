package com.taller.vista;

import com.taller.dao.RepuestoDAO;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.modelo.Repuesto;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Window;
import java.util.List;

public class ListadoRepuestos extends ListadoBase {

    private final RepuestoDAO dao = new RepuestoDAO();
    private final JComboBox<String> cmbFiltro = new JComboBox<>(new String[]{"Todos", "Agotados"});

    public ListadoRepuestos(Window propietario) {
        super(propietario, "Repuestos", "ID", "Nombre", "Descripción", "Precio", "Cantidad");
        JButton botonFiltrar = new JButton("Filtrar");
        botonFiltrar.addActionListener(e -> cargarDatos());
        panelFiltros.add(new JLabel("Mostrar:"));
        panelFiltros.add(cmbFiltro);
        panelFiltros.add(botonFiltrar);

        JButton botonAbastecer = new JButton("Abastecer");
        botonAbastecer.addActionListener(e -> abastecer());
        panelBotones.add(botonAbastecer);
    }

    @Override
    protected void cargarDatos() {
        try {
            List<Repuesto> repuestos = "Agotados".equals(cmbFiltro.getSelectedItem())
                    ? dao.listarAgotados()
                    : dao.listar();
            modelo.setRowCount(0);
            for (Repuesto r : repuestos) {
                modelo.addRow(new Object[]{r.getId(), r.getNombre(), r.getDescripcion(), r.getPrecio(),
                        r.getCantidad()});
            }
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    @Override
    protected void nuevo() {
        new FormularioRepuesto(this, null).setVisible(true);
        cargarDatos();
    }

    @Override
    protected void editar(int fila) {
        try {
            Repuesto repuesto = dao.buscarPorId(String.valueOf(modelo.getValueAt(fila, 0)));
            if (repuesto != null) {
                new FormularioRepuesto(this, repuesto).setVisible(true);
            }
            cargarDatos();
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void abastecer() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            UtilVista.mostrarAviso(this, "Seleccione un repuesto de la tabla");
            return;
        }
        String id = String.valueOf(modelo.getValueAt(fila, 0));
        String texto = JOptionPane.showInputDialog(this, "Cantidad a abastecer para " + id + ":");
        if (texto == null) {
            return;
        }
        try {
            dao.abastecer(id, UtilVista.leerEntero(texto, "la cantidad"));
            UtilVista.mostrarInfo(this, "Inventario actualizado");
            cargarDatos();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }
}
