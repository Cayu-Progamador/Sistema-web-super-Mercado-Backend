import request from '../../util/request'

export function getContratoDashboard() {
    return request({
        url: '/api/contratos/dashboard',
        method: 'get'
    })
}

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

export function getDetalleContrato(id) {
    return request({
        url: `/api/contratos/${id}/detalle`,
        method: 'get'
    })
}

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

export function renovarContrato(id, data) {
    return request({
        url: `/api/contratos/${id}/renovar`,
        method: 'post',
        data
    })
}

export async function descargarPdfContrato(id) {
    const { default: axios } = await import('axios')
    const { useAuthStore } = await import('../../store/store')
    const store = useAuthStore()
    return axios({
        url: `${import.meta.env.VITE_API_URL}/api/contratos/${id}/pdf`,
        method: 'get',
        responseType: 'blob',
        headers: { Authorization: `Bearer ${store.token}` }
    }).then(res => res.data)
}

export function exportarContratosPDF(params) {
    return request({
        url: '/api/contratos/exportar/pdf',
        method: 'get',
        params,
        responseType: 'blob'
    })
}

export function exportarContratosExcel(params) {
    return request({
        url: '/api/contratos/exportar/excel',
        method: 'get',
        params,
        responseType: 'blob'
    })
}
