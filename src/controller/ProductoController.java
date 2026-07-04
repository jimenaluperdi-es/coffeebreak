package controller;

import beans.Producto;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ProductoService;
import utils.JsonUtils;

@WebServlet(name = "ProductoController", urlPatterns = {"/api/productos"})
public class ProductoController extends HttpServlet {

    private final ProductoService productoService = new ProductoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String categoriaParam = request.getParameter("categoria");
            String busqueda = request.getParameter("busqueda");
            List<Producto> productos;
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                productos = productoService.buscar(busqueda);
            } else if (categoriaParam != null && !categoriaParam.trim().isEmpty()) {
                int idCategoria = Integer.parseInt(categoriaParam);
                productos = productoService.listarPorCategoria(idCategoria);
            } else {
                productos = productoService.listarDisponibles();
            }
            JsonUtils.enviarJson(response, productos);
        } catch (SQLException e) {
            JsonUtils.enviarError(response, 500, "Error al obtener productos: " + e.getMessage());
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
