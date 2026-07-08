import request from '../../util/request'

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

export function listarEmpleados({ busqueda, estado, page, size, sortBy, sortDir }) {
    return request({
        url: '/api/empleados/listar',
        method: 'get',
        params: { busqueda, estado, page, size, sortBy, sortDir }
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
