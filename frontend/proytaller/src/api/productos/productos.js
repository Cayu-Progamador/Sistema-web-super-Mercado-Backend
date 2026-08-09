import request from '../../util/request'

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

export function listarCategorias() {
    return request({
        url: '/api/categorias',
        method: 'get'
    })
}

export function listarSubcategorias() {
    return request({
        url: '/api/subcategorias',
        method: 'get'
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