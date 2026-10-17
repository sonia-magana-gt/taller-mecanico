package com.taller.vista;

import com.taller.dao.RepuestoDAO;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.modelo.Repuesto;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;

public class FormularioRepuesto extends JDialog {

    private final RepuestoDAO dao = new RepuestoDAO();
    private final Repuesto existente;
    private final JTextField txtId = new JTextField(20);
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtDescripcion = new JTextField();
    private final JTextField txtPrecio = new JTextField();
    private final JTextField txtCantidad = new JTextField();

    public FormularioRepuesto(Window propietario, Repuesto existente) {
        super(propietario, existente == null ? "Nuevo repuesto" : "Editar repuesto", ModalityType.APPLICATION_MODAL);
        this.existente = existente;

        JPanel campos = new JPanel(new GridLayout(0, 2, 5, 5));
        campos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        UtilVista.agregarFila(campos, "Identificador:", txtId);
        UtilVista.agregarFila(campos, "Nombre:", txtNombre);
        UtilVista.agregarFila(campos, "Descripción:", txtDescripcion);
        UtilVista.agregarFila(campos, "Precio (Q):", txtPrecio);
        UtilVista.agregarFila(campos, "Cantidad:", txtCantidad);
        add(campos, BorderLayout.CENTER);

        if (existente != null) {
            txtId.setText(existente.getId());
            txtId.setEditable(false);
            txtNombre.setText(existente.getNombre());
            txtDescripcion.setText(existente.getDescripcion());
            txtPrecio.setText(existente.getPrecio().toPlainString());
            txtCantidad.setText(String.valueOf(existente.getCantidad()));
            txtCantidad.setEditable(false);
        }

        JPanel botones = new JPanel();
        JButton botonGuardar = new JButton("Guardar");
        botonGuardar.addActionListener(e -> guardar());
        JButton botonCancelar = new JButton("Cancelar");
        botonCancelar.addActionListener(e -> dispose());
        botones.add(botonGuardar);
        botones.add(botonCancelar);
        add(botones, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(propietario);
    }

    private void guardar() {
        try {
            Repuesto repuesto = new Repuesto(txtId.getText(), txtNombre.getText(), txtDescripcion.getText(),
                    UtilVista.leerDecimal(txtPrecio.getText(), "el precio"),
                    UtilVista.leerEntero(txtCantidad.getText(), "la cantidad"));
            if (existente == null) {
                dao.insertar(repuesto);
            } else {
                dao.actualizar(repuesto);
            }
            UtilVista.mostrarInfo(this, "Repuesto guardado correctamente");
            dispose();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }
}
