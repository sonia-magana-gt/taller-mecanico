package com.taller.dao;

import com.taller.bd.ConexionBD;
import com.taller.excepciones.DatosException;
import com.taller.modelo.EstadoOrden;

import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReporteDAO {

    private static final String ORDENES_BASE =
            "SELECT o.id_orden, TO_CHAR(o.fecha_creacion, 'YYYY-MM-DD') AS fecha, o.placa, "
                    + "v.marca || ' ' || v.modelo AS vehiculo, c.nombre AS cliente, m.nombre AS mecanico, "
                    + "o.estado, o.total "
                    + "FROM orden_trabajo o "
                    + "JOIN vehiculo v ON v.placa = o.placa "
                    + "JOIN cliente c ON c.id_cliente = v.id_cliente "
                    + "JOIN mecanico m ON m.id_mecanico = o.id_mecanico";

    public DefaultTableModel ordenesPorEstado(EstadoOrden estado) throws DatosException {
        String[] columnas = {"No. Orden", "Fecha", "Placa", "Vehículo", "Cliente", "Mecánico", "Estado", "Total"};
        if (estado == null) {
            return ejecutar(ORDENES_BASE + " ORDER BY o.id_orden DESC", columnas);
        }
        return ejecutar(ORDENES_BASE + " WHERE o.estado = ? ORDER BY o.id_orden DESC", columnas, estado.name());
    }

    public DefaultTableModel reparacionesPorVehiculo(String placa) throws DatosException {
        String sql = "SELECT o.id_orden, TO_CHAR(o.fecha_creacion, 'YYYY-MM-DD') AS fecha, "
                + "o.descripcion_problema, m.nombre AS mecanico, o.estado, o.total "
                + "FROM orden_trabajo o JOIN mecanico m ON m.id_mecanico = o.id_mecanico "
                + "WHERE o.placa = ? ORDER BY o.id_orden DESC";
        String[] columnas = {"No. Orden", "Fecha", "Problema", "Mecánico", "Estado", "Total"};
        return ejecutar(sql, columnas, placa.trim().toUpperCase());
    }

    public DefaultTableModel repuestosBajoInventario() throws DatosException {
        String sql = "SELECT id_repuesto, nombre, descripcion, precio, cantidad FROM repuesto "
                + "WHERE cantidad <= 5 ORDER BY cantidad, nombre";
        String[] columnas = {"ID", "Repuesto", "Descripción", "Precio", "Existencia"};
        return ejecutar(sql, columnas);
    }

    public DefaultTableModel reparacionesPorMecanico(String idMecanico) throws DatosException {
        String sql = "SELECT m.nombre AS mecanico, o.id_orden, TO_CHAR(o.fecha_creacion, 'YYYY-MM-DD') AS fecha, "
                + "o.placa, o.descripcion_problema, o.estado, o.total "
                + "FROM orden_trabajo o JOIN mecanico m ON m.id_mecanico = o.id_mecanico";
        String[] columnas = {"Mecánico", "No. Orden", "Fecha", "Placa", "Problema", "Estado", "Total"};
        if (idMecanico == null) {
            return ejecutar(sql + " ORDER BY m.nombre, o.id_orden", columnas);
        }
        return ejecutar(sql + " WHERE m.id_mecanico = ? ORDER BY o.id_orden", columnas, idMecanico);
    }

    public DefaultTableModel ingresos(LocalDate desde, LocalDate hasta) throws DatosException {
        String sql = "SELECT o.id_orden, TO_CHAR(o.fecha_finalizacion, 'YYYY-MM-DD') AS fecha, o.placa, "
                + "NVL((SELECT SUM(s.costo) FROM orden_servicio s WHERE s.id_orden = o.id_orden), 0) AS mano_obra, "
                + "NVL((SELECT SUM(r.subtotal) FROM orden_repuesto r WHERE r.id_orden = o.id_orden), 0) AS repuestos, "
                + "o.total "
                + "FROM orden_trabajo o "
                + "WHERE o.estado = 'FINALIZADA' AND o.fecha_finalizacion >= ? AND o.fecha_finalizacion < ? "
                + "ORDER BY o.fecha_finalizacion, o.id_orden";
        String[] columnas = {"No. Orden", "Fecha", "Placa", "Mano de obra", "Repuestos", "Total"};
        DefaultTableModel modelo = ejecutar(sql, columnas, Date.valueOf(desde), Date.valueOf(hasta.plusDays(1)));
        BigDecimal manoObra = BigDecimal.ZERO;
        BigDecimal repuestos = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < modelo.getRowCount(); i++) {
            manoObra = manoObra.add((BigDecimal) modelo.getValueAt(i, 3));
            repuestos = repuestos.add((BigDecimal) modelo.getValueAt(i, 4));
            total = total.add((BigDecimal) modelo.getValueAt(i, 5));
        }
        modelo.addRow(new Object[]{"TOTAL GENERAL", "", "", manoObra, repuestos, total});
        return modelo;
    }

    private DefaultTableModel ejecutar(String sql, String[] columnas, Object... parametros) throws DatosException {
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                ps.setObject(i + 1, parametros[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                int total = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] fila = new Object[total];
                    for (int i = 0; i < total; i++) {
                        fila[i] = rs.getObject(i + 1);
                    }
                    modelo.addRow(fila);
                }
            }
        } catch (SQLException e) {
            throw new DatosException("Error al generar el reporte: " + e.getMessage(), e);
        }
        return modelo;
    }
}
