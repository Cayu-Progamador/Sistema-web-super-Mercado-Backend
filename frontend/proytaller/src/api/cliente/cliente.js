import request from '../../util/request'

export function listarClientes(params) {
  return request({ url: '/api/clientes', method: 'get', params })
}

export function obtenerCliente(id) {
  return request({ url: `/api/clientes/${id}`, method: 'get' })
}

export function crearCliente(data) {
  return request({ url: '/api/clientes', method: 'post', data })
}

export function actualizarCliente(id, data) {
  return request({ url: `/api/clientes/${id}`, method: 'put', data })
}

export function activarCliente(id) {
  return request({ url: `/api/clientes/${id}/activar`, method: 'patch' })
}

export function desactivarCliente(id) {
  return request({ url: `/api/clientes/${id}/desactivar`, method: 'patch' })
}
