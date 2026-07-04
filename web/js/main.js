let sesionActual = { autenticado: false };

document.addEventListener('DOMContentLoaded', async function() {
    await verificarSesion();
    actualizarNavBar();
});

async function verificarSesion() {
    try {
        const data = await API.get(API.getBaseUrl() + '/api/sesion');
        sesionActual = data;
        if (data.autenticado) {
            document.body.classList.add('sesion-iniciada');
        }
    } catch (error) {
        sesionActual = { autenticado: false };
    }
}

function actualizarNavBar() {
    const guestEls = document.querySelectorAll('.nav-guest');
    const userEls = document.querySelectorAll('.nav-user');
    const userNameEl = document.getElementById('drawerUserName');
    const userEmailEl = document.getElementById('drawerUserEmail');

    const dnavGuestEls = document.querySelectorAll('.dnav-guest');
    const dnavUserEls = document.querySelectorAll('.dnav-user');
    const dnavUserNameEl = document.getElementById('dnavUserName');

    if (sesionActual.autenticado) {
        guestEls.forEach(el => el.style.display = 'none');
        userEls.forEach(el => el.style.display = 'flex');
        dnavGuestEls.forEach(el => el.style.display = 'none');
        dnavUserEls.forEach(el => el.style.display = 'flex');
        if (userNameEl) userNameEl.textContent = sesionActual.nombre || 'Usuario';
        if (userEmailEl) userEmailEl.textContent = sesionActual.email || '';
        if (dnavUserNameEl) dnavUserNameEl.textContent = sesionActual.nombre || 'Usuario';
    } else {
        guestEls.forEach(el => el.style.display = 'flex');
        userEls.forEach(el => el.style.display = 'none');
        dnavGuestEls.forEach(el => el.style.display = 'flex');
        dnavUserEls.forEach(el => el.style.display = 'none');
    }
}

function abrirDrawer() {
    document.getElementById('drawerOverlay').classList.add('open');
    document.getElementById('drawer').classList.add('open');
    actualizarNavBar();
}

function cerrarDrawer() {
    document.getElementById('drawerOverlay').classList.remove('open');
    document.getElementById('drawer').classList.remove('open');
}

async function cerrarSesion() {
    try {
        await API.post(API.getBaseUrl() + '/api/logout', {});
        sesionActual = { autenticado: false };
        cerrarDrawer();
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
