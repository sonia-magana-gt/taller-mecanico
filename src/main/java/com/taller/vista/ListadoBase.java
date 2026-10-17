package com.taller.vista;

import com.taller.util.ExportadorTabla;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public abstract class ListadoBase extends JDialog {

    protected final DefaultTableModel modelo;
    protected final JTable tabla;
    protected final JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
    protected final JPanel panelBotones = new JPanel();
    protected final JButton botonNuevo = new JButton("Nuevo");
    protected final JButton botonEditar = new JButton("Editar");
    private final String titulo;

    protected ListadoBase(Window propietario, String titulo, String... columnas) {
        super(propietario, titulo, ModalityType.APPLICATION_MODAL);
        this.titulo = titulo;
        modelo = UtilVista.crearModelo(columnas);
        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarSeleccion();
                }
            }
        });

        botonNuevo.addActionListener(e -> nuevo());
        botonEditar.addActionListener(e -> editarSeleccion());
        JButton botonExportar = new JButton("Exportar HTML");
        botonExportar.addActionListener(e -> exportar());
        panelBotones.add(botonNuevo);
        panelBotones.add(botonEditar);
        panelBotones.add(botonExportar);

        add(panelFiltros, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
        setSize(900, 500);
        setLocationRelativeTo(propietario);
    }

    protected abstract void cargarDatos();

    protected abstract void nuevo();

    protected abstract void editar(int fila);

    public void mostrar() {
        JButton botonCerrar = new JButton("Cerrar");
        botonCerrar.addActionListener(e -> dispose());
        panelBotones.add(botonCerrar);
        cargarDatos();
        setVisible(true);
    }

    private void editarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            UtilVista.mostrarAviso(this, "Seleccione un registro de la tabla");
            return;
        }
        editar(fila);
    }

    private void exportar() {
        if (tabla.getRowCount() == 0) {
            UtilVista.mostrarAviso(this, "No hay datos para exportar");
            return;
        }
        File archivo = UtilVista.elegirArchivoParaGuardar(this, titulo.toLowerCase() + ".html");
        if (archivo == null) {
            return;
        }
        try {
            ExportadorTabla.aHtml(tabla, titulo, archivo);
            UtilVista.mostrarInfo(this, "Archivo guardado en " + archivo.getAbsolutePath());
        } catch (IOException e) {
            UtilVista.mostrarError(this, "No se pudo guardar el archivo: " + e.getMessage());
        }
    }
}
