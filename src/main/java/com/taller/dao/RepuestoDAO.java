package com.taller.dao;

import com.taller.bd.ConexionBD;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.modelo.Repuesto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RepuestoDAO {

    private static final String COLUMNAS = "SELECT id_repuesto, nombre, descripcion, precio, cantidad FROM repuesto";

    public void insertar(Repuesto r) throws DatosException, ValidacionException {
        if (buscarPorId(r.getId()) != null) {
            throw new ValidacionException("Ya existe un repuesto con el identificador " + r.getId());
        }
        String sql = "INSERT INTO repuesto (id_repuesto, nombre, descripcion, precio, cantidad) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getId());
            ps.setString(2, r.getNombre());
            ps.setString(3, r.getDescripcion());
            ps.setBigDecimal(4, r.getPrecio());
            ps.setInt(5, r.getCantidad());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al registrar el repuesto: " + e.getMessage(), e);
        }
    }

    public void actualizar(Repuesto r) throws DatosException, ValidacionException {
        if (buscarPorId(r.getId()) == null) {
            throw new ValidacionException("El repuesto no existe");
        }
        String sql = "UPDATE repuesto SET nombre = ?, descripcion = ?, precio = ? WHERE id_repuesto = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getNombre());
            ps.setString(2, r.getDescripcion());
            ps.setBigDecimal(3, r.getPrecio());
            ps.setString(4, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al actualizar el repuesto: " + e.getMessage(), e);
        }
    }

    public Repuesto buscarPorId(String id) throws DatosException {
        String sql = COLUMNAS + " WHERE id_repuesto = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException | ValidacionException e) {
            throw new DatosException("Error al buscar el repuesto: " + e.getMessage(), e);
        }
    }

    public ArrayList<Repuesto> listar() throws DatosException {
        return consultar(COLUMNAS + " ORDER BY nombre");
    }

    public ArrayList<Repuesto> listarAgotados() throws DatosException {
        return consultar(COLUMNAS + " WHERE cantidad = 0 ORDER BY nombre");
    }

    public void abastecer(String idRepuesto, int cantidad) throws DatosException, ValidacionException {
        if (cantidad <= 0) {
            throw new ValidacionException("La cantidad a abastecer debe ser mayor que cero");
        }
        try (Connection con = ConexionBD.obtenerConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(
                    "UPDATE repuesto SET cantidad = cantidad + ? WHERE id_repuesto = ?");
                 PreparedStatement ps2 = con.prepareStatement(
                         "INSERT INTO abastecimiento (id_repuesto, cantidad) VALUES (?, ?)")) {
                ps1.setInt(1, cantidad);
                ps1.setString(2, idRepuesto);
                if (ps1.executeUpdate() == 0) {
                    throw new ValidacionException("El repuesto no existe");
                }
                ps2.setString(1, idRepuesto);
                ps2.setInt(2, cantidad);
                ps2.executeUpdate();
                con.commit();
            } catch (SQLException | ValidacionException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al abastecer: " + e.getMessage(), e);
        }
    }

    private ArrayList<Repuesto> consultar(String sql) throws DatosException {
        ArrayList<Repuesto> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException | ValidacionException e) {
            throw new DatosException("Error al listar los repuestos: " + e.getMessage(), e);
        }
        return lista;
    }

    private Repuesto mapear(ResultSet rs) throws SQLException, ValidacionException {
        return new Repuesto(rs.getString("id_repuesto"), rs.getString("nombre"), rs.getString("descripcion"),
                rs.getBigDecimal("precio"), rs.getInt("cantidad"));
    }
}
