import request from '../../util/request'

const BASE_URL = '/api/inventario/ajustes'

export function listarAjustes(params = {}) {
  return request({ url: BASE_URL, method: 'get', params })
}

export function listarCatalogosAjustes() {
  return request({ url: `${BASE_URL}/catalogos`, method: 'get' })
}

export function obtenerAjuste(id) {
  return request({ url: `${BASE_URL}/${id}`, method: 'get' })
}

export function descargarAjustePdf(id) {
  return request({
    url: `${BASE_URL}/${id}/pdf`,
    method: 'get',
    responseType: 'blob'
  })
}

export function crearAjuste(data) {
  return request({ url: BASE_URL, method: 'post', data })
}

export function actualizarBorradorAjuste(id, data) {
  return request({ url: `${BASE_URL}/${id}`, method: 'put', data })
}

export function enviarAjuste(id) {
  return request({ url: `${BASE_URL}/${id}/enviar`, method: 'patch' })
}

export function aprobarAjuste(id) {
  return request({ url: `${BASE_URL}/${id}/aprobar`, method: 'patch' })
}

export function rechazarAjuste(id, motivo) {
  return request({ url: `${BASE_URL}/${id}/rechazar`, method: 'patch', data: { motivo } })
}

export function aplicarAjuste(id) {
  return request({ url: `${BASE_URL}/${id}/aplicar`, method: 'patch' })
}

export function cancelarAjuste(id, motivo) {
  return request({ url: `${BASE_URL}/${id}/cancelar`, method: 'patch', data: { motivo } })
}

export function eliminarBorradorAjuste(id) {
  return request({ url: `${BASE_URL}/${id}`, method: 'delete' })
}
