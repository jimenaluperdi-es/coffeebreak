package beans;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Pedido {

    private int idPedido;
    private int idCliente;
    private LocalDateTime fechaPedido;
    private int idEstado;
    private LocalDate fechaPago;
    private double monto;
    private String metodoPago;
    private String nombreCliente;
    private String nombreEstado;

    public Pedido() {
    }

    public Pedido(int idPedido, int idCliente, LocalDateTime fechaPedido, int idEstado, LocalDate fechaPago, double monto, String metodoPago) {
        this.idPedido = idPedido;
        this.idCliente = idCliente;
        this.fechaPedido = fechaPedido;
        this.idEstado = idEstado;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.metodoPago = metodoPago;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    @Override
    public String toString() {
        return "Pedido{" + "idPedido=" + idPedido + ", idCliente=" + idCliente + ", fechaPedido=" + fechaPedido + ", monto=" + monto + '}';
    }
}
