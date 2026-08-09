import request from '../../util/request'

export function listarMarcasPaginado(params) {
    return request({
        url: '/api/marcas/listar',
        method: 'get',
        params
    })
}

export function obtenerDetalleMarca(id) {
    return request({
        url: `/api/marcas/${id}`,
        method: 'get'
    })
}

export function listarMarcas(busqueda) {
    return request({
        url: '/api/marcas',
        method: 'get',
        params: busqueda ? { busqueda } : undefined
    })
}

export function crearMarca(data) {
    return request({
        url: '/api/marcas',
        method: 'post',
        data
    })
}

export function actualizarMarca(id, data) {
    return request({
        url: `/api/marcas/${id}`,
        method: 'put',
        data
    })
}

export function activarMarca(id) {
    return request({
        url: `/api/marcas/${id}/activar`,
        method: 'patch'
    })
}

export function desactivarMarca(id) {
    return request({
        url: `/api/marcas/${id}/desactivar`,
        method: 'patch'
    })
}