package com.taller.vista;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Window;

public class DialogoCatalogos extends JDialog {

    public DialogoCatalogos(Window propietario) {
        super(propietario, "Catálogos", ModalityType.APPLICATION_MODAL);
        JPanel botones = new JPanel(new GridLayout(0, 1, 10, 10));
        botones.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        agregarBoton(botones, "Clientes", () -> new ListadoClientes(this).mostrar());
        agregarBoton(botones, "Vehículos", () -> new ListadoVehiculos(this).mostrar());
        agregarBoton(botones, "Mecánicos", () -> new ListadoMecanicos(this).mostrar());
        agregarBoton(botones, "Repuestos", () -> new ListadoRepuestos(this).mostrar());
        agregarBoton(botones, "Servicios", () -> new ListadoServicios(this).mostrar());
        agregarBoton(botones, "Volver", this::dispose);
        add(botones);
        pack();
        setLocationRelativeTo(propietario);
    }

    private void agregarBoton(JPanel panel, String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> accion.run());
        panel.add(boton);
    }
}
