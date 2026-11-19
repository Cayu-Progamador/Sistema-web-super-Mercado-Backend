import request from '../../util/request'

const BASE_URL = '/api/reportes/ventas'

export const rutasReporteVenta = {
  'ventas-general': 'general',
  'rentabilidad-ventas': 'ganancias',
  'productos-mas-vendidos': 'productos-mas-vendidos',
  'productos-menos-vendidos': 'productos-menos-vendidos',
  'productos-sin-ventas': 'productos-sin-ventas',
  'ventas-metodo-pago': 'metodos-pago',
  'ventas-cajero': 'cajeros',
  'ventas-contado-credito': 'contado-credito',
  'ventas-periodo': 'periodos',
  'ventas-cliente': 'clientes'
}

export function consultarReporteVenta(tipo, params = {}) {
  return request({
    url: `${BASE_URL}/${rutasReporteVenta[tipo]}`,
    method: 'get',
    params
  })
}

export function obtenerDetalleGananciaVenta(idVenta) {
  return request({
    url: `${BASE_URL}/ganancias/${idVenta}/detalle`,
    method: 'get'
  })
}

export function exportarReporteVenta(tipo, formato, params = {}) {
  return request({
    url: `${BASE_URL}/${rutasReporteVenta[tipo]}/${formato.toLowerCase()}`,
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export async function cargarCatalogosReporteVenta() {
  const solicitudes = await Promise.allSettled([
    request({ url: '/api/productos/listar', method: 'get', params: { estado: true, page: 0, size: 1000 } }),
    request({ url: '/api/categorias', method: 'get' }),
    request({ url: '/api/marcas', method: 'get' }),
    request({ url: '/api/clientes', method: 'get', params: { estado: true, page: 0, size: 1000 } }),
    request({ url: '/api/usuarios/listar', method: 'get', params: { page: 0, size: 1000 } }),
    request({ url: '/api/cajas', method: 'get' })
  ])

  const valor = (indice, defecto) => solicitudes[indice].status === 'fulfilled'
    ? solicitudes[indice].value
    : defecto

  return {
    productos: valor(0, { content: [] }).content || [],
    categorias: valor(1, []),
    marcas: valor(2, []),
    clientes: valor(3, { content: [] }).content || [],
    cajeros: valor(4, { content: [] }).content || [],
    cajas: valor(5, [])
  }
}
