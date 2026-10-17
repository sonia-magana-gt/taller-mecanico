package com.taller.dao;

import com.taller.bd.ConexionBD;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.modelo.Cliente;
import com.taller.modelo.DetalleRepuesto;
import com.taller.modelo.DetalleServicio;
import com.taller.modelo.EstadoOrden;
import com.taller.modelo.Mecanico;
import com.taller.modelo.OrdenTrabajo;
import com.taller.modelo.Repuesto;
import com.taller.modelo.Servicio;
import com.taller.modelo.Vehiculo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class OrdenDAO {

    private static final String CONSULTA_BASE =
            "SELECT o.id_orden, o.descripcion_problema, o.estado, o.fecha_creacion, o.fecha_finalizacion, o.total, "
                    + "v.placa, v.marca, v.modelo, v.anio, v.color, v.id_cliente, "
                    + "c.nombre AS cli_nombre, c.telefono AS cli_telefono, c.direccion AS cli_direccion, "
                    + "m.id_mecanico, m.nombre AS mec_nombre, m.telefono AS mec_telefono, m.especialidad "
                    + "FROM orden_trabajo o "
                    + "JOIN vehiculo v ON v.placa = o.placa "
                    + "JOIN cliente c ON c.id_cliente = v.id_cliente "
                    + "JOIN mecanico m ON m.id_mecanico = o.id_mecanico";

    private interface Operacion {
        void ejecutar(Connection con) throws SQLException, ValidacionException;
    }

    public int crear(String placa, String idMecanico, String descripcion) throws DatosException, ValidacionException {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new ValidacionException("La descripción del problema es obligatoria");
        }
        if (!new VehiculoDAO().existe(placa)) {
            throw new ValidacionException("El vehículo no existe");
        }
        if (!new MecanicoDAO().existe(idMecanico)) {
            throw new ValidacionException("El mecánico no existe");
        }
        String sql = "INSERT INTO orden_trabajo (placa, id_mecanico, descripcion_problema, estado) VALUES (?, ?, ?, 'PENDIENTE')";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"id_orden"})) {
            ps.setString(1, placa);
            ps.setString(2, idMecanico);
            ps.setString(3, descripcion.trim());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatosException("Error al crear la orden: " + e.getMessage(), e);
        }
    }

    public ArrayList<OrdenTrabajo> listar(EstadoOrden filtro) throws DatosException {
        String sql = CONSULTA_BASE + (filtro == null ? "" : " WHERE o.estado = ?") + " ORDER BY o.id_orden DESC";
        ArrayList<OrdenTrabajo> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (filtro != null) {
                ps.setString(1, filtro.name());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearOrden(rs));
                }
            }
        } catch (SQLException | ValidacionException e) {
            throw new DatosException("Error al listar las órdenes: " + e.getMessage(), e);
        }
        return lista;
    }

    public OrdenTrabajo buscarCompleta(int idOrden) throws DatosException, ValidacionException {
        try (Connection con = ConexionBD.obtenerConexion()) {
            OrdenTrabajo orden;
            try (PreparedStatement ps = con.prepareStatement(CONSULTA_BASE + " WHERE o.id_orden = ?")) {
                ps.setInt(1, idOrden);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ValidacionException("La orden no existe");
                    }
                    orden = mapearOrden(rs);
                }
            }
            String sqlServicios = "SELECT d.id_detalle, d.costo, s.id_servicio, s.descripcion, s.costo_mano_obra "
                    + "FROM orden_servicio d JOIN servicio s ON s.id_servicio = d.id_servicio "
                    + "WHERE d.id_orden = ? ORDER BY d.id_detalle";
            try (PreparedStatement ps = con.prepareStatement(sqlServicios)) {
                ps.setInt(1, idOrden);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Servicio servicio = new Servicio(rs.getString("id_servicio"), rs.getString("descripcion"),
                                rs.getBigDecimal("costo_mano_obra"));
                        DetalleServicio detalle = new DetalleServicio(servicio, rs.getBigDecimal("costo"));
                        detalle.setIdDetalle(rs.getInt("id_detalle"));
                        orden.agregarDetalle(detalle);
                    }
                }
            }
            String sqlRepuestos = "SELECT d.id_detalle, d.cantidad AS cant_usada, d.precio_unitario, "
                    + "r.id_repuesto, r.nombre, r.descripcion, r.precio, r.cantidad "
                    + "FROM orden_repuesto d JOIN repuesto r ON r.id_repuesto = d.id_repuesto "
                    + "WHERE d.id_orden = ? ORDER BY d.id_detalle";
            try (PreparedStatement ps = con.prepareStatement(sqlRepuestos)) {
                ps.setInt(1, idOrden);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Repuesto repuesto = new Repuesto(rs.getString("id_repuesto"), rs.getString("nombre"),
                                rs.getString("descripcion"), rs.getBigDecimal("precio"), rs.getInt("cantidad"));
                        DetalleRepuesto detalle = new DetalleRepuesto(repuesto, rs.getInt("cant_usada"),
                                rs.getBigDecimal("precio_unitario"));
                        detalle.setIdDetalle(rs.getInt("id_detalle"));
                        orden.agregarDetalle(detalle);
                    }
                }
            }
            return orden;
        } catch (SQLException e) {
            throw new DatosException("Error al cargar la orden: " + e.getMessage(), e);
        }
    }

    public void iniciar(int idOrden) throws DatosException, ValidacionException {
        ejecutarTransaccion("iniciar la orden", con -> {
            verificarEstado(con, idOrden, EstadoOrden.PENDIENTE);
            cambiarEstado(con, idOrden, EstadoOrden.EN_PROCESO);
        });
    }

    public void agregarServicio(int idOrden, String idServicio) throws DatosException, ValidacionException {
        ejecutarTransaccion("agregar el servicio", con -> {
            verificarEstado(con, idOrden, EstadoOrden.EN_PROCESO);
            BigDecimal costo;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT costo_mano_obra FROM servicio WHERE id_servicio = ?")) {
                ps.setString(1, idServicio);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ValidacionException("El servicio no existe");
                    }
                    costo = rs.getBigDecimal(1);
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO orden_servicio (id_orden, id_servicio, costo) VALUES (?, ?, ?)")) {
                ps.setInt(1, idOrden);
                ps.setString(2, idServicio);
                ps.setBigDecimal(3, costo);
                ps.executeUpdate();
            }
            actualizarTotal(con, idOrden);
        });
    }

    public void quitarServicio(int idOrden, int idDetalle) throws DatosException, ValidacionException {
        ejecutarTransaccion("quitar el servicio", con -> {
            verificarEstado(con, idOrden, EstadoOrden.EN_PROCESO);
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM orden_servicio WHERE id_detalle = ? AND id_orden = ?")) {
                ps.setInt(1, idDetalle);
                ps.setInt(2, idOrden);
                ps.executeUpdate();
            }
            actualizarTotal(con, idOrden);
        });
    }

    public void agregarRepuesto(int idOrden, String idRepuesto, int cantidad) throws DatosException, ValidacionException {
        if (cantidad <= 0) {
            throw new ValidacionException("La cantidad debe ser mayor que cero");
        }
        ejecutarTransaccion("asignar el repuesto", con -> {
            verificarEstado(con, idOrden, EstadoOrden.EN_PROCESO);
            int existencia;
            BigDecimal precio;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT cantidad, precio FROM repuesto WHERE id_repuesto = ? FOR UPDATE")) {
                ps.setString(1, idRepuesto);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ValidacionException("El repuesto no existe");
                    }
                    existencia = rs.getInt("cantidad");
                    precio = rs.getBigDecimal("precio");
                }
            }
            if (cantidad > existencia) {
                throw new ValidacionException("Inventario insuficiente. Disponible: " + existencia);
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE repuesto SET cantidad = cantidad - ? WHERE id_repuesto = ?")) {
                ps.setInt(1, cantidad);
                ps.setString(2, idRepuesto);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO orden_repuesto (id_orden, id_repuesto, cantidad, precio_unitario, subtotal) "
                            + "VALUES (?, ?, ?, ?, ?)")) {
                ps.setInt(1, idOrden);
                ps.setString(2, idRepuesto);
                ps.setInt(3, cantidad);
                ps.setBigDecimal(4, precio);
                ps.setBigDecimal(5, precio.multiply(BigDecimal.valueOf(cantidad)));
                ps.executeUpdate();
            }
            actualizarTotal(con, idOrden);
        });
    }

    public void quitarRepuesto(int idOrden, int idDetalle) throws DatosException, ValidacionException {
        ejecutarTransaccion("quitar el repuesto", con -> {
            verificarEstado(con, idOrden, EstadoOrden.EN_PROCESO);
            String idRepuesto;
            int cantidad;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT id_repuesto, cantidad FROM orden_repuesto WHERE id_detalle = ? AND id_orden = ?")) {
                ps.setInt(1, idDetalle);
                ps.setInt(2, idOrden);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ValidacionException("El detalle no existe");
                    }
                    idRepuesto = rs.getString("id_repuesto");
                    cantidad = rs.getInt("cantidad");
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE repuesto SET cantidad = cantidad + ? WHERE id_repuesto = ?")) {
                ps.setInt(1, cantidad);
                ps.setString(2, idRepuesto);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM orden_repuesto WHERE id_detalle = ?")) {
                ps.setInt(1, idDetalle);
                ps.executeUpdate();
            }
            actualizarTotal(con, idOrden);
        });
    }

    public void finalizar(int idOrden) throws DatosException, ValidacionException {
        ejecutarTransaccion("finalizar la orden", con -> {
            verificarEstado(con, idOrden, EstadoOrden.EN_PROCESO);
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COUNT(*) FROM orden_servicio WHERE id_orden = ?")) {
                ps.setInt(1, idOrden);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        throw new ValidacionException("No se puede finalizar una orden sin servicios registrados");
                    }
                }
            }
            actualizarTotal(con, idOrden);
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE orden_trabajo SET estado = 'FINALIZADA', fecha_finalizacion = SYSDATE WHERE id_orden = ?")) {
                ps.setInt(1, idOrden);
                ps.executeUpdate();
            }
        });
    }

    public void cancelar(int idOrden) throws DatosException, ValidacionException {
        ejecutarTransaccion("cancelar la orden", con -> {
            verificarEstado(con, idOrden, EstadoOrden.PENDIENTE, EstadoOrden.EN_PROCESO);
            try (PreparedStatement consulta = con.prepareStatement(
                    "SELECT id_repuesto, cantidad FROM orden_repuesto WHERE id_orden = ?");
                 PreparedStatement devolver = con.prepareStatement(
                         "UPDATE repuesto SET cantidad = cantidad + ? WHERE id_repuesto = ?")) {
                consulta.setInt(1, idOrden);
                try (ResultSet rs = consulta.executeQuery()) {
                    while (rs.next()) {
                        devolver.setInt(1, rs.getInt("cantidad"));
                        devolver.setString(2, rs.getString("id_repuesto"));
                        devolver.executeUpdate();
                    }
                }
            }
            cambiarEstado(con, idOrden, EstadoOrden.CANCELADA);
        });
    }

    private void ejecutarTransaccion(String accion, Operacion operacion) throws DatosException, ValidacionException {
        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try {
                operacion.ejecutar(con);
                con.commit();
            } catch (SQLException | ValidacionException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al " + accion + ": " + e.getMessage(), e);
        }
    }

    private void verificarEstado(Connection con, int idOrden, EstadoOrden... permitidos)
            throws SQLException, ValidacionException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT estado FROM orden_trabajo WHERE id_orden = ? FOR UPDATE")) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new ValidacionException("La orden no existe");
                }
                EstadoOrden actual = EstadoOrden.valueOf(rs.getString(1));
                if (!Arrays.asList(permitidos).contains(actual)) {
                    throw new ValidacionException("La orden está " + actual + " y no permite esta operación");
                }
            }
        }
    }

    private void cambiarEstado(Connection con, int idOrden, EstadoOrden nuevo) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("UPDATE orden_trabajo SET estado = ? WHERE id_orden = ?")) {
            ps.setString(1, nuevo.name());
            ps.setInt(2, idOrden);
            ps.executeUpdate();
        }
    }

    private void actualizarTotal(Connection con, int idOrden) throws SQLException {
        String sql = "UPDATE orden_trabajo SET total = "
                + "NVL((SELECT SUM(costo) FROM orden_servicio WHERE id_orden = ?), 0) + "
                + "NVL((SELECT SUM(subtotal) FROM orden_repuesto WHERE id_orden = ?), 0) "
                + "WHERE id_orden = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            ps.setInt(2, idOrden);
            ps.setInt(3, idOrden);
            ps.executeUpdate();
        }
    }

    private OrdenTrabajo mapearOrden(ResultSet rs) throws SQLException, ValidacionException {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setIdOrden(rs.getInt("id_orden"));
        orden.setDescripcionProblema(rs.getString("descripcion_problema"));
        orden.setEstado(EstadoOrden.valueOf(rs.getString("estado")));
        orden.setFechaCreacion(rs.getDate("fecha_creacion").toLocalDate());
        Date fin = rs.getDate("fecha_finalizacion");
        orden.setFechaFinalizacion(fin == null ? null : fin.toLocalDate());
        orden.setTotal(rs.getBigDecimal("total"));
        orden.setVehiculo(new Vehiculo(rs.getString("placa"), rs.getString("marca"), rs.getString("modelo"),
                rs.getInt("anio"), rs.getString("color"), rs.getString("id_cliente")));
        orden.setCliente(new Cliente(rs.getString("id_cliente"), rs.getString("cli_nombre"),
                rs.getString("cli_telefono"), rs.getString("cli_direccion")));
        orden.setMecanico(new Mecanico(rs.getString("id_mecanico"), rs.getString("mec_nombre"),
                rs.getString("mec_telefono"), rs.getString("especialidad")));
        return orden;
    }
}
