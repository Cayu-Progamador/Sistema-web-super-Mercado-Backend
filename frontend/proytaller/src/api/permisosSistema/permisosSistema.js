import request from '../../util/request'

export function obtenerMisPermisos() {
  return request({
    url: '/api/permisos-sistema/mis-permisos',
    method: 'get'
  })
}

export function obtenerMatrizPermisos(idRol) {
  return request({
    url: `/api/permisos-sistema/matriz/${idRol}`,
    method: 'get'
  })
}

export function guardarMatrizPermisos(idRol, idsPermiso) {
  return request({
    url: `/api/permisos-sistema/matriz/${idRol}`,
    method: 'put',
    data: { idsPermiso }
  })
}
