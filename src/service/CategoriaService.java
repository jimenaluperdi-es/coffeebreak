package service;

import beans.Categoria;
import dao.CategoriaDAO;
import java.sql.SQLException;
import java.util.List;

public class CategoriaService {

    private final CategoriaDAO categoriaDAO;

    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAO();
    }

    public List<Categoria> listarTodas() throws SQLException {
        return categoriaDAO.listarTodos();
    }

    public Categoria buscarPorId(int id) throws SQLException {
        return categoriaDAO.buscarPorId(id);
    }
}
