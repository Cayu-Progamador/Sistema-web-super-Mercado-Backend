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

export function activarProveedor(id) {
    return request({
        url: `/api/proveedores/${id}/activar`,
        method: 'patch'
    })
}

export function desactivarProveedor(id) {
    return request({
        url: `/api/proveedores/${id}/desactivar`,
        method: 'patch'
    })
}

export function exportarProveedoresPDF(params = {}) {
    return request({
        url: '/api/proveedores/exportar/pdf',
        method: 'get',
        params,
        responseType: 'blob'
    })
}

export function exportarProveedoresExcel(params = {}) {
    return request({
        url: '/api/proveedores/exportar/excel',
        method: 'get',
        params,
        responseType: 'blob'
    })
}

export function exportarProveedorDetallePDF(id) {
    return request({
        url: `/api/proveedores/${id}/pdf`,
        method: 'get',
        responseType: 'blob'
    })
}

export function exportarProveedorDetalleExcel(id) {
    return request({
        url: `/api/proveedores/${id}/excel`,
        method: 'get',
        responseType: 'blob'
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
