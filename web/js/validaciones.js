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
};

const Toast = {
    mostrar: function(mensaje, tipo = 'success') {
        const container = document.getElementById('toastContainer');
        if (!container) return;
        const icono = tipo === 'success' ? 'bi-check-circle-fill' : tipo === 'danger' ? 'bi-x-circle-fill' : tipo === 'warning' ? 'bi-exclamation-triangle-fill' : 'bi-info-circle-fill';
        const toast = document.createElement('div');
        toast.className = `toast-custom ${tipo}`;
        toast.innerHTML = `<i class="bi ${icono}"></i> ${mensaje}`;
        container.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 3500);
    },
};
