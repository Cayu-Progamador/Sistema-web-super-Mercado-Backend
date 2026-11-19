import request from '../../util/request'
export const rutasReporteInventario = {
  existencias: 'existencias', 'stock-minimo': 'stock-minimo', 'productos-agotados': 'productos-agotados',
  'proximos-vencer': 'proximos-vencer', 'productos-vencidos': 'productos-vencidos', kardex: 'kardex',
  'inventario-almacen': 'existencias', 'inventario-ubicacion': 'existencias', 'movimientos-inventario': 'kardex', 'transferencias-inventario': 'transferencias-inventario', 'inventario-fisico': 'inventario-fisico', 'ajustes-inventario': 'ajustes-inventario'
}
export function consultarReporteInventario(tipo, params = {}) { return request({ url: `/api/reportes/inventario/${rutasReporteInventario[tipo]}`, method: 'get', params }) }
export function exportarReporteInventario(tipo, formato, params = {}) { return request({ url: `/api/reportes/inventario/${rutasReporteInventario[tipo]}/${formato.toLowerCase()}`, method: 'get', params, responseType: 'blob' }) }
