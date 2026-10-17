package com.taller.vista;

import com.taller.dao.ReporteDAO;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.util.ExportadorTabla;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeParseException;

public abstract class ReporteBase extends JDialog {

    protected final ReporteDAO dao = new ReporteDAO();
    protected final JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
    protected final JTable tabla = new JTable(UtilVista.crearModelo());
    protected boolean generarAlAbrir = false;
    private final String titulo;

    protected ReporteBase(Window propietario, String titulo) {
        super(propietario, titulo, ModalityType.APPLICATION_MODAL);
        this.titulo = titulo;

        JButton botonGenerar = new JButton("Generar");
        botonGenerar.addActionListener(e -> generarReporte());
        JButton botonExportar = new JButton("Exportar HTML");
        botonExportar.addActionListener(e -> exportar());
        JButton botonCerrar = new JButton("Cerrar");
        botonCerrar.addActionListener(e -> dispose());
        JPanel botones = new JPanel();
        botones.add(botonGenerar);
        botones.add(botonExportar);
        botones.add(botonCerrar);

        add(panelFiltros, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
        setSize(900, 500);
        setLocationRelativeTo(propietario);
    }

    protected abstract DefaultTableModel generar() throws ValidacionException, DatosException;

    public void mostrar() {
        if (generarAlAbrir) {
            generarReporte();
        }
        setVisible(true);
    }

    private void generarReporte() {
        try {
            tabla.setModel(generar());
        } catch (DateTimeParseException e) {
            UtilVista.mostrarAviso(this, "Formato de fecha inválido (use AAAA-MM-DD)");
        } catch (ValidacionException e) {
            UtilVista.mostrarAviso(this, e.getMessage());
        } catch (DatosException e) {
            UtilVista.mostrarError(this, e.getMessage());
        }
    }

    private void exportar() {
        if (tabla.getColumnCount() == 0) {
            UtilVista.mostrarAviso(this, "Primero genere el reporte");
            return;
        }
        File archivo = UtilVista.elegirArchivoParaGuardar(this, titulo.toLowerCase().replace(" ", "_") + ".html");
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
