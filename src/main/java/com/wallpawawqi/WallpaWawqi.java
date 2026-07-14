package com.wallpawawqi;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import java.util.Base64;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.wallpawawqi.Class.Producto;
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
                                                // GET /productos/{id}
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
                                                // GET /productos (todos)
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
                                                String contentType = exchange.getRequestHeaders()
                                                                .getFirst("Content-Type");

                                                if (contentType != null
                                                                && contentType.startsWith("multipart/form-data")) {
                                                        // Manejar multipart (form-data) con imagen
                                                        handleMultipartPost(exchange, gson);
                                                        return;
                                                }

                                                String requestBody = new String(
                                                                exchange.getRequestBody().readAllBytes());

                                                // Parse JSON
                                                com.google.gson.JsonObject json = gson.fromJson(requestBody,
                                                                com.google.gson.JsonObject.class);

                                                String nombre = json.get("nombre").getAsString();
                                                String descripcion = json.get("descripcion").getAsString();
                                                double precio = json.get("precio").getAsDouble();
                                                String imagenBase64 = json.get("imagenBase64").getAsString();

                                                // ✅ Subir imagen a Cloudinary
                                                String urlImagenCloudinary = CloudinaryService
                                                                .uploadImageFromBase64(imagenBase64, nombre);

                                                // ✅ Crear objeto Producto con todos los datos
                                                Producto nuevoProducto = new Producto(
                                                                nombre,
                                                                descripcion,
                                                                precio,
                                                                urlImagenCloudinary);

                                                // ✅ DAO inserta: nombre, descripción, precio, url_imagen
                                                ProductoDAO dao = new ProductoDAO();

                                                System.out.println("Nombre: " + nombre);
                                                System.out.println("Descripción: " + descripcion);
                                                System.out.println("Precio: " + precio);
                                                System.out.println("ImagenBase64 length: " + imagenBase64.length());

                                                // Limpiar prefijo
                                                if (imagenBase64.contains(",")) {
                                                        imagenBase64 = imagenBase64.split(",")[1];
                                                }

                                                System.out.println("URL Cloudinary: " + urlImagenCloudinary);

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

                server.start();

                System.out.println("Servidor en http://localhost:8080/productos");
        }

        // ✅ NUEVO: Método para manejar multipart con imagen
        private static void handleMultipartPost(HttpExchange exchange, Gson gson) throws Exception {
                String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
                String boundary = contentType.split("boundary=")[1];

                byte[] requestBody = exchange.getRequestBody().readAllBytes();
                String bodyString = new String(requestBody);

                // Parse multipart
                Map<String, Object> fields = parseMultipart(bodyString, boundary, requestBody);

                String nombre = (String) fields.get("nombre");
                String descripcion = (String) fields.get("descripcion");
                String precio = (String) fields.get("precio");
                byte[] imagenBytes = (byte[]) fields.get("imagen");

                // ✅ Subir imagen a Cloudinary
                String imagenBase64 = Base64.getEncoder().encodeToString(imagenBytes);

                String imageUrl = CloudinaryService.uploadImageFromBase64(
                                imagenBase64,
                                nombre.replaceAll(" ", "_"));

                // Crear producto
                Producto nuevoProducto = new Producto(nombre, descripcion, Double.parseDouble(precio), imageUrl);

                ProductoDAO dao = new ProductoDAO();
                long idGenerado = dao.crear(nuevoProducto);

                String response;
                if (idGenerado > 0) {
                        response = "{\"message\": \"Producto creado\", \"id\": " + idGenerado
                                        + ", \"imageUrl\": \"" + imageUrl + "\"}";
                        exchange.sendResponseHeaders(201, response.getBytes().length);
                } else {
                        response = "{\"error\": \"No se pudo crear el producto\"}";
                        exchange.sendResponseHeaders(500, response.getBytes().length);
                }

                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.getResponseBody().write(response.getBytes());
        }

        // Método original para JSON
        private static void handleJsonPost(HttpExchange exchange, Gson gson) throws Exception {
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                Producto nuevoProducto = gson.fromJson(requestBody, Producto.class);

                ProductoDAO dao = new ProductoDAO();
                long idGenerado = dao.crear(nuevoProducto);

                String response;
                if (idGenerado > 0) {
                        response = "{\"message\": \"Producto creado\", \"id\": " + idGenerado + "}";
                        exchange.sendResponseHeaders(201, response.getBytes().length);
                } else {
                        response = "{\"error\": \"No se pudo crear el producto\"}";
                        exchange.sendResponseHeaders(500, response.getBytes().length);
                }

                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.getResponseBody().write(response.getBytes());
        }

        // ✅ NUEVO: Parser simple de multipart
        private static Map<String, Object> parseMultipart(String body, String boundary, byte[] bodyBytes)
                        throws Exception {
                Map<String, Object> result = new java.util.HashMap<>();

                String[] parts = body.split("--" + boundary);

                for (String part : parts) {
                        if (part.contains("Content-Disposition")) {
                                // Extraer nombre del campo
                                int nameStart = part.indexOf("name=\"") + 6;
                                int nameEnd = part.indexOf("\"", nameStart);
                                String fieldName = part.substring(nameStart, nameEnd);

                                if (part.contains("filename=")) {
                                        // Es archivo (imagen)
                                        int contentStart = part.indexOf("\r\n\r\n") + 4;
                                        int contentEnd = part.lastIndexOf("\r\n");

                                        // Encontrar el índice en bytes
                                        int startIdx = body.indexOf(part) + contentStart;
                                        byte[] imageBytes = new byte[contentEnd - contentStart];
                                        System.arraycopy(bodyBytes, startIdx, imageBytes, 0, contentEnd - contentStart);

                                        result.put("imagen", imageBytes);
                                } else {
                                        // Es campo de texto
                                        int valueStart = part.indexOf("\r\n\r\n") + 4;
                                        int valueEnd = part.indexOf("\r\n", valueStart);
                                        String value = part.substring(valueStart, valueEnd);
                                        result.put(fieldName, value);
                                }
                        }
                }

                return result;
        }

        private static void configurarCors(HttpExchange exchange) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        }
}
