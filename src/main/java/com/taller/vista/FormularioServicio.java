package com.taller.vista;

import com.taller.dao.ServicioDAO;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.modelo.Servicio;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;

public class FormularioServicio extends JDialog {

    private final ServicioDAO dao = new ServicioDAO();
    private final Servicio existente;
    private final JTextField txtId = new JTextField(20);
    private final JTextField txtDescripcion = new JTextField();
    private final JTextField txtCosto = new JTextField();

    public FormularioServicio(Window propietario, Servicio existente) {
        super(propietario, existente == null ? "Nuevo servicio" : "Editar servicio", ModalityType.APPLICATION_MODAL);
        this.existente = existente;

        JPanel campos = new JPanel(new GridLayout(0, 2, 5, 5));
        campos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        UtilVista.agregarFila(campos, "Identificador:", txtId);
        UtilVista.agregarFila(campos, "Descripción:", txtDescripcion);
        UtilVista.agregarFila(campos, "Costo de mano de obra (Q):", txtCosto);
        add(campos, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton botonGuardar = new JButton("Guardar");
        botonGuardar.addActionListener(e -> guardar());
        botones.add(botonGuardar);
        if (existente != null) {
            txtId.setText(existente.getId());
            txtId.setEditable(false);
            txtDescripcion.setText(existente.getDescripcion());
            txtCosto.setText(existente.getCostoManoObra().toPlainString());
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
            Servicio servicio = new Servicio(txtId.getText(), txtDescripcion.getText(),
                    UtilVista.leerDecimal(txtCosto.getText(), "el costo de mano de obra"));
            if (existente == null) {
                dao.insertar(servicio);
            } else {
                dao.actualizar(servicio);
            }
            UtilVista.mostrarInfo(this, "Servicio guardado correctamente");
            dispose();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void eliminar() {
        if (!UtilVista.confirmar(this, "¿Desea eliminar este servicio?")) {
            return;
        }
        try {
            dao.eliminar(existente.getId());
            UtilVista.mostrarInfo(this, "Servicio eliminado");
            dispose();
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }
}
