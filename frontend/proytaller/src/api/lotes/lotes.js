import request from '../../util/request'

export function listarLotes(params = {}) {
  return request({ url: '/api/inventario/lotes', method: 'get', params })
}
export function listarEstadosLote() { return request({ url: '/api/inventario/lotes/estados', method: 'get' }) }
export function actualizarLote(idLote, data) { return request({ url: `/api/inventario/lotes/${idLote}`, method: 'put', data }) }
export function descargarLotesPdf(params = {}) { return request({ url: '/api/inventario/lotes/pdf', method: 'get', params, responseType: 'blob' }) }
