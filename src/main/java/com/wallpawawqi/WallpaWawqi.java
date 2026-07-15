package com.wallpawawqi;

import java.net.InetSocketAddress;
import java.util.List;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.wallpawawqi.Class.EmpleadoSesion;
import com.wallpawawqi.Class.Producto;
import com.wallpawawqi.DAO.EmpleadoDAO;
import com.wallpawawqi.DAO.ProductoDAO;
import com.wallpawawqi.services.CloudinaryService;

public class WallpaWawqi {

        public static void main(String[] args) throws Exception {
                int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

                HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

                Gson gson = new Gson();

                server.createContext("/productos", exchange -> {
                        try {
                                configurarCors(exchange);

                                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                                        exchange.sendResponseHeaders(204, -1);
                                        exchange.close();
                                        return;
                                }

                                if ("GET".equals(exchange.getRequestMethod())) {
                                        String path = exchange.getRequestURI().getPath();
                                        String[] partes = path.split("/");

                                        ProductoDAO dao = new ProductoDAO();

                                        if (partes.length >= 3) {
                                                long id = Long.parseLong(partes[2]);
                                                Producto producto = dao.obtenerPorId(id);

                                                if (producto != null) {
                                                        String json = gson.toJson(producto);
                                                        exchange.getResponseHeaders().add("Content-Type",
                                                                        "application/json");
                                                        exchange.sendResponseHeaders(200, json.getBytes().length);
                                                        exchange.getResponseBody().write(json.getBytes());
                                                } else {
                                                        String error = "{\"error\": \"Producto no encontrado\"}";
                                                        exchange.getResponseHeaders().add("Content-Type",
                                                                        "application/json");
                                                        exchange.sendResponseHeaders(404, error.getBytes().length);
                                                        exchange.getResponseBody().write(error.getBytes());
                                                }
                                        } else {
                                                List<Producto> productos = dao.obtenerTodos();
                                                String json = gson.toJson(productos);

                                                exchange.getResponseHeaders().add("Content-Type", "application/json");
                                                exchange.sendResponseHeaders(200, json.getBytes().length);
                                                exchange.getResponseBody().write(json.getBytes());
                                        }
                                } else if ("PUT".equals(exchange.getRequestMethod())) {
                                        String path = exchange.getRequestURI().getPath();
                                        String[] partes = path.split("/");

                                        if (partes.length < 3) {
                                                String error = "{\"error\": \"ID requerido\"}";
                                                exchange.sendResponseHeaders(400, error.getBytes().length);
                                                exchange.getResponseBody().write(error.getBytes());
                                                exchange.close();
                                                return;
                                        }

                                        long id = Long.parseLong(partes[2]);
                                        String requestBody = new String(exchange.getRequestBody().readAllBytes());
                                        Producto productoActualizado = gson.fromJson(requestBody, Producto.class);

                                        ProductoDAO dao = new ProductoDAO();
                                        boolean actualizado = dao.actualizar(id, productoActualizado);

                                        String response;
                                        if (actualizado) {
                                                response = "{\"message\": \"Producto actualizado\"}";
                                                exchange.sendResponseHeaders(200, response.getBytes().length);
                                        } else {
                                                response = "{\"error\": \"Producto no encontrado\"}";
                                                exchange.sendResponseHeaders(404, response.getBytes().length);
                                        }

                                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                                        exchange.getResponseBody().write(response.getBytes());
                                } else if ("POST".equals(exchange.getRequestMethod())) {
                                        try {
                                                String requestBody = new String(
                                                                exchange.getRequestBody().readAllBytes());

                                                // ✅ DEBUG: Ver qué se recibe
                                                System.out.println("Request Body: " + requestBody);
                                                System.out.println("Request Body length: " + requestBody.length());

                                                // Validar que no esté vacío
                                                if (requestBody == null || requestBody.isEmpty()) {
                                                        String error = "{\"error\": \"Body vacío\"}";
                                                        exchange.sendResponseHeaders(400, error.getBytes().length);
                                                        exchange.getResponseBody().write(error.getBytes());
                                                        return;
                                                }

                                                com.google.gson.JsonObject json = gson.fromJson(requestBody,
                                                                com.google.gson.JsonObject.class);

                                                String nombre = json.get("nombre").getAsString();
                                                String descripcion = json.get("descripcion").getAsString();
                                                double precio = json.get("precio").getAsDouble();
                                                String imagenBase64 = json.get("imagenBase64").getAsString();

                                                // NUEVO: leer y validar categoría
                                                if (!json.has("categoryId") || json.get("categoryId").isJsonNull()) {
                                                        String error = "{\"error\": \"categoryId es requerido\"}";
                                                        exchange.getResponseHeaders().add("Content-Type",
                                                                        "application/json");
                                                        exchange.sendResponseHeaders(400, error.getBytes().length);
                                                        exchange.getResponseBody().write(error.getBytes());
                                                        return;
                                                }
                                                int categoryId = json.get("categoryId").getAsInt();

                                                if (categoryId != 26 && categoryId != 27 && categoryId != 28
                                                                && categoryId != 29) {
                                                        String error = "{\"error\": \"categoryId inválido\"}";
                                                        exchange.getResponseHeaders().add("Content-Type",
                                                                        "application/json");
                                                        exchange.sendResponseHeaders(400, error.getBytes().length);
                                                        exchange.getResponseBody().write(error.getBytes());
                                                        return;
                                                }

                                                System.out.println("Nombre: " + nombre);
                                                System.out.println("Descripción: " + descripcion);
                                                System.out.println("Precio: " + precio);
                                                System.out.println("CategoryId: " + categoryId);
                                                System.out.println("ImagenBase64 length: " + imagenBase64.length());

                                                // Limpiar prefijo data:image/...;base64,
                                                if (imagenBase64.contains(",")) {
                                                        imagenBase64 = imagenBase64.split(",")[1];
                                                }

                                                // Convertir base64 a bytes
                                                byte[] imagenBytes = java.util.Base64.getDecoder().decode(imagenBase64);
                                                System.out.println("Imagen bytes: " + imagenBytes.length);

                                                // Subir a Cloudinary
                                                String urlImagenCloudinary = CloudinaryService
                                                                .uploadImageFromBytes(imagenBytes, nombre);
                                                System.out.println("URL Cloudinary: " + urlImagenCloudinary);

                                                // Crear producto
                                                Producto nuevoProducto = new Producto(nombre, descripcion, precio,
                                                                urlImagenCloudinary);
                                                nuevoProducto.setCategoryId(categoryId); // NUEVO

                                                ProductoDAO dao = new ProductoDAO();
                                                long idGenerado = dao.crear(nuevoProducto);

                                                String response;
                                                if (idGenerado > 0) {
                                                        response = "{\"message\": \"Producto creado\", \"id\": "
                                                                        + idGenerado + "}";
                                                        exchange.sendResponseHeaders(201, response.getBytes().length);
                                                } else {
                                                        response = "{\"error\": \"No se pudo crear el producto\"}";
                                                        exchange.sendResponseHeaders(500, response.getBytes().length);
                                                }

                                                exchange.getResponseHeaders().add("Content-Type", "application/json");
                                                exchange.getResponseBody().write(response.getBytes());
                                        } catch (Exception e) {
                                                e.printStackTrace();
                                                String error = "{\"error\": \"" + e.getMessage() + "\"}";
                                                exchange.sendResponseHeaders(400, error.getBytes().length);
                                                exchange.getResponseBody().write(error.getBytes());
                                        }
                                } else if ("DELETE".equals(exchange.getRequestMethod())) {
                                        String path = exchange.getRequestURI().getPath();
                                        String[] partes = path.split("/");

                                        if (partes.length < 3) {
                                                String error = "{\"error\": \"ID requerido\"}";
                                                exchange.getResponseHeaders().add("Content-Type", "application/json");
                                                exchange.sendResponseHeaders(400, error.getBytes().length);
                                                exchange.getResponseBody().write(error.getBytes());
                                                exchange.close();
                                                return;
                                        }

                                        long id = Long.parseLong(partes[2]);
                                        ProductoDAO dao = new ProductoDAO();
                                        boolean eliminado = dao.eliminar(id);

                                        String response;
                                        if (eliminado) {
                                                response = "{\"message\": \"Producto eliminado\"}";
                                                exchange.sendResponseHeaders(200, response.getBytes().length);
                                        } else {
                                                response = "{\"error\": \"Producto no encontrado\"}";
                                                exchange.sendResponseHeaders(404, response.getBytes().length);
                                        }

                                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                                        exchange.getResponseBody().write(response.getBytes());
                                }

                        } catch (Exception e) {
                                e.printStackTrace();
                                String error = "{\"error\": \"" + e.getMessage() + "\"}";
                                exchange.sendResponseHeaders(500, error.getBytes().length);
                                exchange.getResponseBody().write(error.getBytes());
                        } finally {
                                exchange.close();
                        }
                });

