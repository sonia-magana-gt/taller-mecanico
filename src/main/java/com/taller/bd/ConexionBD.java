package com.taller.bd;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {

    private static final Properties PROPIEDADES = new Properties();

    static {
        try (InputStream entrada = ConexionBD.class.getResourceAsStream("/db.properties")) {
            PROPIEDADES.load(entrada);
        } catch (IOException | NullPointerException e) {
            throw new ExceptionInInitializerError("No se pudo leer db.properties");
        }
    }

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(
                PROPIEDADES.getProperty("db.url"),
                PROPIEDADES.getProperty("db.usuario"),
                PROPIEDADES.getProperty("db.clave"));
    }
}
