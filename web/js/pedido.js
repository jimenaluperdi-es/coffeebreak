document.addEventListener('DOMContentLoaded', function() {
    renderizarResumenCarrito();
    configurarMetodosPago();
    document.getElementById('confirmarPedidoBtn').addEventListener('click', confirmarPedido);
});

function renderizarResumenCarrito() {
    const items = Carrito.obtener();
    const container = document.getElementById('resumenItems');
    const totalContainer = document.getElementById('resumenTotal');
    if (!container) return;
    if (!items || items.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="bi bi-cart-x"></i>
                <h4>Tu carrito está vacío</h4>
                <p class="text-muted">Añade productos desde el menú</p>
                <a href="menu.html" class="btn btn-primary mt-3">Ver Menú</a>
            </div>
        `;
        if (totalContainer) totalContainer.style.display = 'none';
        document.getElementById('confirmarPedidoBtn').disabled = true;
        return;
    }
    document.getElementById('confirmarPedidoBtn').disabled = false;
    if (totalContainer) totalContainer.style.display = 'block';
    container.innerHTML = '';
    items.forEach(item => {
        const div = document.createElement('div');
        div.className = 'carrito-item animacion-fade';
        div.innerHTML = `
            <div class="row align-items-center">
                <div class="col-md-5">
                    <div class="item-nombre">${item.nombre}</div>
                </div>
                <div class="col-md-2">
                    <div class="item-precio">${item.precio.toFixed(2)} €</div>
                </div>
                <div class="col-md-2">
                    <div class="cantidad-control">
                        <button onclick="Carrito.actualizarCantidad(${item.idProducto}, ${item.cantidad - 1})">-</button>
                        <input type="text" value="${item.cantidad}" readonly>
                        <button onclick="Carrito.actualizarCantidad(${item.idProducto}, ${item.cantidad + 1})">+</button>
                    </div>
                </div>
                <div class="col-md-2">
                    <div class="item-precio">${(item.precio * item.cantidad).toFixed(2)} €</div>
                </div>
                <div class="col-md-1">
                    <button class="btn btn-danger btn-sm" onclick="Carrito.eliminar(${item.idProducto})">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </div>
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
    const metodos = document.querySelectorAll('.metodo-pago-card');
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
            setTimeout(() => { window.location.href = 'login.html'; }, 1500);
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
        const resultado = await API.post(API.getBaseUrl() + '/api/pedido', pedidoData);
        Toast.mostrar('Pedido realizado con éxito!', 'success');
        Carrito.vaciar();
        setTimeout(() => { window.location.href = 'historial.html'; }, 1500);
    } catch (error) {
        Toast.mostrar('Error al confirmar pedido: ' + error.message, 'danger');
    } finally {
        ocultarLoader();
    }
}
