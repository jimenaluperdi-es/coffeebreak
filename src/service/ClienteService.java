package service;

import beans.Cliente;
import dao.ClienteDAO;
import java.sql.SQLException;
import java.util.List;
import utils.PasswordUtils;

public class ClienteService {

    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.clienteDAO = new ClienteDAO();
    }

    public Cliente registrar(String nombre, String apellido, String email, String contrasena, String telefono) throws SQLException {
        if (clienteDAO.buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setEmail(email);
        cliente.setContrasena(PasswordUtils.hashPassword(contrasena));
        cliente.setTelefono(telefono);
        clienteDAO.insertar(cliente);
        return cliente;
    }

    public Cliente login(String email, String contrasena) throws SQLException {
        Cliente cliente = clienteDAO.buscarPorEmail(email);
        if (cliente != null && PasswordUtils.checkPassword(contrasena, cliente.getContrasena())) {
            return cliente;
        }
        return null;
    }

    public Cliente buscarPorId(int id) throws SQLException {
        return clienteDAO.buscarPorId(id);
    }

    public List<Cliente> listarTodos() throws SQLException {
        return clienteDAO.listarTodos();
    }

    public void actualizar(Cliente cliente) throws SQLException {
        clienteDAO.actualizar(cliente);
    }
}
