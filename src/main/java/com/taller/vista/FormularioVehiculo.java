package com.taller.vista;

import com.taller.dao.ClienteDAO;
import com.taller.dao.VehiculoDAO;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.modelo.Cliente;
import com.taller.modelo.Vehiculo;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;

public class FormularioVehiculo extends JDialog {

    private final VehiculoDAO dao = new VehiculoDAO();
    private final Vehiculo existente;
    private final JTextField txtPlaca = new JTextField(20);
    private final JTextField txtMarca = new JTextField();
    private final JTextField txtModelo = new JTextField();
    private final JTextField txtAnio = new JTextField();
    private final JTextField txtColor = new JTextField();
    private final JComboBox<Cliente> cmbCliente = new JComboBox<>();

    public FormularioVehiculo(Window propietario, Vehiculo existente) {
        super(propietario, existente == null ? "Nuevo vehículo" : "Editar vehículo", ModalityType.APPLICATION_MODAL);
        this.existente = existente;

        try {
            for (Cliente c : new ClienteDAO().listar()) {
                cmbCliente.addItem(c);
                if (existente != null && c.getId().equals(existente.getIdCliente())) {
                    cmbCliente.setSelectedItem(c);
                }
            }
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }

        JPanel campos = new JPanel(new GridLayout(0, 2, 5, 5));
        campos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        UtilVista.agregarFila(campos, "Placa:", txtPlaca);
        UtilVista.agregarFila(campos, "Marca:", txtMarca);
        UtilVista.agregarFila(campos, "Modelo:", txtModelo);
        UtilVista.agregarFila(campos, "Año:", txtAnio);
        UtilVista.agregarFila(campos, "Color:", txtColor);
        UtilVista.agregarFila(campos, "Cliente:", cmbCliente);
        add(campos, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton botonGuardar = new JButton("Guardar");
        botonGuardar.addActionListener(e -> guardar());
        botones.add(botonGuardar);
        if (existente != null) {
            txtPlaca.setText(existente.getPlaca());
            txtPlaca.setEditable(false);
            txtMarca.setText(existente.getMarca());
            txtModelo.setText(existente.getModelo());
            txtAnio.setText(String.valueOf(existente.getAnio()));
            txtColor.setText(existente.getColor());
            JButton botonEliminar = new JButton("Eliminar");
            botonEliminar.addActionListener(e -> eliminar());
            botones.add(botonEliminar);
        }
        JButton botonCancelar = new JButton("Cancelar");
        botonCancelar.addActionListener(e -> dispose());
        botones.add(botonCancelar);
        add(botones, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(propietario);
    }

    private void guardar() {
        try {
            Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
            if (cliente == null) {
                throw new ValidacionException("Debe seleccionar un cliente");
            }
            Vehiculo vehiculo = new Vehiculo(txtPlaca.getText(), txtMarca.getText(), txtModelo.getText(),
                    UtilVista.leerEntero(txtAnio.getText(), "el año"), txtColor.getText(), cliente.getId());
            if (existente == null) {
                dao.insertar(vehiculo);
            } else {
                dao.actualizar(vehiculo);
            }
            UtilVista.mostrarInfo(this, "Vehículo guardado correctamente");
            dispose();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void eliminar() {
        if (!UtilVista.confirmar(this, "¿Desea eliminar este vehículo?")) {
            return;
        }
        try {
            dao.eliminar(existente.getPlaca());
            UtilVista.mostrarInfo(this, "Vehículo eliminado");
            dispose();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }
}
