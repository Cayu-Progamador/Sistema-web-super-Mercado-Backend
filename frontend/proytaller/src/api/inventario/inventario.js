import request from '../../util/request'

export function listarExistencias(params = {}) {
  const query = { ...params }
  if (query.idAlmacen != null && query.almacen == null) query.almacen = query.idAlmacen
  if (query.idUbicacion != null && query.ubicacion == null) query.ubicacion = query.idUbicacion
  delete query.idAlmacen
  delete query.idUbicacion
  return request({ url: '/api/inventario/existencias', method: 'get', params: query })
}

export function listarEstadosExistencia() {
  return request({ url: '/api/inventario/existencias/estados', method: 'get' })
}

export function obtenerDetalleExistencia(id) {
  return request({ url: `/api/inventario/existencias/${id}`, method: 'get' })
}
export function obtenerResumen() { return request({ url: '/api/inventario/resumen', method: 'get' }) }

export function descargarExistenciasPdf(params = {}) {
  const query = { ...params }
  if (query.idAlmacen != null && query.almacen == null) query.almacen = query.idAlmacen
  if (query.idUbicacion != null && query.ubicacion == null) query.ubicacion = query.idUbicacion
  delete query.idAlmacen
  delete query.idUbicacion
  return request({
    url: '/api/inventario/existencias/pdf',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
