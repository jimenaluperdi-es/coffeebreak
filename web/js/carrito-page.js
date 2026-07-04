document.addEventListener('DOMContentLoaded', function() {
    renderizarCarrito();
});

function renderizarCarrito() {
    const items = Carrito.obtener();
    const container = document.getElementById('carritoItems');
    const vacio = document.getElementById('carritoVacio');
    const contenido = document.getElementById('carritoContenido');
    const resumen = document.getElementById('resumenContainer');
    const btnPagar = document.getElementById('irAPagarBtn');

    if (!items || items.length === 0) {
        if (vacio) vacio.style.display = 'block';
        if (contenido) contenido.style.display = 'none';
        return;
    }

    if (vacio) vacio.style.display = 'none';
    if (contenido) contenido.style.display = 'block';
    if (resumen) resumen.style.display = 'block';
    container.innerHTML = '';

    items.forEach(item => {
        const iconos = {1:'bi-cup-hot-fill', 2:'bi-droplet-fill', 3:'bi-basket-fill', 4:'bi-egg-fried'};
        const icono = iconos[item.idCategoria] || item.imagen || 'bi-box-seam-fill';
        const div = document.createElement('div');
        div.className = 'cart-item-card';
        div.innerHTML = `
            <div class="item-icon"><i class="bi ${icono}"></i></div>
            <div class="item-info">
                <div class="item-name">${item.nombre}</div>
                <div class="item-price">${item.precio.toFixed(2)} € / ud.</div>
            </div>
            <div class="item-qty">
                <button onclick="Carrito.actualizarCantidad(${item.idProducto}, ${item.cantidad - 1})">−</button>
                <span>${item.cantidad}</span>
                <button onclick="Carrito.actualizarCantidad(${item.idProducto}, ${item.cantidad + 1})">+</button>
            </div>
            <div class="item-total">${(item.precio * item.cantidad).toFixed(2)} €</div>
            <button class="item-remove" onclick="Carrito.eliminar(${item.idProducto})"><i class="bi bi-x-lg"></i></button>
        `;
        container.appendChild(div);
    });

    document.getElementById('resumenSubtotal').textContent = Carrito.calcularSubtotal().toFixed(2) + ' €';
    document.getElementById('resumenIVA').textContent = Carrito.calcularIVA().toFixed(2) + ' €';
    document.getElementById('resumenTotal').textContent = Carrito.calcularTotal().toFixed(2) + ' €';
}
