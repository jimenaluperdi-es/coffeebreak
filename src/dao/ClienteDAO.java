package dao;

import beans.Cliente;
import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private static final String INSERT = "INSERT INTO CLIENTE (nombre, apellido, email, contrasena, telefono) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE CLIENTE SET nombre=?, apellido=?, email=?, contrasena=?, telefono=? WHERE id_cliente=?";
    private static final String DELETE = "DELETE FROM CLIENTE WHERE id_cliente=?";
    private static final String SELECT_ALL = "SELECT * FROM CLIENTE";
    private static final String SELECT_BY_ID = "SELECT * FROM CLIENTE WHERE id_cliente=?";
    private static final String SELECT_BY_EMAIL = "SELECT * FROM CLIENTE WHERE email=?";

    public void insertar(Cliente cliente) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getContrasena());
            ps.setString(5, cliente.getTelefono());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setIdCliente(rs.getInt(1));
                }
            }
        }
    }

    public void actualizar(Cliente cliente) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getContrasena());
            ps.setString(5, cliente.getTelefono());
            ps.setInt(6, cliente.getIdCliente());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_ALL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Cliente buscarPorId(int id) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public Cliente buscarPorEmail(String email) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_EMAIL)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setApellido(rs.getString("apellido"));
        c.setEmail(rs.getString("email"));
        c.setContrasena(rs.getString("contrasena"));
        c.setTelefono(rs.getString("telefono"));
        return c;
    }
}
