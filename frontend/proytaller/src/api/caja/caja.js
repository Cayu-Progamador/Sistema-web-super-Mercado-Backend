import request from '../../util/request'

export function obtenerSesionCajaAbierta () {
  return request({ url: '/api/cajas/sesiones/abierta', method: 'get' })
}

export function listarCajas () { return request({ url: '/api/cajas', method: 'get' }).then(cajas => (cajas || []).filter(caja => !caja.ocupada)) }
export function abrirCaja (data) { return request({ url: '/api/cajas/sesiones/abrir', method: 'post', data }) }
export function cerrarCaja (id, data) { return request({ url: `/api/cajas/sesiones/${id}/cerrar`, method: 'post', data }) }
export function listarSesionesCaja (params = {}) { return request({ url: '/api/cajas/sesiones', method: 'get', params }) }
export function listarMisSesionesCaja (params = {}) { return request({ url: '/api/cajas/mis-sesiones', method: 'get', params }) }
