package dao;

import beans.DetallePedido;
import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetallePedidoDAO {

    private static final String INSERT = "INSERT INTO DETALLE_PEDIDO (id_pedido, id_producto, cantidad) VALUES (?, ?, ?)";
    private static final String SELECT_BY_PEDIDO = "SELECT dp.*, p.nombre_producto, p.precio FROM DETALLE_PEDIDO dp JOIN PRODUCTO p ON dp.id_producto = p.id_producto WHERE dp.id_pedido=?";

    public void insertar(DetallePedido detalle) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, detalle.getIdPedido());
            ps.setInt(2, detalle.getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    detalle.setIdDetalle(rs.getInt(1));
                }
            }
        }
    }

    public List<DetallePedido> listarPorPedido(int idPedido) throws SQLException {
        List<DetallePedido> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_PEDIDO)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallePedido d = new DetallePedido();
                    d.setIdDetalle(rs.getInt("id_detalle"));
                    d.setIdPedido(rs.getInt("id_pedido"));
                    d.setIdProducto(rs.getInt("id_producto"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setNombreProducto(rs.getString("nombre_producto"));
                    d.setPrecioProducto(rs.getDouble("precio"));
                    lista.add(d);
                }
            }
        }
        return lista;
    }
}
