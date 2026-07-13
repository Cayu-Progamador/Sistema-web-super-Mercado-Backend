import request from '../../util/request'

export function listarCargos({ busqueda, estado } = {}) {
    return request({
        url: '/api/cargos',
        method: 'get',
        params: { busqueda, estado }
    })
}

export function obtenerCargo(id) {
    return request({
        url: `/api/cargos/${id}`,
        method: 'get'
    })
}

export function crearCargo(data) {
    return request({
        url: '/api/cargos',
        method: 'post',
        data
    })
}

export function actualizarCargo(id, data) {
    return request({
        url: `/api/cargos/${id}`,
        method: 'put',
        data
    })
}

export function activarCargo(id) {
    return request({
        url: `/api/cargos/${id}/activar`,
        method: 'patch'
    })
}

export function desactivarCargo(id) {
    return request({
        url: `/api/cargos/${id}/desactivar`,
        method: 'patch'
    })
}
