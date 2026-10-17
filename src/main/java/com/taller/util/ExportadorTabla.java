package com.taller.util;

import javax.swing.JTable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class ExportadorTabla {

    private ExportadorTabla() {
    }

    public static void aHtml(JTable tabla, String titulo, File archivo) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">\n<title>")
                .append(escapar(titulo)).append("</title>\n")
                .append("<style>body{font-family:Arial,sans-serif;margin:24px}table{border-collapse:collapse}")
                .append("th,td{border:1px solid #888;padding:6px 10px}th{background:#eee}</style>\n")
                .append("</head>\n<body>\n<h1>").append(escapar(titulo)).append("</h1>\n<table>\n<tr>");
        for (int c = 0; c < tabla.getColumnCount(); c++) {
            sb.append("<th>").append(escapar(tabla.getColumnName(c))).append("</th>");
        }
        sb.append("</tr>\n");
        for (int f = 0; f < tabla.getRowCount(); f++) {
            sb.append("<tr>");
            for (int c = 0; c < tabla.getColumnCount(); c++) {
                Object valor = tabla.getValueAt(f, c);
                sb.append("<td>").append(escapar(valor == null ? "" : valor.toString())).append("</td>");
            }
            sb.append("</tr>\n");
        }
        sb.append("</table>\n</body>\n</html>\n");
        Files.writeString(archivo.toPath(), sb.toString(), StandardCharsets.UTF_8);
    }

    private static String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
