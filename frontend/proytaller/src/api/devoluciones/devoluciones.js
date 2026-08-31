import request from '../../util/request'

export function listarDevoluciones(params) {
  return request({
    url: '/api/devoluciones',
    method: 'get',
    params
  })
}

export function obtenerDevolucion(id) {
  return request({
    url: `/api/devoluciones/${id}`,
    method: 'get'
  })
}

export function listarRecepcionesDisponibles() {
  return request({
    url: '/api/devoluciones/recepciones-disponibles',
    method: 'get'
  })
}

export function listarEstadosDevolucion() {
  return request({
    url: '/api/devoluciones/estados',
    method: 'get'
  })
}

export function listarMotivosDevolucion() {
  return request({
    url: '/api/devoluciones/motivos',
    method: 'get'
  })
}

export function crearDevolucion(data) {
  return request({
    url: '/api/devoluciones',
    method: 'post',
    data
  })
}

export function actualizarDevolucion(id, data) {
  return request({
    url: `/api/devoluciones/${id}`,
    method: 'put',
    data
  })
}

export function confirmarDevolucion(id) {
  return request({
    url: `/api/devoluciones/${id}/confirmar`,
    method: 'post'
  })
}

export function enviarDevolucion(id) {
  return request({
    url: `/api/devoluciones/${id}/enviar`,
    method: 'post'
  })
}

export function completarDevolucion(id) {
  return request({
    url: `/api/devoluciones/${id}/completar`,
    method: 'post'
  })
}

export function cancelarDevolucion(id) {
  return request({
    url: `/api/devoluciones/${id}/cancelar`,
    method: 'post'
  })
}

export function descargarPdfDevolucion(id) {
  return request({
    url: `/api/devoluciones/${id}/pdf`,
    method: 'get',
    responseType: 'blob'
  })
}
