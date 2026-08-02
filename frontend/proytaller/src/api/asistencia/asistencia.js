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
    url: '/api/asistencias/admin/listar',
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

export function descargarPdfAsistenciaAdmin(idAsistencia) {
  return request({
    url: `/api/asistencias/admin/${idAsistencia}/pdf`,
    method: 'get',
    responseType: 'blob'
  })
}

export function editarAsistenciaAdmin(idAsistencia, data) {
  return request({
    url: `/api/asistencias/admin/${idAsistencia}`,
    method: 'put',
    data
  })
}

export function crearAsistenciaAdmin(data) {
  return request({
    url: '/api/asistencias/admin/crear',
    method: 'post',
    data
  })
}

export function justificarAsistenciaAdmin(idAsistencia, data) {
  return request({
    url: `/api/asistencias/${idAsistencia}/justificar`,
    method: 'put',
    data
  })
}

export function justificarAusenteAdmin(idContrato, data) {
  return request({
    url: `/api/asistencias/admin/justificar-ausente/${idContrato}`,
    method: 'post',
    data
  })
}

export function justificarMiAsistencia(data) {
  return request({
    url: '/api/asistencias/justificar',
    method: 'post',
    data
  })
}

export function listarMisAusencias() {
  return request({
    url: '/api/asistencias/mis-ausencias',
    method: 'get'
  })
}

export function tieneAccesoAsistencia() {
  return request({
    url: '/api/asistencias/tiene-acceso',
    method: 'get'
  })
}

export function obtenerFiltrosAsistencia() {
  return request({
    url: '/api/asistencias/admin/filtros',
    method: 'get'
  })
}

export function descargarReporteSemanal(params) {
  return request({
    url: '/api/asistencias/descargar-reporte',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function exportarPDFAdmin(params) {
  return request({
    url: '/api/asistencias/admin/exportar/pdf',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function exportarExcelAdmin(params) {
  return request({
    url: '/api/asistencias/admin/exportar/excel',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function listarAusentesAdmin(mes, anio) {
  return request({
    url: '/api/asistencias/admin/ausentes',
    method: 'get',
    params: { mes, anio }
  })
}

export function listarAusentesDelDiaAdmin(fecha) {
  return request({
    url: '/api/asistencias/admin/ausentes',
    method: 'get',
    params: { fecha }
  })
}

export function listarAusentesDetalleAdmin(mes, anio) {
  return request({
    url: '/api/asistencias/admin/ausentes/detalle',
    method: 'get',
    params: { mes, anio }
  })
}
