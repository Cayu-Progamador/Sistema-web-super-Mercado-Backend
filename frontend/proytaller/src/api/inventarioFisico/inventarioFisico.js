import request from '../../util/request'

export function listarInventariosFisicos(params) {
  return request({
    url: '/api/inventarios-fisicos',
    method: 'get',
    params
  })
}

export function crearInventarioFisico(data) {
  return request({
    url: '/api/inventarios-fisicos',
    method: 'post',
    data
  })
}

export function actualizarInventarioFisico(id, data) {
  return request({
    url: `/api/inventarios-fisicos/${id}`,
    method: 'put',
    data
  })
}

export function iniciarInventarioFisico(id) {
  return request({
    url: `/api/inventarios-fisicos/${id}/iniciar`,
    method: 'post'
  })
}

export function obtenerInventarioFisico(id) {
  return request({
    url: `/api/inventarios-fisicos/${id}`,
    method: 'get'
  })
}

export function listarDetallesConteoFisico(id, params) {
  return request({
    url: `/api/inventarios-fisicos/${id}/detalles`,
    method: 'get',
    params
  })
}

export function guardarAvanceConteoFisico(id, data) {
  return request({
    url: `/api/inventarios-fisicos/${id}/conteo`,
    method: 'put',
    data
  })
}

export function finalizarConteoFisico(id) {
  return request({
    url: `/api/inventarios-fisicos/${id}/finalizar`,
    method: 'post'
  })
}

export function listarDiferenciasInventarioFisico(id, params) {
  return request({
    url: `/api/inventarios-fisicos/${id}/diferencias`,
    method: 'get',
    params
  })
}

export function guardarDiferenciasInventarioFisico(id, data) {
  return request({
    url: `/api/inventarios-fisicos/${id}/diferencias`,
    method: 'put',
    data
  })
}

export function aprobarInventarioFisico(id) {
  return request({
    url: `/api/inventarios-fisicos/${id}/aprobar`,
    method: 'post'
  })
}

export function cancelarInventarioFisico(id, data) {
  return request({
    url: `/api/inventarios-fisicos/${id}/cancelar`,
    method: 'post',
    data
  })
}

export function eliminarInventarioFisico(id) {
  return request({
    url: `/api/inventarios-fisicos/${id}`,
    method: 'delete'
  })
}

export function descargarActaInventarioFisico(id) {
  return request({
    url: `/api/inventarios-fisicos/${id}/pdf`,
    method: 'get',
    responseType: 'blob'
  })
}
