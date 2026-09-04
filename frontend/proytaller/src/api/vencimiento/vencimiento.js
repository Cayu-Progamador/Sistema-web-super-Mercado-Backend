import request from '../../util/request'

export function listarVencimientos(params = {}) {
  return request({
    url: '/api/inventario/vencimientos',
    method: 'get',
    params
  })
}

export function obtenerResumenVencimientos() {
  return request({
    url: '/api/inventario/vencimientos/resumen',
    method: 'get'
  })
}

export function descargarVencimientosPdf(params = {}) {
  return request({
    url: '/api/inventario/vencimientos/pdf',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
