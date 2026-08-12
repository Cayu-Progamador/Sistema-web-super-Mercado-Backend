import request from '../../util/request'

export function getProductoDashboard() {
    return request({
        url: '/api/productos/dashboard',
        method: 'get'
    })
}

export function listarProductos(params) {
    return request({
        url: '/api/productos/listar',
        method: 'get',
        params
    })
}

export function obtenerProducto(id) {
    return request({
        url: `/api/productos/${id}`,
        method: 'get'
    })
}

export function crearProducto(data) {
    return request({
        url: '/api/productos',
        method: 'post',
        data
    })
}

export function subirImagenProducto(file) {
    const formData = new FormData()
    formData.append('imagen', file)
    return request({
        url: '/api/productos/imagen',
        method: 'post',
        data: formData,
        headers: { 'Content-Type': 'multipart/form-data' }
    })
}

export function actualizarProducto(id, data) {
    return request({
        url: `/api/productos/${id}`,
        method: 'put',
        data
    })
}

export function eliminarProducto(id) {
    return request({
        url: `/api/productos/${id}`,
        method: 'delete'
    })
}

export function actualizarEstadoProducto(id, data) {
    return request({
        url: `/api/productos/${id}/estado`,
        method: 'patch',
        data
    })
}

export function activarProducto(id) {
    return request({
        url: `/api/productos/${id}/activar`,
        method: 'patch'
    })
}

export function desactivarProducto(id) {
    return request({
        url: `/api/productos/${id}/desactivar`,
        method: 'patch'
    })
}

export function exportarProductosPDF(params = {}) {
    return request({
        url: '/api/productos/exportar/pdf',
        method: 'get',
        params,
        responseType: 'blob'
    })
}

export function exportarProductosExcel(params = {}) {
    return request({
        url: '/api/productos/exportar/excel',
        method: 'get',
        params,
        responseType: 'blob'
    })
}

export function listarCategorias() {
    return request({
        url: '/api/categorias',
        method: 'get'
    })
}

export function listarSubcategorias(params) {
    return request({
        url: '/api/subcategorias',
        method: 'get',
        params
    })
}

export function listarMarcas() {
    return request({
        url: '/api/marcas',
        method: 'get'
    })
}

export function listarUnidades() {
    return request({
        url: '/api/unidades-medida',
        method: 'get'
    })
}