document.addEventListener('DOMContentLoaded', function() {
    cargarPedidosCocina();
    setInterval(cargarPedidosCocina, 15000);
});

async function cargarPedidosCocina() {
    try {
        const pedidos = await API.get(API.getBaseUrl() + '/api/pedidos');
        renderizarPedidosCocina(pedidos);
    } catch (error) {
        console.error('Error cargando pedidos de cocina:', error);
    }
}

function renderizarPedidosCocina(pedidos) {
    const container = document.getElementById('kitchenOrdersContainer');
    if (!container) return;

    const activos = (pedidos || []).filter(p =>
        p.nombreEstado === 'Pendiente' || p.nombreEstado === 'En preparaci\u00f3n'
    );
    const pendientes = (pedidos || []).filter(p => p.nombreEstado === 'Pendiente');
    const enPreparacion = (pedidos || []).filter(p => p.nombreEstado === 'En preparaci\u00f3n');
    const procesados = (pedidos || []).filter(p =>
        p.nombreEstado === 'Listo para recoger' || p.nombreEstado === 'Entregado'
    );

    document.getElementById('orderCountBadge').textContent = activos.length + ' pedidos';
    document.getElementById('statProcesados').textContent = procesados.length;
    document.getElementById('statPendientes').textContent = pendientes.length;
    document.getElementById('statEnPreparacion').textContent = enPreparacion.length;
    document.getElementById('statHoy').textContent = pedidos ? pedidos.length : 0;

    const capacity = Math.min(100, activos.length * 20);
    document.getElementById('capacityBar').style.width = capacity + '%';
    document.getElementById('capacityLabel').textContent = capacity + '%';
    const queueMin = Math.max(2, activos.length * 5);
    document.getElementById('queueBar').style.width = Math.min(100, queueMin * 5) + '%';
    document.getElementById('queueLabel').textContent = '~' + queueMin + ' min';

    if (!activos || activos.length === 0) {
        container.innerHTML = `
            <div style="grid-column:1/-1; text-align:center; padding:3rem 1rem; color:var(--texto-secundario);">
                <i class="bi bi-inbox" style="font-size:2.5rem; display:block; margin-bottom:0.5rem;"></i>
                <p style="font-weight:600;">No hay pedidos activos</p>
            </div>
        `;
        return;
    }

    container.innerHTML = '';
    activos.forEach(pedido => {
        const esPendiente = pedido.nombreEstado === 'Pendiente';
        const div = document.createElement('div');
        div.className = 'kitchen-order-card';
        div.innerHTML = `
            <h3>#${pedido.idPedido}</h3>
            <div class="ko-customer"><i class="bi bi-person me-1"></i>${pedido.nombreCliente || 'Cliente'}</div>
            <ul class="ko-items" id="koItems_${pedido.idPedido}">
                <li style="font-size:0.75rem; color:var(--texto-claro);">Cargando productos...</li>
            </ul>
            <div class="ko-meta">
                <span><i class="bi bi-credit-card me-1"></i>${pedido.metodoPago || '—'}</span>
                <span>${pedido.monto ? pedido.monto.toFixed(2) + ' €' : '—'}</span>
            </div>
            <div class="ko-actions">
                ${esPendiente ?
                    `<button class="btn-kitchen send" onclick="enviarACocina(${pedido.idPedido})"><i class="bi bi-arrow-right me-1"></i>Enviar a cocina</button>` :
                    `<button class="btn-kitchen ready" onclick="marcarListo(${pedido.idPedido})"><i class="bi bi-check-lg me-1"></i>Marcar listo</button>`
                }
            </div>
        `;
        container.appendChild(div);
        cargarDetallesPedido(pedido.idPedido);
    });
}

async function cargarDetallesPedido(idPedido) {
    try {
        const data = await API.get(API.getBaseUrl() + '/api/pedido/' + idPedido);
        const detalles = data.detalles || [];
        const ul = document.getElementById('koItems_' + idPedido);
        if (!ul) return;
        ul.innerHTML = detalles.map(det =>
            `<li><span>${det.cantidad}x ${det.nombreProducto || 'Producto'}</span></li>`
        ).join('');
    } catch (error) {
        console.error('Error cargando detalles:', error);
    }
}

async function enviarACocina(idPedido) {
    mostrarLoader();
    try {
        await API.put(API.getBaseUrl() + '/api/pedido/' + idPedido, { idEstado: 2 });
        Toast.mostrar('Pedido #' + idPedido + ' enviado a cocina', 'success');
        cargarPedidosCocina();
    } catch (error) {
        Toast.mostrar('Error: ' + error.message, 'danger');
    } finally {
        ocultarLoader();
    }
}

async function marcarListo(idPedido) {
    mostrarLoader();
    try {
        await API.put(API.getBaseUrl() + '/api/pedido/' + idPedido, { idEstado: 3 });
        Toast.mostrar('Pedido #' + idPedido + ' marcado como listo', 'success');
        cargarPedidosCocina();
    } catch (error) {
        Toast.mostrar('Error: ' + error.message, 'danger');
    } finally {
        ocultarLoader();
    }
}
