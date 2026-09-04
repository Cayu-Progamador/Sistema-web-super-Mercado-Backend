import request from '../../util/request'

export function listarKardex(params = {}) {
  return request({
    url: '/api/inventario/kardex',
    method: 'get',
    params
  })
}

export function descargarKardexPdf(params = {}) {
  return request({
    url: '/api/inventario/kardex/pdf',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
