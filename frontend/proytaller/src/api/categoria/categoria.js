import request from '../../util/request'

export function listarCategoriasPaginado(params) {
    return request({
        url: '/api/categorias/listar',
        method: 'get',
        params
    })
}

export function listarCategorias() {
    return request({
        url: '/api/categorias',
        method: 'get'
    })
}

export function obtenerResumenCategorias() {
    return request({
        url: '/api/categorias/resumen',
        method: 'get'
    })
}

export function listarCategoriasActivas() {
    return request({
        url: '/api/categorias/activas',
        method: 'get'
    })
}

export function obtenerDetalleCategoria(id) {
    return request({
        url: `/api/categorias/${id}`,
        method: 'get'
    })
}

export function crearCategoria(data) {
    return request({
        url: '/api/categorias',
        method: 'post',
        data
    })
}

export function actualizarCategoria(id, data) {
    return request({
        url: `/api/categorias/${id}`,
        method: 'put',
        data
    })
}

export function activarCategoria(id) {
    return request({
        url: `/api/categorias/${id}/activar`,
        method: 'patch'
    })
}

export function desactivarCategoria(id) {
    return request({
        url: `/api/categorias/${id}/desactivar`,
        method: 'patch'
    })
}
