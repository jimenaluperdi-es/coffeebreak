document.addEventListener('DOMContentLoaded', function() {
    cargarHistorial();
});

async function cargarHistorial() {
    mostrarLoader();
    try {
        const data = await API.get(API.getBaseUrl() + '/api/sesion');
        if (!data.autenticado) {
            document.getElementById('historialContainer').innerHTML = `
                <div class="empty-state">
                    <i class="bi bi-person-lock"></i>
                    <h4>Debes iniciar sesión</h4>
                    <p>Inicia sesión para ver tu historial de pedidos</p>
                    <a href="login.html" class="btn-primary-custom" style="display:inline-block; width:auto; padding:0.7rem 1.5rem; text-decoration:none;">Iniciar Sesión</a>
                </div>
            `;
            return;
        }
        const pedidos = await API.get(API.getBaseUrl() + '/api/pedidos');
        renderizarHistorial(pedidos);
    } catch (error) {
        Toast.mostrar('Error al cargar historial: ' + error.message, 'danger');
    } finally {
        ocultarLoader();
    }
}

function renderizarHistorial(pedidos) {
    const container = document.getElementById('historialContainer');
    if (!container) return;
    if (!pedidos || pedidos.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="bi bi-clock-history"></i>
                <h4>No tienes pedidos aún</h4>
                <p>Realiza tu primer pedido desde el menú</p>
                <a href="menu.html" class="btn-primary-custom" style="display:inline-block; width:auto; padding:0.7rem 1.5rem; text-decoration:none;">Ver Menú</a>
            </div>
        `;
        return;
    }
    container.innerHTML = '';
    pedidos.forEach(pedido => {
        const div = document.createElement('div');
        div.className = 'order-card';
        div.innerHTML = `
            <div class="order-header">
                <div class="order-id">Pedido <strong>#${pedido.idPedido}</strong></div>
                <span class="order-status ${obtenerEstadoClass(pedido.nombreEstado)}">${pedido.nombreEstado}</span>
            </div>
            <div class="step-progress">
                ${renderStepProgress(pedido.nombreEstado)}
            </div>
            <div class="order-details">
                <div><strong>${formatearFecha(pedido.fechaPedido)}</strong></div>
                <div>${pedido.metodoPago || '—'} · ${pedido.monto ? pedido.monto.toFixed(2) + ' €' : '—'}</div>
            </div>
            <div class="order-actions">
                <button class="btn-outline-custom" onclick="verDetallePedido(${pedido.idPedido})">
                    <i class="bi bi-eye me-1"></i>Ver detalle
                </button>
                <button class="btn-outline-custom danger" onclick="cancelarPedido(${pedido.idPedido})">
                    <i class="bi bi-x-lg me-1"></i>Cancelar
                </button>
            </div>
        `;
        container.appendChild(div);
    });
}

function renderStepProgress(estadoActual) {
    const pasos = [
        { id: 'Pendiente', label: 'Recibido' },
        { id: 'En preparaci\u00f3n', label: 'Preparando' },
        { id: 'Listo para recoger', label: 'Listo' },
        { id: 'Entregado', label: 'Entregado' },
    ];
    let currentIndex = -1;
    pasos.forEach((p, i) => {
        if (p.id === estadoActual) currentIndex = i;
    });
    if (currentIndex === -1) {
        return '<div style="font-size:0.75rem; color:var(--texto-secundario);">Estado no disponible</div>';
    }
    return pasos.map((paso, i) => {
        let cls = '';
        if (i < currentIndex) cls = 'completed';
        else if (i === currentIndex) cls = 'active';
        const icon = i < currentIndex ? '<i class="bi bi-check" style="font-size:0.55rem;"></i>' : '';
        return `
            <div class="step ${cls}">
                <div class="step-dot">${icon}</div>
                <div class="step-line"></div>
                <div class="step-label">${paso.label}</div>
            </div>
        `;
    }).join('');
}

function obtenerEstadoClass(estado) {
    const clases = {
        'Pendiente': 'status-pendiente',
        'En preparaci\u00f3n': 'status-preparacion',
        'Listo para recoger': 'status-listo',
        'Entregado': 'status-entregado',
    };
    return clases[estado] || 'status-pendiente';
}

function formatearFecha(fechaStr) {
    if (!fechaStr) return '—';
    const fecha = new Date(fechaStr);
    return fecha.toLocaleDateString('es-ES', {
        year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit',
    });
}

async function verDetallePedido(idPedido) {
    mostrarLoader();
    try {
        const data = await API.get(API.getBaseUrl() + '/api/pedido/' + idPedido);
        const modalBody = document.getElementById('detallePedidoBody');
        if (!modalBody) return;
        const pedido = data.pedido;
        const detalles = data.detalles || [];
        const estadoClass = obtenerEstadoClass(pedido.nombreEstado);
        let html = `
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:1rem;">
                <div>
                    <small style="color:var(--texto-secundario);">Pedido #${pedido.idPedido}</small>
                    <div style="font-weight:600;">${formatearFecha(pedido.fechaPedido)}</div>
                </div>
                <span class="order-status ${estadoClass}">${pedido.nombreEstado}</span>
            </div>
            <div style="margin-bottom:1rem;">
                ${renderStepProgress(pedido.nombreEstado)}
            </div>
            <hr style="border-color:var(--avena-oscuro);">
            <h6 style="font-weight:700; margin-bottom:0.75rem; color:var(--espresso);">Productos</h6>
        `;
        detalles.forEach(det => {
            html += `
                <div style="display:flex; justify-content:space-between; align-items:center; padding:0.35rem 0;">
                    <div>
                        <div style="font-weight:500; font-size:0.85rem;">${det.nombreProducto || 'Producto'}</div>
                        <small style="color:var(--texto-secundario);">${det.cantidad} x ${det.precioProducto ? det.precioProducto.toFixed(2) + ' €' : '—'}</small>
                    </div>
                    <div style="font-weight:700; font-size:0.85rem;">${det.precioProducto ? (det.cantidad * det.precioProducto).toFixed(2) + ' €' : '—'}</div>
                </div>
            `;
        });
        const subtotal = pedido.monto ? pedido.monto / 1.1 : 0;
        const iva = pedido.monto ? pedido.monto - subtotal : 0;
        html += `
            <hr style="border-color:var(--avena-oscuro);">
            <div style="display:flex; justify-content:space-between; font-size:0.85rem; color:var(--texto-secundario); padding:0.2rem 0;">
                <span>Subtotal</span>
                <span>${subtotal.toFixed(2)} €</span>
            </div>
            <div style="display:flex; justify-content:space-between; font-size:0.85rem; color:var(--texto-secundario); padding:0.2rem 0;">
                <span>IVA (10%)</span>
                <span>${iva.toFixed(2)} €</span>
            </div>
            <div style="display:flex; justify-content:space-between; font-weight:800; font-size:1.1rem; padding:0.5rem 0 0; margin-top:0.25rem; border-top:1.5px solid var(--avena-oscuro);">
                <span>Total</span>
                <span>${pedido.monto ? pedido.monto.toFixed(2) + ' €' : '—'}</span>
            </div>
            ${pedido.metodoPago ? `<div style="margin-top:0.75rem; font-size:0.8rem; color:var(--texto-secundario);">Método de pago: <strong>${pedido.metodoPago}</strong></div>` : ''}
        `;
        modalBody.innerHTML = html;
        const modal = new bootstrap.Modal(document.getElementById('detallePedidoModal'));
        modal.show();
    } catch (error) {
        Toast.mostrar('Error al cargar detalle: ' + error.message, 'danger');
    } finally {
        ocultarLoader();
    }
}

async function cancelarPedido(idPedido) {
    if (!confirm('¿Cancelar este pedido?')) return;
    mostrarLoader();
    try {
        await API.del(API.getBaseUrl() + '/api/pedido/' + idPedido);
        Toast.mostrar('Pedido cancelado', 'success');
        cargarHistorial();
    } catch (error) {
        Toast.mostrar('Error: ' + error.message, 'danger');
    } finally {
        ocultarLoader();
    }
}
