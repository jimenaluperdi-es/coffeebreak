package controller;

import beans.Cliente;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.ClienteService;
import utils.JsonUtils;
import utils.Validaciones;

@WebServlet(name = "AuthController", urlPatterns = {"/api/login", "/api/registro", "/api/logout", "/api/sesion"})
public class AuthController extends HttpServlet {

    private final ClienteService clienteService = new ClienteService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if (request.getRequestURI().endsWith("/api/sesion")) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("clienteId") != null) {
                int idCliente = (int) session.getAttribute("clienteId");
                String nombre = (String) session.getAttribute("clienteNombre");
                String email = (String) session.getAttribute("clienteEmail");
                JsonUtils.enviarJson(response, Map.of("autenticado", true, "idCliente", idCliente, "nombre", nombre, "email", email));
            } else {
                JsonUtils.enviarJson(response, Map.of("autenticado", false));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String uri = request.getRequestURI();
        try {
            if (uri.endsWith("/api/login")) {
                handleLogin(request, response);
            } else if (uri.endsWith("/api/registro")) {
                handleRegistro(request, response);
            } else if (uri.endsWith("/api/logout")) {
                handleLogout(request, response);
            } else {
                JsonUtils.enviarError(response, 404, "Ruta no encontrada");
            }
        } catch (SQLException e) {
            JsonUtils.enviarError(response, 500, "Error de base de datos: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            JsonUtils.enviarError(response, 400, e.getMessage());
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        Map<String, Object> datos = JsonUtils.parsearMapa(request);
        String email = (String) datos.get("email");
        String contrasena = (String) datos.get("contrasena");
        if (Validaciones.esCampoVacio(email) || Validaciones.esCampoVacio(contrasena)) {
            JsonUtils.enviarError(response, 400, "Todos los campos son obligatorios");
            return;
        }
        Cliente cliente = clienteService.login(email, contrasena);
        if (cliente != null) {
            HttpSession session = request.getSession();
            session.setAttribute("clienteId", cliente.getIdCliente());
            session.setAttribute("clienteNombre", cliente.getNombre());
            session.setAttribute("clienteEmail", cliente.getEmail());
            JsonUtils.enviarJson(response, Map.of("mensaje", "Inicio de sesión exitoso", "cliente", Map.of("id", cliente.getIdCliente(), "nombre", cliente.getNombre(), "email", cliente.getEmail())));
        } else {
            JsonUtils.enviarError(response, 401, "Email o contraseña incorrectos");
        }
    }

    private void handleRegistro(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        Map<String, Object> datos = JsonUtils.parsearMapa(request);
        String nombre = (String) datos.get("nombre");
        String apellido = (String) datos.get("apellido");
        String email = (String) datos.get("email");
        String contrasena = (String) datos.get("contrasena");
        String telefono = (String) datos.get("telefono");
        if (Validaciones.esCampoVacio(nombre) || Validaciones.esCampoVacio(apellido) || Validaciones.esCampoVacio(email) || Validaciones.esCampoVacio(contrasena)) {
            JsonUtils.enviarError(response, 400, "Todos los campos obligatorios deben estar llenos");
            return;
        }
        if (!Validaciones.esEmailValido(email)) {
            JsonUtils.enviarError(response, 400, "Email no válido");
            return;
        }
        if (!Validaciones.esContrasenaValida(contrasena)) {
            JsonUtils.enviarError(response, 400, "La contraseña debe tener al menos 8 caracteres");
            return;
        }
        try {
            Cliente cliente = clienteService.registrar(nombre, apellido, email, contrasena, telefono);
            JsonUtils.enviarJson(response, Map.of("mensaje", "Registro exitoso", "id", cliente.getIdCliente()));
        } catch (IllegalArgumentException e) {
            JsonUtils.enviarError(response, 400, e.getMessage());
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        JsonUtils.enviarJson(response, Map.of("mensaje", "Sesión cerrada exitosamente"));
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
