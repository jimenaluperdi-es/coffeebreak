package beans;

public class EstadoPedido {

    private int idEstado;
    private String nombre;
    private String descripcion;

    public EstadoPedido() {
    }

    public EstadoPedido(int idEstado, String nombre, String descripcion) {
        this.idEstado = idEstado;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "EstadoPedido{" + "idEstado=" + idEstado + ", nombre=" + nombre + '}';
    }
}
