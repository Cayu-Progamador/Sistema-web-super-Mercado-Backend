import request from '../../util/request'

export function obtenerConfiguracionCorreo() {
  return request({
    url: '/api/configuracion-correo',
    method: 'get'
  })
}

export function guardarConfiguracionCorreo(data) {
  return request({
    url: '/api/configuracion-correo',
    method: 'put',
    data
  })
}