                server.createContext("/login", exchange -> {
                        try {
                                configurarCors(exchange);

                                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                                        exchange.sendResponseHeaders(204, -1);
                                        exchange.close();
                                        return;
                                }

                                if (!"POST".equals(exchange.getRequestMethod())) {
                                        String error = "{\"error\": \"Método no permitido\"}";
                                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                                        exchange.sendResponseHeaders(405, error.getBytes().length);
                                        exchange.getResponseBody().write(error.getBytes());
                                        exchange.close();
                                        return;
                                }

                                String requestBody = new String(exchange.getRequestBody().readAllBytes());

                                if (requestBody == null || requestBody.isEmpty()) {
                                        String error = "{\"error\": \"Body vacío\"}";
                                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                                        exchange.sendResponseHeaders(400, error.getBytes().length);
                                        exchange.getResponseBody().write(error.getBytes());
                                        exchange.close();
                                        return;
                                }

                                com.google.gson.JsonObject json = gson.fromJson(requestBody,
                                                com.google.gson.JsonObject.class);

                                if (!json.has("nombre") || !json.has("celular")) {
                                        String error = "{\"error\": \"nombre y celular son requeridos\"}";
                                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                                        exchange.sendResponseHeaders(400, error.getBytes().length);
                                        exchange.getResponseBody().write(error.getBytes());
                                        exchange.close();
                                        return;
                                }

                                String nombre = json.get("nombre").getAsString();
                                String celular = json.get("celular").getAsString();

                                EmpleadoDAO empleadoDAO = new EmpleadoDAO();
                                EmpleadoSesion sesion = empleadoDAO.autenticar(nombre, celular);

                                String response;
                                if (sesion == null) {
                                        response = "{\"error\": \"Credenciales inválidas\"}";
                                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                                        exchange.sendResponseHeaders(401, response.getBytes().length);
                                } else {
                                        response = gson.toJson(sesion);
                                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                                        exchange.sendResponseHeaders(200, response.getBytes().length);
                                }

                                exchange.getResponseBody().write(response.getBytes());
                        } catch (Exception e) {
                                e.printStackTrace();
                                String error = "{\"error\": \"" + e.getMessage() + "\"}";
                                try {
                                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                                        exchange.sendResponseHeaders(500, error.getBytes().length);
                                        exchange.getResponseBody().write(error.getBytes());
                                } catch (Exception ignored) {
                                }
                        } finally {
                                exchange.close();
                        }
                });

                server.start();

                System.out.println("Servidor en http://localhost:8080/productos");
        }

        private static void configurarCors(HttpExchange exchange) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        }
}
