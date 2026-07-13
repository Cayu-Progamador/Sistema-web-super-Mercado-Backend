import request from '../../util/request'

export function listarContratos(params) {
    return request({
        url: '/api/contratos',
        method: 'get',
        params
    })
}

export function obtenerContrato(id) {
    return request({
        url: `/api/contratos/${id}`,
        method: 'get'
    })
}

export function crearContrato(data) {
    return request({
        url: '/api/contratos',
        method: 'post',
        data
    })
}

export function actualizarContrato(id, data) {
    return request({
        url: `/api/contratos/${id}`,
        method: 'put',
        data
    })
}

export function activarContrato(id) {
    return request({
        url: `/api/contratos/${id}/activar`,
        method: 'patch'
    })
}

export function desactivarContrato(id) {
    return request({
        url: `/api/contratos/${id}/desactivar`,
        method: 'patch'
    })
}

export function finalizarContrato(id, motivoFin) {
    return request({
        url: `/api/contratos/${id}/finalizar`,
        method: 'patch',
        params: { motivoFin }
    })
}

// Aliases
export const getContrato = obtenerContrato
export const createContrato = crearContrato
export const updateContrato = actualizarContrato

export function listarTiposContrato() {
    return request({
        url: '/api/tipos-contrato',
        method: 'get'
    })
}

export function listarTiposJornada() {
    return request({
        url: '/api/tipos-jornada',
        method: 'get'
    })
}

export function listarTurnos() {
    return request({
        url: '/api/turnos',
        method: 'get'
    })
}

export function listarEmpleadosSelect() {
    return request({
        url: '/api/empleados/select',
        method: 'get'
    })
}

export function listarCargos() {
    return request({
        url: '/api/cargos',
        method: 'get'
    })
}

export function listarTiposPago() {
    return request({
        url: '/api/tipos-pago',
        method: 'get'
    })
}
