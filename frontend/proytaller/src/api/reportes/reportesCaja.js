import request from '../../util/request'

const BASE_URL = '/api/reportes/caja'

export const rutasReporteCaja = {
  'sesiones-caja': 'sesiones-caja',
  'movimientos-caja': 'movimientos-caja',
  'conciliacion-caja': 'conciliacion-caja',
  'recaudacion-metodo': 'recaudacion-metodo',
  'ventas-sesion-caja': 'ventas-sesion-caja',
  'rendimiento-cajero': 'rendimiento-cajero'
}

export function consultarReporteCaja(tipo, params = {}) {
  return request({ url: `${BASE_URL}/${rutasReporteCaja[tipo]}`, method: 'get', params })
}

export function exportarReporteCaja(tipo, formato, params = {}) {
  return request({
    url: `${BASE_URL}/${rutasReporteCaja[tipo]}/${formato.toLowerCase()}`,
    method: 'get', params, responseType: 'blob'
  })
}

export function listarSesionesCaja() {
  return request({ url: '/api/cajas/sesiones', method: 'get', params: { page: 0, size: 1000 } })
}
