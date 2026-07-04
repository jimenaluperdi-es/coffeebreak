package dao;

import beans.Producto;
import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private static final String INSERT = "INSERT INTO PRODUCTO (nombre_producto, descripcion, precio, disponible, id_categoria) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE PRODUCTO SET nombre_producto=?, descripcion=?, precio=?, disponible=?, id_categoria=? WHERE id_producto=?";
    private static final String DELETE = "DELETE FROM PRODUCTO WHERE id_producto=?";
    private static final String SELECT_ALL = "SELECT p.*, c.nombre_categoria FROM PRODUCTO p JOIN CATEGORIA c ON p.id_categoria = c.id_categoria";
    private static final String SELECT_BY_ID = "SELECT p.*, c.nombre_categoria FROM PRODUCTO p JOIN CATEGORIA c ON p.id_categoria = c.id_categoria WHERE p.id_producto=?";
    private static final String SELECT_BY_CATEGORIA = "SELECT p.*, c.nombre_categoria FROM PRODUCTO p JOIN CATEGORIA c ON p.id_categoria = c.id_categoria WHERE p.id_categoria=? AND p.disponible=TRUE";
    private static final String SELECT_DISPONIBLES = "SELECT p.*, c.nombre_categoria FROM PRODUCTO p JOIN CATEGORIA c ON p.id_categoria = c.id_categoria WHERE p.disponible=TRUE";
    private static final String BUSCAR = "SELECT p.*, c.nombre_categoria FROM PRODUCTO p JOIN CATEGORIA c ON p.id_categoria = c.id_categoria WHERE p.disponible=TRUE AND p.nombre_producto LIKE ?";

    public void insertar(Producto producto) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, producto.getNombreProducto());
            ps.setString(2, producto.getDescripcion());
            ps.setDouble(3, producto.getPrecio());
            ps.setBoolean(4, producto.isDisponible());
            ps.setInt(5, producto.getIdCategoria());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    producto.setIdProducto(rs.getInt(1));
                }
            }
        }
    }

    public void actualizar(Producto producto) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, producto.getNombreProducto());
            ps.setString(2, producto.getDescripcion());
            ps.setDouble(3, producto.getPrecio());
            ps.setBoolean(4, producto.isDisponible());
            ps.setInt(5, producto.getIdCategoria());
            ps.setInt(6, producto.getIdProducto());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Producto> listarTodos() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_ALL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Producto> listarDisponibles() throws SQLException {
        List<Producto> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_DISPONIBLES); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Producto> listarPorCategoria(int idCategoria) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_CATEGORIA)) {
            ps.setInt(1, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public Producto buscarPorId(int id) throws SQLException {
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

    public List<Producto> buscar(String termino) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(BUSCAR)) {
            ps.setString(1, "%" + termino + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("id_producto"));
        p.setNombreProducto(rs.getString("nombre_producto"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecio(rs.getDouble("precio"));
        p.setDisponible(rs.getBoolean("disponible"));
        p.setIdCategoria(rs.getInt("id_categoria"));
        p.setNombreCategoria(rs.getString("nombre_categoria"));
        return p;
    }
}
