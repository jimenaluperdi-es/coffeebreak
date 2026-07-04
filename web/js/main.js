document.addEventListener('DOMContentLoaded', function() {
    verificarSesion();
    actualizarNavbar();
});

async function verificarSesion() {
    try {
        const data = await API.get(API.getBaseUrl() + '/api/sesion');
        if (data.autenticado) {
            document.body.classList.add('sesion-iniciada');
            const navUsuario = document.getElementById('navUsuario');
            if (navUsuario) {
                navUsuario.textContent = data.nombre;
            }
        }
    } catch (error) {
        console.error('Error verificando sesión:', error);
    }
}

function actualizarNavbar() {
    const token = localStorage.getItem('token');
    const navLogin = document.getElementById('navLogin');
    const navRegistro = document.getElementById('navRegistro');
    const navCerrarSesion = document.getElementById('navCerrarSesion');
    const navPerfil = document.getElementById('navPerfil');
    if (token) {
        if (navLogin) navLogin.style.display = 'none';
        if (navRegistro) navRegistro.style.display = 'none';
        if (navCerrarSesion) navCerrarSesion.style.display = 'block';
        if (navPerfil) navPerfil.style.display = 'block';
    } else {
        if (navLogin) navLogin.style.display = 'block';
        if (navRegistro) navRegistro.style.display = 'block';
        if (navCerrarSesion) navCerrarSesion.style.display = 'none';
        if (navPerfil) navPerfil.style.display = 'none';
    }
}

async function cerrarSesion() {
    try {
        await API.post(API.getBaseUrl() + '/api/logout', {});
        localStorage.removeItem('token');
        window.location.href = 'index.html';
    } catch (error) {
        Toast.mostrar('Error al cerrar sesión', 'danger');
    }
}

function mostrarLoader() {
    const loader = document.getElementById('loaderGlobal');
    if (loader) loader.style.display = 'flex';
}

function ocultarLoader() {
    const loader = document.getElementById('loaderGlobal');
    if (loader) loader.style.display = 'none';
}

function irAPagina(url) {
    window.location.href = url;
}

function confirmarEliminacion(mensaje) {
    return confirm(mensaje || '¿Estás seguro de que deseas eliminar este elemento?');
}
