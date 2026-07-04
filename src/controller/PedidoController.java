package controller;

import beans.DetallePedido;
import beans.Pedido;
import beans.Producto;
import com.google.gson.Gson;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.PedidoService;
import service.ProductoService;
import utils.JsonUtils;

@WebServlet(name = "PedidoController", urlPatterns = {"/api/pedido", "/api/pedido/*", "/api/pedidos"})
public class PedidoController extends HttpServlet {

    private final PedidoService pedidoService = new PedidoService();
    private final ProductoService productoService = new ProductoService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();
        try {
            if (request.getRequestURI().endsWith("/api/pedidos")) {
                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute("clienteId") == null) {
                    JsonUtils.enviarError(response, 401, "No autenticado");
                    return;
                }
                int idCliente = (int) session.getAttribute("clienteId");
                List<Pedido> pedidos = pedidoService.listarPorCliente(idCliente);
                JsonUtils.enviarJson(response, pedidos);
            } else if (pathInfo != null && pathInfo.length() > 1) {
                int idPedido = Integer.parseInt(pathInfo.substring(1));
                Pedido pedido = pedidoService.buscarPorId(idPedido);
                if (pedido != null) {
                    List<DetallePedido> detalles = pedidoService.obtenerDetalles(idPedido);
                    response.getWriter().write(gson.toJson(Map.of("pedido", pedido, "detalles", detalles)));
                } else {
                    JsonUtils.enviarError(response, 404, "Pedido no encontrado");
                }
            } else {
                JsonUtils.enviarError(response, 400, "ID de pedido requerido");
            }
        } catch (SQLException e) {
            JsonUtils.enviarError(response, 500, "Error al obtener pedido: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clienteId") == null) {
            JsonUtils.enviarError(response, 401, "No autenticado");
            return;
        }
        try {
            int idCliente = (int) session.getAttribute("clienteId");
            Map<String, Object> datos = JsonUtils.parsearMapa(request);
            String metodoPago = (String) datos.get("metodoPago");
            double monto = ((Number) datos.get("monto")).doubleValue();
            List<Map<String, Object>> items = (List<Map<String, Object>>) datos.get("items");
            if (items == null || items.isEmpty()) {
                JsonUtils.enviarError(response, 400, "El pedido no puede estar vacío");
                return;
            }
            List<DetallePedido> detalles = new java.util.ArrayList<>();
            for (Map<String, Object> item : items) {
                DetallePedido detalle = new DetallePedido();
                int idProducto = ((Number) item.get("idProducto")).intValue();
                int cantidad = ((Number) item.get("cantidad")).intValue();
                if (cantidad <= 0) {
                    JsonUtils.enviarError(response, 400, "Cantidades deben ser positivas");
                    return;
                }
                Producto producto = productoService.buscarPorId(idProducto);
                if (producto == null || !producto.isDisponible()) {
                    JsonUtils.enviarError(response, 400, "Producto no disponible: " + idProducto);
                    return;
                }
                detalle.setIdProducto(idProducto);
                detalle.setCantidad(cantidad);
                detalles.add(detalle);
            }
            Pedido pedido = pedidoService.crearPedido(idCliente, monto, metodoPago, detalles);
            JsonUtils.enviarJson(response, Map.of("mensaje", "Pedido creado con éxito", "idPedido", pedido.getIdPedido()));
        } catch (SQLException e) {
            JsonUtils.enviarError(response, 500, "Error al crear pedido: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            JsonUtils.enviarError(response, 400, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            JsonUtils.enviarError(response, 400, "ID de pedido requerido");
            return;
        }
        try {
            int idPedido = Integer.parseInt(pathInfo.substring(1));
            Map<String, Object> datos = JsonUtils.parsearMapa(request);
            int idEstado = ((Number) datos.get("idEstado")).intValue();
            Pedido pedido = pedidoService.actualizarEstado(idPedido, idEstado);
            if (pedido != null) {
                JsonUtils.enviarJson(response, Map.of("mensaje", "Estado actualizado", "pedido", pedido));
            } else {
                JsonUtils.enviarError(response, 404, "Pedido no encontrado");
            }
        } catch (SQLException e) {
            JsonUtils.enviarError(response, 500, "Error al actualizar pedido: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            JsonUtils.enviarError(response, 400, "ID de pedido requerido");
            return;
        }
        try {
            int idPedido = Integer.parseInt(pathInfo.substring(1));
            pedidoService.eliminarPedido(idPedido);
            JsonUtils.enviarJson(response, Map.of("mensaje", "Pedido eliminado con éxito"));
        } catch (SQLException e) {
            JsonUtils.enviarError(response, 500, "Error al eliminar pedido: " + e.getMessage());
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
