import request from '../../util/request'

const BASE_URL = '/api/ventas'

export function listarCatalogoVenta(buscar = '') {
  return request({
    url: `${BASE_URL}/catalogo`,
    method: 'get',
    params: buscar?.trim() ? { buscar: buscar.trim() } : undefined
  })
}

export function crearVenta(data) {
  return request({ url: BASE_URL, method: 'post', data })
}

export function listarVentas(params = {}) {
  return request({ url: BASE_URL, method: 'get', params })
}

export function obtenerVenta(id) {
  return request({ url: `${BASE_URL}/${id}`, method: 'get' })
}

export function anularVenta(id, motivo) {
  return request({ url: `${BASE_URL}/${id}`, method: 'delete', params: { motivo } })
}

export function verificarVenta(codigo) {
  return request({ url: `${BASE_URL}/verificar/${encodeURIComponent(codigo)}`, method: 'get' })
}

export function descargarComprobante(id) {
  return request({ url: `${BASE_URL}/${id}/comprobante`, method: 'get', responseType: 'blob' })
}

export function obtenerGananciaDiaria() {
  return request({ url: `${BASE_URL}/ganancia-diaria`, method: 'get' })
}

export function obtenerResumenVentas() {
  return request({ url: `${BASE_URL}/resumen`, method: 'get' })
}

export function obtenerDashboardVentas() {
  return request({ url: `${BASE_URL}/dashboard`, method: 'get' })
}

export function listarCreditos(params = {}) {
  return request({ url: '/api/creditos', method: 'get', params })
}

export function obtenerResumenCreditos() {
  return request({ url: '/api/creditos/resumen', method: 'get' })
}

export function registrarPagoCredito(idCuenta, data) {
  return request({ url: `/api/creditos/${idCuenta}/pagos`, method: 'post', data })
}
