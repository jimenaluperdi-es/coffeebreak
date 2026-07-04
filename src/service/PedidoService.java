package service;

import beans.DetallePedido;
import beans.Pedido;
import dao.DetallePedidoDAO;
import dao.PedidoDAO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoService {

    private final PedidoDAO pedidoDAO;
    private final DetallePedidoDAO detallePedidoDAO;

    public PedidoService() {
        this.pedidoDAO = new PedidoDAO();
        this.detallePedidoDAO = new DetallePedidoDAO();
    }

    public Pedido crearPedido(int idCliente, double monto, String metodoPago, List<DetallePedido> detalles) throws SQLException {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("El pedido no puede estar vacío");
        }
        Pedido pedido = new Pedido();
        pedido.setIdCliente(idCliente);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setIdEstado(1);
        pedido.setMonto(monto);
        pedido.setMetodoPago(metodoPago);
        if (metodoPago != null && !metodoPago.isEmpty()) {
            pedido.setFechaPago(LocalDate.now());
        }
        pedidoDAO.insertar(pedido);
        for (DetallePedido detalle : detalles) {
            detalle.setIdPedido(pedido.getIdPedido());
            detallePedidoDAO.insertar(detalle);
        }
        return pedido;
    }

    public Pedido buscarPorId(int id) throws SQLException {
        return pedidoDAO.buscarPorId(id);
    }

    public List<Pedido> listarPorCliente(int idCliente) throws SQLException {
        return pedidoDAO.listarPorCliente(idCliente);
    }

    public Pedido actualizarEstado(int idPedido, int idEstado) throws SQLException {
        pedidoDAO.actualizarEstado(idPedido, idEstado);
        return pedidoDAO.buscarPorId(idPedido);
    }

    public void eliminarPedido(int idPedido) throws SQLException {
        pedidoDAO.eliminar(idPedido);
    }

    public List<DetallePedido> obtenerDetalles(int idPedido) throws SQLException {
        return detallePedidoDAO.listarPorPedido(idPedido);
    }
}
