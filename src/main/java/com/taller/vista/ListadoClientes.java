package com.taller.vista;

import com.taller.dao.ClienteDAO;
import com.taller.excepciones.DatosException;
import com.taller.modelo.Cliente;

import java.awt.Window;

public class ListadoClientes extends ListadoBase {

    private final ClienteDAO dao = new ClienteDAO();

    public ListadoClientes(Window propietario) {
        super(propietario, "Clientes", "ID", "Nombre", "Teléfono", "Dirección");
    }

    @Override
    protected void cargarDatos() {
        try {
            modelo.setRowCount(0);
            for (Cliente c : dao.listar()) {
                modelo.addRow(new Object[]{c.getId(), c.getNombre(), c.getTelefono(), c.getDireccion()});
            }
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    @Override
    protected void nuevo() {
        new FormularioCliente(this, null).setVisible(true);
        cargarDatos();
    }

    @Override
    protected void editar(int fila) {
        try {
            Cliente cliente = dao.buscarPorId(String.valueOf(modelo.getValueAt(fila, 0)));
            if (cliente != null) {
                new FormularioCliente(this, cliente).setVisible(true);
            }
            cargarDatos();
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }
}
