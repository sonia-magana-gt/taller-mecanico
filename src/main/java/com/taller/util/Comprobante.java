package com.taller.util;

import com.taller.modelo.DetalleRepuesto;
import com.taller.modelo.DetalleServicio;
import com.taller.modelo.OrdenTrabajo;
import com.taller.vista.UtilVista;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;

public final class Comprobante {

    private static final int ANCHO = 50;

    private Comprobante() {
    }

    public static String generarTexto(OrdenTrabajo o) {
        String igual = "=".repeat(ANCHO);
        String guion = "-".repeat(ANCHO);
        StringBuilder sb = new StringBuilder();
        sb.append(igual).append("\n");
        sb.append(centrar("TALLER EL ÚLTIMO ARRANQUE")).append("\n");
        sb.append(centrar("COMPROBANTE DE REPARACIÓN")).append("\n");
        sb.append(igual).append("\n");
        sb.append("Orden No.: ").append(o.getIdOrden()).append("    Fecha: ").append(fechaFinal(o)).append("\n");
        sb.append("Cliente:   ").append(o.getCliente().getNombre()).append(" (ID ").append(o.getCliente().getId())
                .append(")\n");
        sb.append("Vehículo:  ").append(o.getVehiculo().getPlaca()).append(" - ").append(o.getVehiculo().getMarca())
                .append(" ").append(o.getVehiculo().getModelo()).append(" ").append(o.getVehiculo().getAnio())
                .append("\n");
        sb.append("Mecánico:  ").append(o.getMecanico().getNombre()).append("\n");
        sb.append("Problema:  ").append(o.getDescripcionProblema()).append("\n");
        sb.append(guion).append("\n");
        sb.append("SERVICIOS\n");
        for (DetalleServicio d : o.getServicios()) {
            sb.append(String.format("  %-34s %12s%n", d.getDescripcion(), q(d.getSubtotal())));
        }
        sb.append(guion).append("\n");
        sb.append("REPUESTOS\n");
        for (DetalleRepuesto d : o.getRepuestos()) {
            String texto = d.getRepuesto().getNombre() + " x" + d.getCantidad() + " (" + q(d.getPrecioUnitario()) + ")";
            sb.append(String.format("  %-34s %12s%n", texto, q(d.getSubtotal())));
        }
        sb.append(guion).append("\n");
        sb.append(String.format("%-36s %12s%n", "Total mano de obra:", q(o.calcularTotalManoObra())));
        sb.append(String.format("%-36s %12s%n", "Total repuestos:", q(o.calcularTotalRepuestos())));
        sb.append(String.format("%-36s %12s%n", "TOTAL A PAGAR:", q(o.calcularTotal())));
        sb.append(igual).append("\n");
        return sb.toString();
    }

