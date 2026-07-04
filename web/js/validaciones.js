const Validaciones = {
    esEmailValido: function(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    },
    esContrasenaValida: function(contrasena) {
        return contrasena && contrasena.length >= 8;
    },
    esCampoVacio: function(valor) {
        return !valor || valor.trim() === '';
    },
    esCantidadValida: function(cantidad) {
        return Number.isInteger(cantidad) && cantidad > 0;
    },
    mostrarError: function(mensaje) {
        Toast.mostrar(mensaje, 'danger');
    },
    mostrarExito: function(mensaje) {
        Toast.mostrar(mensaje, 'success');
    },
};

const Toast = {
    mostrar: function(mensaje, tipo = 'success') {
        const container = document.getElementById('toastContainer');
        if (!container) return;
        const bgClass = tipo === 'success' ? 'bg-success' : tipo === 'danger' ? 'bg-danger' : tipo === 'warning' ? 'bg-warning' : 'bg-info';
        const icono = tipo === 'success' ? 'bi-check-circle' : tipo === 'danger' ? 'bi-exclamation-circle' : tipo === 'warning' ? 'bi-exclamation-triangle' : 'bi-info-circle';
        const toast = document.createElement('div');
        toast.className = `toast align-items-center text-white ${bgClass} border-0 show`;
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');
        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    <i class="bi ${icono} me-2"></i>${mensaje}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        `;
        container.appendChild(toast);
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    },
};
