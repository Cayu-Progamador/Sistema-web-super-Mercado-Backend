import request from '../../util/request'

export function marcarAsistencia(data) {
  return request({
    url: '/api/asistencias/marcar',
    method: 'post',
    data
  })
}

export function obtenerAsistenciaHoy() {
  return request({
    url: '/api/asistencias/hoy',
    method: 'get'
  })
}

export function listarMisAsistencias(params) {
  return request({
    url: '/api/asistencias/mis-asistencias',
    method: 'get',
    params
  })
}

export function obtenerMiResumen(params) {
  return request({
    url: '/api/asistencias/mi-resumen',
    method: 'get',
    params
  })
}

export function listarAsistenciasAdmin(params) {
  return request({
    url: '/api/asistencias/listar',
    method: 'get',
    params
  })
}

export function obtenerResumenHoyAdmin() {
  return request({
    url: '/api/asistencias/admin/resumen-hoy',
    method: 'get'
  })
}

export function obtenerDetalleEmpleadoAdmin(idEmpleado, params) {
  return request({
    url: `/api/asistencias/admin/empleado/${idEmpleado}/detalle`,
    method: 'get',
    params
  })
}

export function justificarAsistenciaAdmin(idAsistencia, data) {
  return request({
    url: `/api/asistencias/${idAsistencia}/justificar`,
    method: 'put',
    data
  })
}