    public static String generarHtml(OrdenTrabajo o) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">\n<title>Comprobante orden ")
                .append(o.getIdOrden()).append("</title>\n")
                .append("<style>body{font-family:Arial,sans-serif;margin:24px;max-width:700px}")
                .append("table{border-collapse:collapse;width:100%;margin-bottom:16px}")
                .append("th,td{border:1px solid #888;padding:6px 10px;text-align:left}th{background:#eee}")
                .append(".der{text-align:right}</style>\n</head>\n<body>\n");
        sb.append("<h1>Taller El Último Arranque</h1>\n<h2>Comprobante de reparación</h2>\n");
        sb.append("<p><b>Orden No.:</b> ").append(o.getIdOrden()).append(" &nbsp; <b>Fecha:</b> ")
                .append(fechaFinal(o)).append("<br>");
        sb.append("<b>Cliente:</b> ").append(escapar(o.getCliente().getNombre())).append(" (ID ")
                .append(escapar(o.getCliente().getId())).append(")<br>");
        sb.append("<b>Vehículo:</b> ").append(escapar(o.getVehiculo().toString())).append(" ")
                .append(o.getVehiculo().getAnio()).append("<br>");
        sb.append("<b>Mecánico:</b> ").append(escapar(o.getMecanico().getNombre())).append("<br>");
        sb.append("<b>Problema:</b> ").append(escapar(o.getDescripcionProblema())).append("</p>\n");
        sb.append("<h3>Servicios</h3>\n<table>\n<tr><th>Descripción</th><th class=\"der\">Costo</th></tr>\n");
        for (DetalleServicio d : o.getServicios()) {
            sb.append("<tr><td>").append(escapar(d.getDescripcion())).append("</td><td class=\"der\">")
                    .append(q(d.getSubtotal())).append("</td></tr>\n");
        }
        sb.append("</table>\n<h3>Repuestos</h3>\n<table>\n")
                .append("<tr><th>Repuesto</th><th class=\"der\">Cantidad</th><th class=\"der\">Precio</th>")
                .append("<th class=\"der\">Subtotal</th></tr>\n");
        for (DetalleRepuesto d : o.getRepuestos()) {
            sb.append("<tr><td>").append(escapar(d.getRepuesto().getNombre())).append("</td><td class=\"der\">")
                    .append(d.getCantidad()).append("</td><td class=\"der\">").append(q(d.getPrecioUnitario()))
                    .append("</td><td class=\"der\">").append(q(d.getSubtotal())).append("</td></tr>\n");
        }
        sb.append("</table>\n<p>Total mano de obra: <b>").append(q(o.calcularTotalManoObra()))
                .append("</b><br>Total repuestos: <b>").append(q(o.calcularTotalRepuestos()))
                .append("</b><br>TOTAL A PAGAR: <b>").append(q(o.calcularTotal())).append("</b></p>\n");
        sb.append("</body>\n</html>\n");
        return sb.toString();
    }

    public static void mostrar(Component padre, OrdenTrabajo o) {
        Window ventana = padre == null ? null : SwingUtilities.getWindowAncestor(padre);
        JDialog dialogo = new JDialog(ventana, "Comprobante - Orden " + o.getIdOrden(), JDialog.ModalityType.APPLICATION_MODAL);
        JTextArea area = new JTextArea(generarTexto(o));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setEditable(false);
        JButton botonHtml = new JButton("Guardar como HTML");
        botonHtml.addActionListener(e -> {
            File archivo = UtilVista.elegirArchivoParaGuardar(dialogo, "comprobante_orden_" + o.getIdOrden() + ".html");
            if (archivo != null) {
                try {
                    Files.writeString(archivo.toPath(), generarHtml(o), StandardCharsets.UTF_8);
                    UtilVista.mostrarInfo(dialogo, "Archivo guardado en " + archivo.getAbsolutePath());
                } catch (IOException ex) {
                    UtilVista.mostrarError(dialogo, "No se pudo guardar el archivo: " + ex.getMessage());
                }
            }
        });
        JButton botonCerrar = new JButton("Cerrar");
        botonCerrar.addActionListener(e -> dialogo.dispose());
        JPanel botones = new JPanel();
        botones.add(botonHtml);
        botones.add(botonCerrar);
        dialogo.add(new JScrollPane(area), BorderLayout.CENTER);
        dialogo.add(botones, BorderLayout.SOUTH);
        dialogo.setSize(560, 560);
        dialogo.setLocationRelativeTo(padre);
        dialogo.setVisible(true);
    }

    private static String fechaFinal(OrdenTrabajo o) {
        return String.valueOf(o.getFechaFinalizacion() != null ? o.getFechaFinalizacion() : o.getFechaCreacion());
    }

    private static String q(BigDecimal valor) {
        return String.format(Locale.US, "Q %,.2f", valor);
    }

    private static String centrar(String texto) {
        int espacios = Math.max(0, (ANCHO - texto.length()) / 2);
        return " ".repeat(espacios) + texto;
    }

    private static String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
