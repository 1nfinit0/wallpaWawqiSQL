package com.wallpawawqi.DAO;

import com.wallpawawqi.Class.Producto;
import com.wallpawawqi.Connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

        public List<Producto> obtenerTodos() {

                List<Producto> productos = new ArrayList<>();

                String sql = """
                                    SELECT
                                        id_producto,
                                        nombre_producto,
                                        descripcion_producto,
                                        precio_producto,
                                        id_categoria,
                                        url_imagen
                                    FROM producto
                                """;

                try (
                                Connection conn = DatabaseConnection.getConnection();
                                PreparedStatement stmt = conn.prepareStatement(sql);
                                ResultSet rs = stmt.executeQuery()) {

                        while (rs.next()) {

                                Producto p = new Producto();
                                p.setId(rs.getLong("id_producto"));
                                p.setName(rs.getString("nombre_producto"));
                                p.setDescription(rs.getString("descripcion_producto"));
                                p.setPrice(rs.getDouble("precio_producto"));
                                int categoryId = rs.getInt("id_categoria");

                                if (!rs.wasNull()) {
                                        p.setCategoryId(categoryId);
                                }

                                p.setImg(rs.getString("url_imagen"));
                                productos.add(p);
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                }

                return productos;
        }

        public Producto obtenerPorId(long id) {
                String sql = """
                                    SELECT
                                        id_producto,
                                        nombre_producto,
                                        descripcion_producto,
                                        precio_producto,
                                        id_categoria,
                                        url_imagen
                                    FROM producto
                                    WHERE id_producto = ?
                                """;

                try (
                                Connection conn = DatabaseConnection.getConnection();
                                PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setLong(1, id);

                        try (ResultSet rs = stmt.executeQuery()) {
                                if (rs.next()) {
                                        Producto p = new Producto();
                                        p.setId(rs.getLong("id_producto"));
                                        p.setName(rs.getString("nombre_producto"));
                                        p.setDescription(rs.getString("descripcion_producto"));
                                        p.setPrice(rs.getDouble("precio_producto"));

                                        int categoryId = rs.getInt("id_categoria");
                                        if (!rs.wasNull()) {
                                                p.setCategoryId(categoryId);
                                        }

                                        p.setImg(rs.getString("url_imagen"));
                                        return p;
                                }
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                }

                return null;
        }

        public boolean actualizar(long id, Producto producto)

        {

                String sql = """
                                        UPDATE producto
                                        SET
                                        nombre_producto = ?,
                                        descripcion_producto = ?,
                                        precio_producto = ?,
                                        id_categoria = ?,
                                        url_imagen = ?
                                        WHERE id_producto = ?
                                """;

                try (
                                Connection conn = DatabaseConnection
                                                .getConnection();

                                PreparedStatement stmt = conn.prepareStatement(sql)) {

                        stmt.setString(
                                        1,
                                        producto.getName());

                        stmt.setString(
                                        2,
                                        producto.getDescription());

                        stmt.setDouble(
                                        3,
                                        producto.getPrice());

                        if (producto.getCategoryId() != null) {
                                stmt.setInt(
                                                4,
                                                producto.getCategoryId());
                        } else {
                                stmt.setNull(
                                                4,
                                                java.sql.Types.INTEGER);
                        }

                        stmt.setString(
                                        5,
                                        producto.getImg());

                        stmt.setLong(
                                        6,
                                        id);

                        int filasAfectadas = stmt.executeUpdate();

                        return filasAfectadas > 0;

                } catch (Exception e) {

                        e.printStackTrace();

                        return false;
                }
        }

        public long crear(Producto producto) {
                String sql = """
                                        INSERT INTO producto (nombre_producto, descripcion_producto, precio_producto, id_categoria, url_imagen)
                                        VALUES (?, ?, ?, ?, ?)
                                """;

                try (
                                Connection conn = DatabaseConnection.getConnection();
                                PreparedStatement stmt = conn.prepareStatement(sql,
                                                PreparedStatement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, producto.getName());
                        stmt.setString(2, producto.getDescription());
                        stmt.setDouble(3, producto.getPrice());

                        if (producto.getCategoryId() != null) {
                                stmt.setInt(4, producto.getCategoryId());
                        } else {
                                stmt.setNull(4, java.sql.Types.INTEGER);
                        }

                        stmt.setString(5, producto.getImg());

                        int filasAfectadas = stmt.executeUpdate();

                        if (filasAfectadas > 0) {
                                try (ResultSet rs = stmt.getGeneratedKeys()) {
                                        if (rs.next()) {
                                                return rs.getLong(1);
                                        }
                                }
                        }
                        return -1;

                } catch (Exception e) {
                        e.printStackTrace();
                        return -1;
                }
        }

        // Agregar este método a ProductoDAO.java

        public boolean eliminar(long id) {
                String sql = "DELETE FROM producto WHERE id_producto = ?";

                try (
                                Connection conn = DatabaseConnection.getConnection();
                                PreparedStatement stmt = conn.prepareStatement(sql)) {

                        stmt.setLong(1, id);
                        int filasAfectadas = stmt.executeUpdate();
                        return filasAfectadas > 0;

                } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                }
        }

}
