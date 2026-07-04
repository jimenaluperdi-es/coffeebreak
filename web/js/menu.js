let productosGlobales = [];
let categoriaActiva = 'todas';

document.addEventListener('DOMContentLoaded', function() {
    cargarCategorias();
    cargarProductos();
    document.getElementById('busquedaInput').addEventListener('input', function() {
        filtrarProductos();
    });
});

async function cargarCategorias() {
    try {
        const categorias = await API.get(API.getBaseUrl() + '/api/categorias');
        const container = document.getElementById('categoriasContainer');
        if (!container) return;
        container.innerHTML = '<button class="categoria-btn active me-2 mb-2" onclick="filtrarPorCategoria(\'todas\', this)">Todas</button>';
        categorias.forEach(cat => {
            container.innerHTML += `<button class="categoria-btn me-2 mb-2" onclick="filtrarPorCategoria(${cat.idCategoria}, this)">${cat.nombreCategoria}</button>`;
        });
    } catch (error) {
        Toast.mostrar('Error al cargar categorías: ' + error.message, 'danger');
    }
}

async function cargarProductos() {
    mostrarLoader();
    try {
        const productos = await API.get(API.getBaseUrl() + '/api/productos');
        productosGlobales = productos;
        renderizarProductos(productos);
    } catch (error) {
        Toast.mostrar('Error al cargar productos: ' + error.message, 'danger');
    } finally {
        ocultarLoader();
    }
}

function renderizarProductos(productos) {
    const container = document.getElementById('productosContainer');
    if (!container) return;
    if (!productos || productos.length === 0) {
        container.innerHTML = `
            <div class="col-12">
                <div class="empty-state">
                    <i class="bi bi-search"></i>
                    <h4>No se encontraron productos</h4>
                    <p class="text-muted">Intenta con otros términos de búsqueda</p>
                </div>
            </div>
        `;
        return;
    }
    container.innerHTML = '';
    productos.forEach(producto => {
        const col = document.createElement('div');
        col.className = 'col-md-6 col-lg-4 col-xl-3 mb-4 animacion-fade';
        col.innerHTML = `
            <div class="card producto-card h-100">
                <div class="card-img-top d-flex align-items-center justify-content-center">
                    <i class="bi ${obtenerIconoProducto(producto.idCategoria)}" style="font-size: 3rem; color: var(--color-cafe);"></i>
                </div>
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">${producto.nombreProducto}</h5>
                    <p class="card-text flex-grow-1">${producto.descripcion || 'Sin descripción'}</p>
                    <p class="precio">${producto.precio.toFixed(2)} €</p>
                    <button class="btn btn-primary w-100" onclick="Carrito.agregar({idProducto: ${producto.idProducto}, nombreProducto: '${producto.nombreProducto.replace(/'/g, "\\'")}', precio: ${producto.precio}}, 1)">
                        <i class="bi bi-cart-plus me-2"></i>Añadir
                    </button>
                </div>
            </div>
        `;
        container.appendChild(col);
    });
}

function obtenerIconoProducto(idCategoria) {
    const iconos = {
        1: 'bi-cup-hot-fill',
        2: 'bi-droplet-fill',
        3: 'bi-basket-fill',
        4: 'bi-egg-fried',
    };
    return iconos[idCategoria] || 'bi-box-seam-fill';
}

function filtrarPorCategoria(categoria, btn) {
    categoriaActiva = categoria;
    document.querySelectorAll('.categoria-btn').forEach(b => b.classList.remove('active'));
    if (btn) btn.classList.add('active');
    filtrarProductos();
}

function filtrarProductos() {
    const busqueda = (document.getElementById('busquedaInput').value || '').toLowerCase().trim();
    let filtrados = productosGlobales;
    if (categoriaActiva !== 'todas') {
        filtrados = filtrados.filter(p => p.idCategoria === parseInt(categoriaActiva));
    }
    if (busqueda) {
        filtrados = filtrados.filter(p =>
            p.nombreProducto.toLowerCase().includes(busqueda) ||
            (p.descripcion && p.descripcion.toLowerCase().includes(busqueda))
        );
    }
    renderizarProductos(filtrados);
}
