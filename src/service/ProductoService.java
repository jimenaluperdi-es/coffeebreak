package service;

import beans.Producto;
import dao.ProductoDAO;
import java.sql.SQLException;
import java.util.List;

public class ProductoService {

    private final ProductoDAO productoDAO;

    public ProductoService() {
        this.productoDAO = new ProductoDAO();
    }

    public List<Producto> listarDisponibles() throws SQLException {
        return productoDAO.listarDisponibles();
    }

    public List<Producto> listarPorCategoria(int idCategoria) throws SQLException {
        return productoDAO.listarPorCategoria(idCategoria);
    }

    public Producto buscarPorId(int id) throws SQLException {
        return productoDAO.buscarPorId(id);
    }

    public List<Producto> buscar(String termino) throws SQLException {
        return productoDAO.buscar(termino);
    }

    public List<Producto> listarDestacados() throws SQLException {
        List<Producto> todos = productoDAO.listarDisponibles();
        return todos.size() > 4 ? todos.subList(0, 4) : todos;
    }
}
