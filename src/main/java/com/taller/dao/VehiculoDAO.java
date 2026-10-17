package com.taller.dao;

import com.taller.bd.ConexionBD;
import com.taller.excepciones.DatosException;
import com.taller.excepciones.ValidacionException;
import com.taller.modelo.Vehiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VehiculoDAO {

    private static final String COLUMNAS = "SELECT placa, marca, modelo, anio, color, id_cliente FROM vehiculo";

    public void insertar(Vehiculo v) throws DatosException, ValidacionException {
        if (existe(v.getPlaca())) {
            throw new ValidacionException("Ya existe un vehículo con esa placa");
        }
        if (!new ClienteDAO().existe(v.getIdCliente())) {
            throw new ValidacionException("El cliente no existe");
        }
        String sql = "INSERT INTO vehiculo (placa, marca, modelo, anio, color, id_cliente) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setInt(4, v.getAnio());
            ps.setString(5, v.getColor());
            ps.setString(6, v.getIdCliente());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al registrar el vehículo: " + e.getMessage(), e);
        }
    }

    public void actualizar(Vehiculo v) throws DatosException, ValidacionException {
        if (!existe(v.getPlaca())) {
            throw new ValidacionException("El vehículo no existe");
        }
        if (!new ClienteDAO().existe(v.getIdCliente())) {
            throw new ValidacionException("El cliente no existe");
        }
        String sql = "UPDATE vehiculo SET marca = ?, modelo = ?, anio = ?, color = ?, id_cliente = ? WHERE placa = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getMarca());
            ps.setString(2, v.getModelo());
            ps.setInt(3, v.getAnio());
            ps.setString(4, v.getColor());
            ps.setString(5, v.getIdCliente());
            ps.setString(6, v.getPlaca());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatosException("Error al actualizar el vehículo: " + e.getMessage(), e);
        }
    }

    public void eliminar(String placa) throws DatosException, ValidacionException {
        String sqlConteo = "SELECT COUNT(*) FROM orden_trabajo WHERE placa = ?";
        String sql = "DELETE FROM vehiculo WHERE placa = ?";
        try (Connection con = ConexionBD.obtenerConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sqlConteo)) {
                ps.setString(1, placa);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        throw new ValidacionException("No se puede eliminar el vehículo porque tiene órdenes registradas");
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, placa);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatosException("Error al eliminar el vehículo: " + e.getMessage(), e);
        }
    }

    public Vehiculo buscarPorPlaca(String placa) throws DatosException {
        String sql = COLUMNAS + " WHERE placa = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException | ValidacionException e) {
            throw new DatosException("Error al buscar el vehículo: " + e.getMessage(), e);
        }
    }

    public ArrayList<Vehiculo> listar() throws DatosException {
        return consultar(COLUMNAS + " ORDER BY placa", null);
    }

    public ArrayList<Vehiculo> listarPorCliente(String idCliente) throws DatosException {
        return consultar(COLUMNAS + " WHERE id_cliente = ? ORDER BY placa", idCliente);
    }

    public boolean existe(String placa) throws DatosException {
        String sql = "SELECT COUNT(*) FROM vehiculo WHERE placa = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatosException("Error al verificar el vehículo: " + e.getMessage(), e);
        }
    }

    private ArrayList<Vehiculo> consultar(String sql, String parametro) throws DatosException {
        ArrayList<Vehiculo> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setString(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException | ValidacionException e) {
            throw new DatosException("Error al listar los vehículos: " + e.getMessage(), e);
        }
        return lista;
    }

    private Vehiculo mapear(ResultSet rs) throws SQLException, ValidacionException {
        return new Vehiculo(rs.getString("placa"), rs.getString("marca"), rs.getString("modelo"),
                rs.getInt("anio"), rs.getString("color"), rs.getString("id_cliente"));
    }
}
