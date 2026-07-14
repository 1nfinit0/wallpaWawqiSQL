package com.wallpawawqi.DAO;

import com.wallpawawqi.Class.EmpleadoSesion;
import com.wallpawawqi.Connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpleadoDAO {

    public EmpleadoSesion autenticar(String nombre, String celular) {
        String sql = "SELECT id_empleado, nombre_empleado, apellido_empleado, cargo " +
                     "FROM empleado " +
                     "WHERE LOWER(TRIM(nombre_empleado)) = LOWER(TRIM(?)) " +
                     "AND celular_empleado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            stmt.setString(2, celular);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EmpleadoSesion(
                        rs.getLong("id_empleado"),
                        rs.getString("nombre_empleado"),
                        rs.getString("apellido_empleado"),
                        rs.getString("cargo")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
