let productosGlobales = [];
let categoriaActiva = 'todas';

document.addEventListener('DOMContentLoaded', function() {
    cargarCategorias();
    cargarProductos();
    const searchInput = document.getElementById('busquedaInput');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            filtrarProductos();
        });
    }
});

async function cargarCategorias() {
    try {
        const categorias = await API.get(API.getBaseUrl() + '/api/categorias');
        const container = document.getElementById('categoriasContainer');
        if (!container) return;
        container.innerHTML = '<button class="pill active" onclick="filtrarPorCategoria(\'todas\', this)">Todas</button>';
        categorias.forEach(cat => {
            container.innerHTML += `<button class="pill" onclick="filtrarPorCategoria(${cat.idCategoria}, this)">${cat.nombreCategoria}</button>`;
        });
    } catch (error) {
        Toast.mostrar('Error al cargar categorías', 'danger');
    }
}

async function cargarProductos() {
    mostrarLoader();
    try {
        const productos = await API.get(API.getBaseUrl() + '/api/productos');
        productosGlobales = productos;
        renderizarProductos(productos);
    } catch (error) {
        Toast.mostrar('Error al cargar productos', 'danger');
    } finally {
        ocultarLoader();
    }
}

function renderizarProductos(productos) {
    const container = document.getElementById('productosContainer');
    if (!container) return;
    if (!productos || productos.length === 0) {
        container.innerHTML = `
            <div style="grid-column:1/-1;">
                <div class="empty-state">
                    <i class="bi bi-search"></i>
                    <h4>No se encontraron productos</h4>
                    <p>Intenta con otros términos de búsqueda</p>
                </div>
            </div>
        `;
        return;
    }
    container.innerHTML = '';
    const iconos = {1:'bi-cup-hot-fill', 2:'bi-droplet-fill', 3:'bi-basket-fill', 4:'bi-egg-fried'};
    productos.forEach(producto => {
        const icono = iconos[producto.idCategoria] || 'bi-box-seam-fill';
        const div = document.createElement('div');
        div.className = 'prod-card';
        div.innerHTML = `
            <div class="prod-img">
                <i class="bi ${icono}"></i>
            </div>
            <div class="prod-body">
                <div class="prod-name">${producto.nombreProducto}</div>
                <div style="display:flex; justify-content:space-between; align-items:center; margin-top:0.35rem;">
                    <span class="prod-price">${producto.precio.toFixed(2)} €</span>
                    <button class="prod-add" onclick="Carrito.agregar({idProducto: ${producto.idProducto}, nombreProducto: '${producto.nombreProducto.replace(/'/g, "\\'")}', precio: ${producto.precio}}, 1)">
                        <i class="bi bi-plus-lg"></i>
                    </button>
                </div>
            </div>
        `;
        container.appendChild(div);
    });
}

function filtrarPorCategoria(categoria, btn) {
    categoriaActiva = categoria;
    document.querySelectorAll('.pill').forEach(b => b.classList.remove('active'));
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
