import request from '../../util/request'

export function obtenerConfiguracionEmpresa() {
  return request({
    url: '/api/configuracion-empresa',
    method: 'get'
  })
}

export function guardarConfiguracionEmpresa(datos, logo, fondoLogin) {
  const formData = new FormData()
  formData.append('datos', new Blob([JSON.stringify(datos)], { type: 'application/json' }))
  if (logo) formData.append('logo', logo)
  if (fondoLogin) formData.append('fondoLogin', fondoLogin)
  return request({
    url: '/api/configuracion-empresa',
    method: 'put',
    data: formData
  })
}
