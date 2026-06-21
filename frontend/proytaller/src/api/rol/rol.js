import request from '../../util/request'

//listar roles para usuario
export function getListRoles() {
    return request({
        url: '/api/roles/select-rol',
        method: 'get'
    })
}


//listar roles para usuario (paginado)
export function listarRoles(page = 0, size = 15) {
    return request({
        url: '/api/roles/listar',
        method: 'get',
        params: { page, size }
    })
}

//crear un nuevo rol
export function crearRol(data) {
    return request({
        url: '/api/roles/crear',
        method: 'post',
        data: data
    })
}

//obtener estadisticas de roles (totales, activos, inactivos, permisos)
export function obtenerEstadisticasRoles() {
    return request({
        url: '/api/roles/estadisticas',
        method: 'get'
    })
}

//activar rol
export function activarRol(id) {
    return request({
        url: `/api/roles/${id}/activar`,
        method: 'patch'
    })
}

//desactivar rol
export function desactivarRol(id) {
    return request({
        url: `/api/roles/${id}/desactivar`,
        method: 'patch'
    })
}

//buscar rol por nombre (paginado)
export function buscarRol(nombre, page = 0, size = 15) {
    return request({
        url: `/api/roles/buscar`,
        method: 'get',
        params: { nombre, page, size }
    })
}

//actualizar rol
export function actualizarRol(id, data) {
    return request({
        url: `/api/roles/${id}`,
        method: 'put',
        data: data
    })
}

//detalle del rol
export function obtenerDetalleRol(id) {
    return request({
        url: `/api/roles/${id}/detalle`,
        method: 'get'
    })
}