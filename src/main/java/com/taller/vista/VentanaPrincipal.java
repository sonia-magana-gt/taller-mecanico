package com.taller.vista;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class VentanaPrincipal extends JFrame {

    public VentanaPrincipal() {
        super("Taller El Último Arranque");
        setSize(420, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel titulo = new JLabel("Taller El Último Arranque", SwingConstants.CENTER);
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.add(new IconoLlanta(), BorderLayout.CENTER);
        encabezado.add(titulo, BorderLayout.SOUTH);
        add(encabezado, BorderLayout.NORTH);

        JPanel botones = new JPanel(new GridLayout(0, 1, 10, 10));
        botones.setBorder(BorderFactory.createEmptyBorder(20, 60, 30, 60));
        botones.add(crearBoton("Catálogos", () -> new DialogoCatalogos(this).setVisible(true)));
        botones.add(crearBoton("Órdenes", () -> new ListadoOrdenes(this).mostrar()));
        botones.add(crearBoton("Reportes", () -> new DialogoReportes(this).setVisible(true)));
        botones.add(crearBoton("Salir", this::salir));
        add(botones, BorderLayout.CENTER);
    }

    private JButton crearBoton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    private void salir() {
        if (UtilVista.confirmar(this, "¿Desea salir del sistema?")) {
            System.exit(0);
        }
    }
}
