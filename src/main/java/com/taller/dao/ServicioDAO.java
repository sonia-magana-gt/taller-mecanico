package com.taller.dao;

import com.taller.bd.ConexionBD;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.modelo.Servicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServicioDAO {

    private static final String COLUMNAS = "SELECT id_servicio, descripcion, costo_mano_obra FROM servicio";

    public void insertar(Servicio s) throws DatosException, ValidacionException {
        if (buscarPorId(s.getId()) != null) {
            throw new ValidacionException("Ya existe un servicio con el identificador " + s.getId());
        }
        String sql = "INSERT INTO servicio (id_servicio, descripcion, costo_mano_obra) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getId());
            ps.setString(2, s.getDescripcion());
            ps.setBigDecimal(3, s.getCostoManoObra());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al registrar el servicio: " + e.getMessage(), e);
        }
    }

    public void actualizar(Servicio s) throws DatosException, ValidacionException {
        if (buscarPorId(s.getId()) == null) {
            throw new ValidacionException("El servicio no existe");
        }
        String sql = "UPDATE servicio SET descripcion = ?, costo_mano_obra = ? WHERE id_servicio = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getDescripcion());
            ps.setBigDecimal(2, s.getCostoManoObra());
            ps.setString(3, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al actualizar el servicio: " + e.getMessage(), e);
        }
    }

    public void eliminar(String id) throws DatosException, ValidacionException {
        String sqlConteo = "SELECT COUNT(*) FROM orden_servicio WHERE id_servicio = ?";
        String sql = "DELETE FROM servicio WHERE id_servicio = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sqlConteo)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        throw new ValidacionException("No se puede eliminar un servicio usado en órdenes");
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatosException("Error al eliminar el servicio: " + e.getMessage(), e);
        }
    }

    public Servicio buscarPorId(String id) throws DatosException {
        String sql = COLUMNAS + " WHERE id_servicio = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException | ValidacionException e) {
            throw new DatosException("Error al buscar el servicio: " + e.getMessage(), e);
        }
    }

    public ArrayList<Servicio> listar() throws DatosException {
        ArrayList<Servicio> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(COLUMNAS + " ORDER BY descripcion");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException | ValidacionException e) {
            throw new DatosException("Error al listar los servicios: " + e.getMessage(), e);
        }
        return lista;
    }

    private Servicio mapear(ResultSet rs) throws SQLException, ValidacionException {
        return new Servicio(rs.getString("id_servicio"), rs.getString("descripcion"),
                rs.getBigDecimal("costo_mano_obra"));
    }
}
