package dao;

import beans.EstadoPedido;
import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EstadoPedidoDAO {

    private static final String SELECT_ALL = "SELECT * FROM ESTADO_PEDIDO";
    private static final String SELECT_BY_ID = "SELECT * FROM ESTADO_PEDIDO WHERE id_estado=?";

    public List<EstadoPedido> listarTodos() throws SQLException {
        List<EstadoPedido> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_ALL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                EstadoPedido e = new EstadoPedido();
                e.setIdEstado(rs.getInt("id_estado"));
                e.setNombre(rs.getString("nombre"));
                e.setDescripcion(rs.getString("descripcion"));
                lista.add(e);
            }
        }
        return lista;
    }

    public EstadoPedido buscarPorId(int id) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EstadoPedido e = new EstadoPedido();
                    e.setIdEstado(rs.getInt("id_estado"));
                    e.setNombre(rs.getString("nombre"));
                    e.setDescripcion(rs.getString("descripcion"));
                    return e;
                }
            }
        }
        return null;
    }
}
