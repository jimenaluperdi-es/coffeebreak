package controller;

import beans.Categoria;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CategoriaService;
import utils.JsonUtils;

@WebServlet(name = "CategoriaController", urlPatterns = {"/api/categorias"})
public class CategoriaController extends HttpServlet {

    private final CategoriaService categoriaService = new CategoriaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            List<Categoria> categorias = categoriaService.listarTodas();
            JsonUtils.enviarJson(response, categorias);
        } catch (SQLException e) {
            JsonUtils.enviarError(response, 500, "Error al obtener categorías: " + e.getMessage());
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
