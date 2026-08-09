import request from '../../util/request'

export function listarSubcategorias(params) {
    return request({
        url: '/api/subcategorias',
        method: 'get',
        params
    })
}

export function crearSubcategoria(data) {
    return request({
        url: '/api/subcategorias',
        method: 'post',
        data
    })
}

export function actualizarSubcategoria(id, data) {
    return request({
        url: `/api/subcategorias/${id}`,
        method: 'put',
        data
    })
}

export function obtenerDetalleSubcategoria(id) {
    return request({
        url: `/api/subcategorias/${id}`,
        method: 'get'
    })
}
