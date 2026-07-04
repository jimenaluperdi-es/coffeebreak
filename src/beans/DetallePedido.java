package beans;

public class DetallePedido {

    private int idDetalle;
    private int idPedido;
    private int idProducto;
    private int cantidad;
    private String nombreProducto;
    private double precioProducto;

    public DetallePedido() {
    }

    public DetallePedido(int idDetalle, int idPedido, int idProducto, int cantidad) {
        this.idDetalle = idDetalle;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(double precioProducto) {
        this.precioProducto = precioProducto;
    }

    @Override
    public String toString() {
        return "DetallePedido{" + "idDetalle=" + idDetalle + ", idPedido=" + idPedido + ", idProducto=" + idProducto + ", cantidad=" + cantidad + '}';
    }
}
