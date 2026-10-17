package com.taller.dao;

import com.taller.bd.ConexionBD;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.modelo.Mecanico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MecanicoDAO {

    public void insertar(Mecanico m) throws DatosException, ValidacionException {
        if (existe(m.getId())) {
            throw new ValidacionException("Ya existe un mecánico con el identificador " + m.getId());
        }
        String sql = "INSERT INTO mecanico (id_mecanico, nombre, telefono, especialidad) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getId());
            ps.setString(2, m.getNombre());
            ps.setString(3, m.getTelefono());
            ps.setString(4, m.getEspecialidad());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al registrar el mecánico: " + e.getMessage(), e);
        }
    }

    public void actualizar(Mecanico m) throws DatosException, ValidacionException {
        if (!existe(m.getId())) {
            throw new ValidacionException("El mecánico no existe");
        }
        String sql = "UPDATE mecanico SET nombre = ?, telefono = ?, especialidad = ? WHERE id_mecanico = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getTelefono());
            ps.setString(3, m.getEspecialidad());
            ps.setString(4, m.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al actualizar el mecánico: " + e.getMessage(), e);
        }
    }

    public void eliminar(String id) throws DatosException, ValidacionException {
        String sqlConteo = "SELECT COUNT(*) FROM orden_trabajo WHERE id_mecanico = ?";
        String sql = "DELETE FROM mecanico WHERE id_mecanico = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sqlConteo)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        throw new ValidacionException("No se puede eliminar el mecánico porque tiene órdenes asociadas");
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatosException("Error al eliminar el mecánico: " + e.getMessage(), e);
        }
    }

    public Mecanico buscarPorId(String id) throws DatosException {
        String sql = "SELECT id_mecanico, nombre, telefono, especialidad FROM mecanico WHERE id_mecanico = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException | ValidacionException e) {
            throw new DatosException("Error al buscar el mecánico: " + e.getMessage(), e);
        }
    }

    public ArrayList<Mecanico> listar() throws DatosException {
        String sql = "SELECT id_mecanico, nombre, telefono, especialidad FROM mecanico ORDER BY nombre";
        ArrayList<Mecanico> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException | ValidacionException e) {
            throw new DatosException("Error al listar los mecánicos: " + e.getMessage(), e);
        }
        return lista;
    }

    public boolean existe(String id) throws DatosException {
        String sql = "SELECT COUNT(*) FROM mecanico WHERE id_mecanico = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al verificar el mecánico: " + e.getMessage(), e);
        }
    }

    private Mecanico mapear(ResultSet rs) throws SQLException, ValidacionException {
        return new Mecanico(rs.getString("id_mecanico"), rs.getString("nombre"),
                rs.getString("telefono"), rs.getString("especialidad"));
    }
}
