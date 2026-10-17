package com.taller.dao;

import com.taller.bd.ConexionBD;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClienteDAO {

    public void insertar(Cliente c) throws DatosException, ValidacionException {
        if (existe(c.getId())) {
            throw new ValidacionException("Ya existe un cliente con el identificador " + c.getId());
        }
        String sql = "INSERT INTO cliente (id_cliente, nombre, telefono, direccion) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getId());
            ps.setString(2, c.getNombre());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getDireccion());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al registrar el cliente: " + e.getMessage(), e);
        }
    }

    public void actualizar(Cliente c) throws DatosException, ValidacionException {
        if (!existe(c.getId())) {
            throw new ValidacionException("El cliente no existe");
        }
        String sql = "UPDATE cliente SET nombre = ?, telefono = ?, direccion = ? WHERE id_cliente = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getTelefono());
            ps.setString(3, c.getDireccion());
            ps.setString(4, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al actualizar el cliente: " + e.getMessage(), e);
        }
    }

    public void eliminar(String id) throws DatosException, ValidacionException {
        String sqlConteo = "SELECT COUNT(*) FROM vehiculo WHERE id_cliente = ?";
        String sql = "DELETE FROM cliente WHERE id_cliente = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sqlConteo)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        throw new ValidacionException("No se puede eliminar el cliente porque tiene vehículos asociados");
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatosException("Error al eliminar el cliente: " + e.getMessage(), e);
        }
    }

    public Cliente buscarPorId(String id) throws DatosException {
        String sql = "SELECT id_cliente, nombre, telefono, direccion FROM cliente WHERE id_cliente = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException | ValidacionException e) {
            throw new DatosException("Error al buscar el cliente: " + e.getMessage(), e);
        }
    }

    public ArrayList<Cliente> listar() throws DatosException {
        String sql = "SELECT id_cliente, nombre, telefono, direccion FROM cliente ORDER BY nombre";
        ArrayList<Cliente> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException | ValidacionException e) {
            throw new DatosException("Error al listar los clientes: " + e.getMessage(), e);
        }
        return lista;
    }

    public boolean existe(String id) throws DatosException {
        String sql = "SELECT COUNT(*) FROM cliente WHERE id_cliente = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al verificar el cliente: " + e.getMessage(), e);
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException, ValidacionException {
        return new Cliente(rs.getString("id_cliente"), rs.getString("nombre"),
                rs.getString("telefono"), rs.getString("direccion"));
    }
}
