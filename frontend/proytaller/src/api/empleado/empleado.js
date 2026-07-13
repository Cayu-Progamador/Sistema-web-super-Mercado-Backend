import request from '../../util/request'

export function getDashboardEmpleados() {
    return request({
        url: '/api/empleados/estadisticas',
        method: 'get'
    })
}

export function getEmpleadoLista() {
    return request({
        url: '/api/empleados/select',
        method: 'get'
    })
}

export function getEmpleadoListaEditar(usuarioId) {
    return request({
        url: `/api/empleados/editar/${usuarioId}`,
        method: 'get'
    })
}

export function listarEmpleados(params) {
    return request({
        url: '/api/empleados/listar',
        method: 'get',
        params
    })
}

export function listarEmpleadosDisponibles(params) {
    return request({
        url: '/api/empleados/disponibles-para-contrato',
        method: 'get',
        params
    })
}

export function crearEmpleado(data) {
    return request({
        url: '/api/empleados',
        method: 'post',
        data
    })
}

export function actualizarEmpleado(id, data) {
    return request({
        url: `/api/empleados/${id}`,
        method: 'put',
        data
    })
}

export function desactivarEmpleado(id) {
    return request({
        url: `/api/empleados/${id}/desactivar`,
        method: 'patch'
    })
}

export function activarEmpleado(id) {
    return request({
        url: `/api/empleados/${id}/activar`,
        method: 'patch'
    })
}

export function obtenerEmpleado(id) {
    return request({
        url: `/api/empleados/${id}`,
        method: 'get'
    })
}

export function exportarEmpleadoDetallePDF(id) {
    return request({
        url: `/api/empleados/${id}/pdf`,
        method: 'get',
        responseType: 'blob'
    })
}

export function exportarEmpleadosPDF(params = {}) {
    return request({
        url: '/api/empleados/exportar/pdf',
        method: 'get',
        params,
        responseType: 'blob'
    })
}

export function exportarEmpleadosExcel(params = {}) {
    return request({
        url: '/api/empleados/exportar/excel',
        method: 'get',
        params,
        responseType: 'blob'
    })
}
