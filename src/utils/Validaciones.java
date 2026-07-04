package utils;

public class Validaciones {

    private Validaciones() {
    }

    public static boolean esEmailValido(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$");
    }

    public static boolean esContrasenaValida(String contrasena) {
        return contrasena != null && contrasena.length() >= 8;
    }

    public static boolean esCampoVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static boolean esCantidadPositiva(int cantidad) {
        return cantidad > 0;
    }

    public static boolean esDoublePositivo(double valor) {
        return valor > 0;
    }
}
