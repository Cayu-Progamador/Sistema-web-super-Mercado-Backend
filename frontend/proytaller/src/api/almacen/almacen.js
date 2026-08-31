import request from '../../util/request'

export function listarAlmacenes(params) {
  return request({
    url: '/api/almacenes',
    method: 'get',
    params
  })
}

export function listarTiposAlmacen() {
  return request({
    url: '/api/almacenes/tipos',
    method: 'get'
  })
}

export function listarResponsablesAlmacen() {
  return request({ url: '/api/almacenes/responsables', method: 'get' })
}

export function crearAlmacen(data) {
  return request({ url: '/api/almacenes', method: 'post', data })
}

export function actualizarAlmacen(idAlmacen, data) {
  return request({ url: `/api/almacenes/${idAlmacen}`, method: 'put', data })
}

export function activarAlmacen(idAlmacen) {
  return request({ url: `/api/almacenes/${idAlmacen}/activar`, method: 'patch' })
}

export function desactivarAlmacen(idAlmacen) {
  return request({ url: `/api/almacenes/${idAlmacen}/desactivar`, method: 'patch' })
}

export async function descargarInventarioAlmacenPdf(idAlmacen) {
  const { default: axios } = await import('axios')
  const { useAuthStore } = await import('../../store/store')
  const store = useAuthStore()
  return axios({
    url: `${import.meta.env.VITE_API_URL}/api/almacenes/${idAlmacen}/inventario/pdf`,
    method: 'get',
    responseType: 'blob',
    headers: { Authorization: `Bearer ${store.token}` }
  }).then(response => response.data)
}
