package dao;

import beans.Pedido;
import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    private static final String INSERT = "INSERT INTO PEDIDO (id_cliente, fecha_pedido, id_estado, fecha_pago, monto, metodo_pago) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE PEDIDO SET id_cliente=?, fecha_pedido=?, id_estado=?, fecha_pago=?, monto=?, metodo_pago=? WHERE id_pedido=?";
    private static final String UPDATE_ESTADO = "UPDATE PEDIDO SET id_estado=? WHERE id_pedido=?";
    private static final String DELETE = "DELETE FROM PEDIDO WHERE id_pedido=?";
    private static final String SELECT_ALL = "SELECT p.*, c.nombre AS nombre_cliente, e.nombre AS nombre_estado FROM PEDIDO p JOIN CLIENTE c ON p.id_cliente = c.id_cliente JOIN ESTADO_PEDIDO e ON p.id_estado = e.id_estado";
    private static final String SELECT_BY_ID = "SELECT p.*, c.nombre AS nombre_cliente, e.nombre AS nombre_estado FROM PEDIDO p JOIN CLIENTE c ON p.id_cliente = c.id_cliente JOIN ESTADO_PEDIDO e ON p.id_estado = e.id_estado WHERE p.id_pedido=?";
    private static final String SELECT_BY_CLIENTE = "SELECT p.*, e.nombre AS nombre_estado FROM PEDIDO p JOIN ESTADO_PEDIDO e ON p.id_estado = e.id_estado WHERE p.id_cliente=? ORDER BY p.fecha_pedido DESC";

    public void insertar(Pedido pedido) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pedido.getIdCliente());
            ps.setTimestamp(2, pedido.getFechaPedido() != null ? Timestamp.valueOf(pedido.getFechaPedido()) : null);
            ps.setInt(3, pedido.getIdEstado());
            ps.setDate(4, pedido.getFechaPago() != null ? Date.valueOf(pedido.getFechaPago()) : null);
            ps.setDouble(5, pedido.getMonto());
            ps.setString(6, pedido.getMetodoPago());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    pedido.setIdPedido(rs.getInt(1));
                }
            }
        }
    }

    public void actualizar(Pedido pedido) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setInt(1, pedido.getIdCliente());
            ps.setTimestamp(2, pedido.getFechaPedido() != null ? Timestamp.valueOf(pedido.getFechaPedido()) : null);
            ps.setInt(3, pedido.getIdEstado());
            ps.setDate(4, pedido.getFechaPago() != null ? Date.valueOf(pedido.getFechaPago()) : null);
            ps.setDouble(5, pedido.getMonto());
            ps.setString(6, pedido.getMetodoPago());
            ps.setInt(7, pedido.getIdPedido());
            ps.executeUpdate();
        }
    }

    public void actualizarEstado(int idPedido, int idEstado) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE_ESTADO)) {
            ps.setInt(1, idEstado);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Pedido> listarTodos() throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_ALL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearCompleto(rs));
            }
        }
        return lista;
    }

    public Pedido buscarPorId(int id) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCompleto(rs);
                }
            }
        }
        return null;
    }

    public List<Pedido> listarPorCliente(int idCliente) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_CLIENTE)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido p = new Pedido();
                    p.setIdPedido(rs.getInt("id_pedido"));
                    p.setIdCliente(rs.getInt("id_cliente"));
                    p.setFechaPedido(rs.getTimestamp("fecha_pedido") != null ? rs.getTimestamp("fecha_pedido").toLocalDateTime() : null);
                    p.setIdEstado(rs.getInt("id_estado"));
                    p.setFechaPago(rs.getDate("fecha_pago") != null ? rs.getDate("fecha_pago").toLocalDate() : null);
                    p.setMonto(rs.getDouble("monto"));
                    p.setMetodoPago(rs.getString("metodo_pago"));
                    p.setNombreEstado(rs.getString("nombre_estado"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    private Pedido mapearCompleto(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setIdPedido(rs.getInt("id_pedido"));
        p.setIdCliente(rs.getInt("id_cliente"));
        p.setFechaPedido(rs.getTimestamp("fecha_pedido") != null ? rs.getTimestamp("fecha_pedido").toLocalDateTime() : null);
        p.setIdEstado(rs.getInt("id_estado"));
        p.setFechaPago(rs.getDate("fecha_pago") != null ? rs.getDate("fecha_pago").toLocalDate() : null);
        p.setMonto(rs.getDouble("monto"));
        p.setMetodoPago(rs.getString("metodo_pago"));
        p.setNombreCliente(rs.getString("nombre_cliente"));
        p.setNombreEstado(rs.getString("nombre_estado"));
        return p;
    }
}
