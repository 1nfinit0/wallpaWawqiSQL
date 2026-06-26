package com.wallpawawqi;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.wallpawawqi.Class.Producto;
import com.wallpawawqi.DAO.ProductoDAO;

import java.net.InetSocketAddress;
import java.util.List;

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
                                        ProductoDAO dao = new ProductoDAO();
                                        List<Producto> productos = dao.obtenerTodos();
                                        String json = gson.toJson(productos);

                                        exchange.getResponseHeaders().add("Content-Type", "application/json");
                                        exchange.sendResponseHeaders(200, json.getBytes().length);
                                        exchange.getResponseBody().write(json.getBytes());
                                }
                        } catch (Exception e) {
                                e.printStackTrace();

                                String error = "{\"error\": \"" + e.getMessage() + "\"}";
                                exchange.sendResponseHeaders(500, error.length());
                                exchange.getResponseBody().write(error.getBytes());
                        } finally {
                                exchange.close();
                        }

                        if ("GET".equals(exchange.getRequestMethod())) {
                                ProductoDAO dao = new ProductoDAO();
                                List<Producto> productos = dao.obtenerTodos();
                                String json = gson.toJson(productos);

                                exchange.getResponseHeaders().add("Content-Type", "application/json");
                                exchange.sendResponseHeaders(200, json.getBytes().length);
                                exchange.getResponseBody().write(json.getBytes());
                        }

                        if ("PUT".equals(exchange.getRequestMethod())) {
                                String path = exchange.getRequestURI().getPath();
                                String[] partes = path.split("/");

                                /*
                                 * /productos/5
                                 * ["", "productos", "5"]
                                 */

                                if (partes.length < 3) {
                                        String error = "{\"error\": \"ID requerido\"}";

                                        exchange.sendResponseHeaders(400, error.getBytes().length);
                                        exchange.getResponseBody().write(error.getBytes());
                                        return;
                                }

                                long id = Long.parseLong(partes[2]);

                                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                                Producto productoActualizado = gson.fromJson(requestBody, Producto.class);

                                ProductoDAO dao = new ProductoDAO();
                                boolean actualizado = dao.actualizar(id, productoActualizado);

                                String response;

                                if (actualizado) {
                                        response = """
                                                                        {
                                                                                "message":
                                                                                "Producto actualizado"
                                                                        }
                                                                        """;

                                        exchange.sendResponseHeaders(200, response.getBytes().length);
                                } else {
                                        response = """
                                                                        {
                                                                                "error":
                                                                                "Producto no encontrado"
                                                                        }
                                                                        """;

                                        exchange.sendResponseHeaders(404, response.getBytes().length);
                                }

                                exchange.getResponseHeaders().add("Content-Type", "application/json");
                                exchange.getResponseBody().write(response.getBytes());
                        }
                });

                server.start();

                System.out.println("Servidor en http://localhost:8080/productos");
        }

        private static void configurarCors(HttpExchange exchange) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        }
}