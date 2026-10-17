package com.taller.vista;

import com.taller.dao.ClienteDAO;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.modelo.Cliente;
import com.taller.modelo.Persona;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;

public class FormularioCliente extends JDialog {

    private final ClienteDAO dao = new ClienteDAO();
    private final Cliente existente;
    private final JTextField txtId = new JTextField(20);
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtTelefono = new JTextField();
    private final JTextField txtDireccion = new JTextField();

    public FormularioCliente(Window propietario, Cliente existente) {
        super(propietario, existente == null ? "Nuevo cliente" : "Editar cliente", ModalityType.APPLICATION_MODAL);
        this.existente = existente;

        JPanel campos = new JPanel(new GridLayout(0, 2, 5, 5));
        campos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        UtilVista.agregarFila(campos, "Identificador:", txtId);
        UtilVista.agregarFila(campos, "Nombre:", txtNombre);
        UtilVista.agregarFila(campos, "Teléfono:", txtTelefono);
        UtilVista.agregarFila(campos, "Dirección:", txtDireccion);
        add(campos, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton botonGuardar = new JButton("Guardar");
        botonGuardar.addActionListener(e -> guardar());
        botones.add(botonGuardar);
        if (existente != null) {
            Persona persona = existente;
            add(new JLabel(persona.mostrarInformacion()), BorderLayout.NORTH);
            txtId.setText(existente.getId());
            txtId.setEditable(false);
            txtNombre.setText(existente.getNombre());
            txtTelefono.setText(existente.getTelefono());
            txtDireccion.setText(existente.getDireccion());
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
            Cliente cliente = new Cliente(txtId.getText(), txtNombre.getText(), txtTelefono.getText(),
                    txtDireccion.getText());
            if (existente == null) {
                dao.insertar(cliente);
            } else {
                dao.actualizar(cliente);
            }
            UtilVista.mostrarInfo(this, "Cliente guardado correctamente");
            dispose();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void eliminar() {
        if (!UtilVista.confirmar(this, "¿Desea eliminar este cliente?")) {
            return;
        }
        try {
            dao.eliminar(existente.getId());
            UtilVista.mostrarInfo(this, "Cliente eliminado");
            dispose();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }
}
