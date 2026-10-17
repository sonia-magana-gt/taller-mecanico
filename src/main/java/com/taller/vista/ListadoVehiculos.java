package com.taller.vista;

import com.taller.dao.ClienteDAO;
import com.taller.dao.VehiculoDAO;
import com.taller.excepciones.DatosException;
import com.taller.modelo.Cliente;
import com.taller.modelo.Vehiculo;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.Window;
import java.util.List;

public class ListadoVehiculos extends ListadoBase {

    private final VehiculoDAO dao = new VehiculoDAO();
    private final JComboBox<Object> cmbCliente = new JComboBox<>();

    public ListadoVehiculos(Window propietario) {
        super(propietario, "Vehículos", "Placa", "Marca", "Modelo", "Año", "Color", "ID Cliente");
        cmbCliente.addItem("Todos");
        try {
            for (Cliente c : new ClienteDAO().listar()) {
                cmbCliente.addItem(c);
            }
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
        JButton botonFiltrar = new JButton("Filtrar");
        botonFiltrar.addActionListener(e -> cargarDatos());
        panelFiltros.add(new JLabel("Cliente:"));
        panelFiltros.add(cmbCliente);
        panelFiltros.add(botonFiltrar);
    }

    @Override
    protected void cargarDatos() {
        try {
            Object seleccion = cmbCliente.getSelectedItem();
            List<Vehiculo> vehiculos = seleccion instanceof Cliente
                    ? dao.listarPorCliente(((Cliente) seleccion).getId())
                    : dao.listar();
            modelo.setRowCount(0);
            for (Vehiculo v : vehiculos) {
                modelo.addRow(new Object[]{v.getPlaca(), v.getMarca(), v.getModelo(), v.getAnio(), v.getColor(),
                        v.getIdCliente()});
            }
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    @Override
    protected void nuevo() {
        new FormularioVehiculo(this, null).setVisible(true);
        cargarDatos();
    }

    @Override
    protected void editar(int fila) {
        try {
            Vehiculo vehiculo = dao.buscarPorPlaca(String.valueOf(modelo.getValueAt(fila, 0)));
            if (vehiculo != null) {
                new FormularioVehiculo(this, vehiculo).setVisible(true);
            }
            cargarDatos();
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }
}
