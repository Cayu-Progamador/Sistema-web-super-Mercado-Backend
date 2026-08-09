import request from '../../util/request'

export function listarUnidadesMedidaPaginado(params) {
    return request({
        url: '/api/unidades-medida/listar',
        method: 'get',
        params
    })
}

export function obtenerDetalleUnidadMedida(id) {
    return request({
        url: `/api/unidades-medida/${id}`,
        method: 'get'
    })
}

export function listarUnidadesMedida() {
    return request({
        url: '/api/unidades-medida',
        method: 'get'
    })
}

export function listarTiposUnidadMedida() {
    return request({
        url: '/api/unidades-medida/tipos',
        method: 'get'
    })
}

export function crearUnidadMedida(data) {
    return request({
        url: '/api/unidades-medida',
        method: 'post',
        data
    })
}

export function actualizarUnidadMedida(id, data) {
    return request({
        url: `/api/unidades-medida/${id}`,
        method: 'put',
        data
    })
}

export function activarUnidadMedida(id) {
    return request({
        url: `/api/unidades-medida/${id}/activar`,
        method: 'patch'
    })
}

export function desactivarUnidadMedida(id) {
    return request({
        url: `/api/unidades-medida/${id}/desactivar`,
        method: 'patch'
    })
}