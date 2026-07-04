const API = {
    baseUrl: window.location.origin + window.location.pathname.replace(/\/[^/]*$/, ''),
    getBaseUrl: function() {
        return this.baseUrl;
    },
    request: async function(url, options = {}) {
        const config = {
            headers: {
                'Content-Type': 'application/json',
            },
            ...options,
        };
        try {
            const response = await fetch(url, config);
            const data = await response.json();
            if (!response.ok) {
                throw new Error(data.error || 'Error en la solicitud');
            }
            return data;
        } catch (error) {
            if (error.name === 'TypeError') {
                throw new Error('Error de conexión con el servidor');
            }
            throw error;
        }
    },
    get: function(url) {
        return this.request(url);
    },
    post: function(url, data) {
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify(data),
        });
    },
    put: function(url, data) {
        return this.request(url, {
            method: 'PUT',
            body: JSON.stringify(data),
        });
    },
    del: function(url) {
        return this.request(url, {
            method: 'DELETE',
        });
    },
    getBase: function() {
        return window.location.origin;
    },
};
