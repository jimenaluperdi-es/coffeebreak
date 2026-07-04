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
                    <p class="text-muted">Inicia sesión para ver tu historial de pedidos</p>
                    <a href="login.html" class="btn btn-primary mt-3">Iniciar Sesión</a>
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
                <p class="text-muted">Realiza tu primer pedido desde el menú</p>
                <a href="menu.html" class="btn btn-primary mt-3">Ver Menú</a>
            </div>
        `;
        return;
    }
    container.innerHTML = '';
    pedidos.forEach(pedido => {
        const estadoClass = obtenerEstadoClass(pedido.nombreEstado);
        const estadoIcono = obtenerIconoEstado(pedido.nombreEstado);
        const stepperHtml = generarStepper(pedido.nombreEstado);
        const div = document.createElement('div');
        div.className = 'card mb-3 animacion-fade';
        div.innerHTML = `
            <div class="card-body">
                <div class="row align-items-center">
                    <div class="col-md-3">
                        <small class="text-muted">Pedido #${pedido.idPedido}</small>
                        <div class="fw-bold">${formatearFecha(pedido.fechaPedido)}</div>
                    </div>
                    <div class="col-md-3">
                        ${stepperHtml}
                    </div>
                    <div class="col-md-2">
                        <div class="fw-bold">${pedido.monto ? pedido.monto.toFixed(2) + ' €' : '—'}</div>
                    </div>
                    <div class="col-md-2 text-center">
                        <div class="estado-dot ${estadoClass}">${estadoIcono}</div>
                    </div>
                    <div class="col-md-2 text-end">
                        <button class="btn btn-outline-primary btn-sm" onclick="verDetallePedido(${pedido.idPedido})">
                            <i class="bi bi-eye me-1"></i>Ver
                        </button>
                    </div>
                </div>
            </div>
        `;
        container.appendChild(div);
    });
    container.insertAdjacentHTML('beforeend', generarLeyendaEstados());
}

function obtenerIconoEstado(estado) {
    const iconos = {
        'Pendiente': '<i class="bi bi-hourglass"></i>',
        'En preparación': '<i class="bi bi-gear-wide-connected"></i>',
        'Listo para recoger': '<i class="bi bi-bell-fill"></i>',
        'Entregado': '<i class="bi bi-check-all"></i>',
    };
    return iconos[estado] || '<i class="bi bi-question-circle"></i>';
}

function generarLeyendaEstados() {
    const estados = [
        { nombre: 'Pendiente', icono: 'bi bi-hourglass' },
        { nombre: 'En preparación', icono: 'bi bi-gear-wide-connected' },
        { nombre: 'Listo para recoger', icono: 'bi bi-bell-fill' },
        { nombre: 'Entregado', icono: 'bi bi-check-all' },
    ];
    let html = '<div class="estado-leyenda mt-4 p-3"><small class="fw-bold text-muted me-3">Leyenda de estados:</small>';
    estados.forEach(e => {
        const cls = obtenerEstadoClass(e.nombre);
        html += `<span class="leyenda-item me-3"><span class="estado-dot ${cls}"><i class="${e.icono}"></i></span> ${e.nombre}</span>`;
    });
    html += '</div>';
    return html;
}

const STEPS = ['Pendiente', 'En preparación', 'Listo para recoger', 'Entregado'];

function generarStepper(estadoActual) {
    const idx = STEPS.indexOf(estadoActual);
    if (idx === -1) return '';

    let html = '<div class="progress-stepper">';
    STEPS.forEach((step, i) => {
        let cls = 'step';
        if (i < idx) cls += ' completed';
        else if (i === idx) cls += ' active';

        const icon = i < idx ? '<i class="bi bi-check-lg"></i>' : i + 1;

        html += `
            <div class="step-wrapper">
                <div class="${cls}">
                    <div class="step-circle">${icon}</div>
                    <div class="step-label">${step}</div>
                </div>`;
        if (i < STEPS.length - 1) {
            let connCls = 'connector';
            if (i < idx) connCls += ' completed';
            else if (i === idx) connCls += ' active';
            html += `<div class="${connCls}"></div>`;
        }
        html += '</div>';
    });
    html += '</div>';
    return html;
}

function obtenerEstadoClass(estado) {
    const clases = {
        'Pendiente': 'estado-pendiente',
        'En preparación': 'estado-preparacion',
        'Listo para recoger': 'estado-listo',
        'Entregado': 'estado-entregado',
    };
    return clases[estado] || 'estado-pendiente';
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
        const estadoIcono = obtenerIconoEstado(pedido.nombreEstado);
        const stepperHtml = generarStepper(pedido.nombreEstado);
        let html = `
            <div class="mb-3">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <div>
                        <small class="text-muted">Pedido #${pedido.idPedido}</small>
                        <div class="fw-bold">${formatearFecha(pedido.fechaPedido)}</div>
                    </div>
                    <span class="estado-badge ${estadoClass}">${estadoIcono} ${pedido.nombreEstado}</span>
                </div>
                <div class="mt-3 p-3" style="background:#f8f9fa;border-radius:12px;">
                    ${stepperHtml}
                </div>
            </div>
            <hr>
            <h6 class="fw-bold mb-3">Productos</h6>
        `;
        detalles.forEach(det => {
            html += `
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <div>
                        <div class="fw-medium">${det.nombreProducto || 'Producto'}</div>
                        <small class="text-muted">${det.cantidad} x ${det.precioProducto ? det.precioProducto.toFixed(2) + ' €' : '—'}</small>
                    </div>
                    <div class="fw-bold">${det.precioProducto ? (det.cantidad * det.precioProducto).toFixed(2) + ' €' : '—'}</div>
                </div>
            `;
        });
        html += `
            <hr>
            <div class="d-flex justify-content-between">
                <span class="text-muted">Subtotal</span>
                <span>${pedido.monto ? (pedido.monto / 1.1).toFixed(2) + ' €' : '—'}</span>
            </div>
            <div class="d-flex justify-content-between">
                <span class="text-muted">IVA (10%)</span>
                <span>${pedido.monto ? (pedido.monto - pedido.monto / 1.1).toFixed(2) + ' €' : '—'}</span>
            </div>
            <div class="d-flex justify-content-between fw-bold fs-5 mt-2">
                <span>Total</span>
                <span>${pedido.monto ? pedido.monto.toFixed(2) + ' €' : '—'}</span>
            </div>
            ${pedido.metodoPago ? `<div class="mt-2"><small class="text-muted">Método de pago: ${pedido.metodoPago}</small></div>` : ''}
        `;
        modalBody.innerHTML = html;
        const modal = new bootstrap.Modal(document.getElementById('detallePedidoModal'));
        modal.show();
    } catch (error) {
        Toast.mostrar('Error al cargar detalle del pedido: ' + error.message, 'danger');
    } finally {
        ocultarLoader();
    }
}
