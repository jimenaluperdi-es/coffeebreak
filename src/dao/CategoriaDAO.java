package dao;

import beans.Categoria;
import conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    private static final String INSERT = "INSERT INTO CATEGORIA (nombre_categoria) VALUES (?)";
    private static final String UPDATE = "UPDATE CATEGORIA SET nombre_categoria=? WHERE id_categoria=?";
    private static final String DELETE = "DELETE FROM CATEGORIA WHERE id_categoria=?";
    private static final String SELECT_ALL = "SELECT * FROM CATEGORIA";
    private static final String SELECT_BY_ID = "SELECT * FROM CATEGORIA WHERE id_categoria=?";

    public void insertar(Categoria categoria) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNombreCategoria());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setIdCategoria(rs.getInt(1));
                }
            }
        }
    }

    public void actualizar(Categoria categoria) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, categoria.getNombreCategoria());
            ps.setInt(2, categoria.getIdCategoria());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Categoria> listarTodos() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_ALL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categoria c = new Categoria();
                c.setIdCategoria(rs.getInt("id_categoria"));
                c.setNombreCategoria(rs.getString("nombre_categoria"));
                lista.add(c);
            }
        }
        return lista;
    }

    public Categoria buscarPorId(int id) throws SQLException {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Categoria c = new Categoria();
                    c.setIdCategoria(rs.getInt("id_categoria"));
                    c.setNombreCategoria(rs.getString("nombre_categoria"));
                    return c;
                }
            }
        }
        return null;
    }
}
