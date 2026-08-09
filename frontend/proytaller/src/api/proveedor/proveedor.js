import request from '../../util/request'

export function listarProveedores(params) {
    return request({
        url: '/api/proveedores/listar',
        method: 'get',
        params
    })
}

export function obtenerEstadisticas() {
    return request({
        url: '/api/proveedores/estadisticas',
        method: 'get'
    })
}

export function obtenerProveedor(id) {
    return request({
        url: `/api/proveedores/${id}`,
        method: 'get'
    })
}

export function crearProveedor(data) {
    return request({
        url: '/api/proveedores',
        method: 'post',
        data
    })
}

export function actualizarProveedor(id, data) {
    return request({
        url: `/api/proveedores/${id}`,
        method: 'put',
        data
    })
}

export function listarSexos() {
    return request({
        url: '/api/sexos',
        method: 'get'
    })
}

export function listarCiudades() {
    return request({
        url: '/api/ciudades',
        method: 'get'
    })
}
