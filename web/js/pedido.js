document.addEventListener('DOMContentLoaded', function() {
    renderizarResumenCarrito();
    configurarMetodosPago();
    document.getElementById('confirmarPedidoBtn').addEventListener('click', confirmarPedido);
});

function renderizarResumenCarrito() {
    const items = Carrito.obtener();
    const container = document.getElementById('resumenItems');
    if (!container) return;

    if (!items || items.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="bi bi-cart-x"></i>
                <h4>Tu carrito está vacío</h4>
                <p>Añade productos desde el menú</p>
                <a href="menu.html" class="btn-primary-custom" style="display:inline-block; width:auto; padding:0.7rem 1.5rem; text-decoration:none;">Ver Menú</a>
            </div>
        `;
        document.getElementById('confirmarPedidoBtn').disabled = true;
        return;
    }

    document.getElementById('confirmarPedidoBtn').disabled = false;
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
                <button onclick="Carrito.actualizarCantidad(${item.idProducto}, ${item.cantidad - 1}); renderizarResumenCarrito();">−</button>
                <span>${item.cantidad}</span>
                <button onclick="Carrito.actualizarCantidad(${item.idProducto}, ${item.cantidad + 1}); renderizarResumenCarrito();">+</button>
            </div>
            <div class="item-total">${(item.precio * item.cantidad).toFixed(2)} €</div>
        `;
        container.appendChild(div);
    });

    actualizarResumen();
}

function actualizarResumen() {
    const subtotalEl = document.getElementById('resumenSubtotal');
    const ivaEl = document.getElementById('resumenIVA');
    const totalEl = document.getElementById('resumenTotalFinal');
    if (subtotalEl) subtotalEl.textContent = Carrito.calcularSubtotal().toFixed(2) + ' €';
    if (ivaEl) ivaEl.textContent = Carrito.calcularIVA().toFixed(2) + ' €';
    if (totalEl) totalEl.textContent = Carrito.calcularTotal().toFixed(2) + ' €';
}

function configurarMetodosPago() {
    const metodos = document.querySelectorAll('.payment-card');
    metodos.forEach(metodo => {
        metodo.addEventListener('click', function() {
            metodos.forEach(m => m.classList.remove('selected'));
            this.classList.add('selected');
            document.getElementById('metodoPago').value = this.dataset.metodo;
        });
    });
}

async function confirmarPedido() {
    const items = Carrito.obtener();
    if (!items || items.length === 0) {
        Toast.mostrar('El carrito está vacío', 'warning');
        return;
    }
    const metodoPago = document.getElementById('metodoPago').value;
    if (!metodoPago) {
        Toast.mostrar('Selecciona un método de pago', 'warning');
        return;
    }
    mostrarLoader();
    try {
        const data = await API.get(API.getBaseUrl() + '/api/sesion');
        if (!data.autenticado) {
            Toast.mostrar('Debes iniciar sesión para realizar un pedido', 'warning');
            setTimeout(() => { window.location.href = 'login.html'; }, 1200);
            return;
        }
        const pedidoData = {
            metodoPago: metodoPago,
            monto: Carrito.calcularTotal(),
            items: items.map(item => ({
                idProducto: item.idProducto,
                cantidad: item.cantidad,
            })),
        };
        await API.post(API.getBaseUrl() + '/api/pedido', pedidoData);
        Toast.mostrar('Pedido realizado con éxito!', 'success');
        Carrito.vaciar();
        setTimeout(() => { window.location.href = 'historial.html'; }, 1200);
    } catch (error) {
        Toast.mostrar('Error: ' + error.message, 'danger');
    } finally {
        ocultarLoader();
    }
}
