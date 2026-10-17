package com.taller.vista;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Window;

public class DialogoReportes extends JDialog {

    public DialogoReportes(Window propietario) {
        super(propietario, "Reportes", ModalityType.APPLICATION_MODAL);
        JPanel botones = new JPanel(new GridLayout(0, 1, 10, 10));
        botones.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        agregarBoton(botones, "Órdenes por estado", () -> new ReporteOrdenesPorEstado(this).mostrar());
        agregarBoton(botones, "Reparaciones por vehículo", () -> new ReporteReparacionesVehiculo(this).mostrar());
        agregarBoton(botones, "Repuestos con bajo inventario", () -> new ReporteBajoInventario(this).mostrar());
        agregarBoton(botones, "Reparaciones por mecánico", () -> new ReporteReparacionesMecanico(this).mostrar());
        agregarBoton(botones, "Ingresos por reparaciones", () -> new ReporteIngresos(this).mostrar());
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
