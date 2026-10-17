package com.taller;

import com.taller.bd.ConexionBD;
import com.taller.vista.UtilVista;
import com.taller.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Se usará la apariencia por defecto");
        }
        SwingUtilities.invokeLater(() -> {
            try (Connection con = ConexionBD.obtenerConexion()) {
                new VentanaPrincipal().setVisible(true);
            } catch (SQLException e) {
                UtilVista.mostrarError(null, "No se pudo conectar a Oracle: " + e.getMessage());
            }
        });
    }
}
