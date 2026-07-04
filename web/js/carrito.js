const Carrito = {
    key: 'coffeebreak_carrito',
    obtener: function() {
        const data = localStorage.getItem(this.key);
        return data ? JSON.parse(data) : [];
    },
    guardar: function(items) {
        localStorage.setItem(this.key, JSON.stringify(items));
        this.actualizarBadge();
    },
    agregar: function(producto, cantidad = 1) {
        let items = this.obtener();
        const existente = items.find(item => item.idProducto === producto.idProducto);
        if (existente) {
            existente.cantidad += cantidad;
        } else {
            items.push({
                idProducto: producto.idProducto,
                nombre: producto.nombreProducto,
                precio: producto.precio,
                cantidad: cantidad,
                imagen: producto.imagen || 'bi-cup-hot-fill',
            });
        }
        this.guardar(items);
        Toast.mostrar(`${producto.nombreProducto} añadido al carrito`, 'success');
        this.actualizarBadge();
    },
    eliminar: function(idProducto) {
        let items = this.obtener();
        items = items.filter(item => item.idProducto !== idProducto);
        this.guardar(items);
        if (typeof renderizarCarrito === 'function') {
            renderizarCarrito();
        }
        Toast.mostrar('Producto eliminado del carrito', 'warning');
    },
    actualizarCantidad: function(idProducto, cantidad) {
        let items = this.obtener();
        const item = items.find(i => i.idProducto === idProducto);
        if (item) {
            if (cantidad <= 0) {
                this.eliminar(idProducto);
                return;
            }
            item.cantidad = cantidad;
            this.guardar(items);
            if (typeof renderizarCarrito === 'function') {
                renderizarCarrito();
            }
        }
    },
    vaciar: function() {
        this.guardar([]);
        if (typeof renderizarCarrito === 'function') {
            renderizarCarrito();
        }
    },
    contar: function() {
        const items = this.obtener();
        return items.reduce((total, item) => total + item.cantidad, 0);
    },
    calcularSubtotal: function() {
        const items = this.obtener();
        return items.reduce((total, item) => total + (item.precio * item.cantidad), 0);
    },
    calcularIVA: function() {
        return this.calcularSubtotal() * 0.10;
    },
    calcularTotal: function() {
        return this.calcularSubtotal() + this.calcularIVA();
    },
    actualizarBadge: function() {
        const badges = document.querySelectorAll('.carrito-badge');
        const count = this.contar();
        badges.forEach(badge => {
            badge.textContent = count;
            badge.style.display = count > 0 ? 'inline' : 'none';
        });
    },
    iniciar: function() {
        this.actualizarBadge();
    },
};

document.addEventListener('DOMContentLoaded', function() {
    Carrito.iniciar();
});
