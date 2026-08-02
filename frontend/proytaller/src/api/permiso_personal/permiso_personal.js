import request from '../../util/request'

export function listarTiposPermiso() {
  return request({
    url: '/api/tipos-permiso',
    method: 'get'
  })
}

export function crearSolicitud(data) {
  return request({
    url: '/api/solicitudes-permiso',
    method: 'post',
    data
  })
}

export function listarMisSolicitudes() {
  return request({
    url: '/api/solicitudes-permiso/mis-solicitudes',
    method: 'get'
  })
}

export function listarSolicitudesPorEstado(estado) {
  return request({
    url: `/api/solicitudes-permiso/estado/${estado}`,
    method: 'get'
  })
}

export function aprobarSolicitud(id, data) {
  return request({
    url: `/api/solicitudes-permiso/${id}/aprobar`,
    method: 'put',
    data
  })
}

export function rechazarSolicitud(id, data) {
  return request({
    url: `/api/solicitudes-permiso/${id}/rechazar`,
    method: 'put',
    data
  })
}

export function cancelarSolicitud(id, data) {
  return request({
    url: `/api/solicitudes-permiso/${id}/cancelar`,
    method: 'put',
    data
  })
}

export function revisarSolicitud(id) {
  return request({
    url: `/api/solicitudes-permiso/${id}/revisar`,
    method: 'put'
  })
}

export function obtenerHistorial(id) {
  return request({
    url: `/api/solicitudes-permiso/${id}/historial`,
    method: 'get'
  })
}

export function listarTodasLasSolicitudes() {
  return request({
    url: '/api/solicitudes-permiso/todas',
    method: 'get'
  })
}

export async function exportarPermisosPDF(estado) {
  const { default: axios } = await import('axios')
  const { useAuthStore } = await import('../../store/store')
  const store = useAuthStore()
  return axios({
    url: `${import.meta.env.VITE_API_URL}/api/solicitudes-permiso/exportar/pdf`,
    method: 'get',
    params: { estado },
    responseType: 'blob',
    headers: { Authorization: `Bearer ${store.token}` }
  }).then(res => res.data)
}

export async function exportarPermisosExcel(estado) {
  const { default: axios } = await import('axios')
  const { useAuthStore } = await import('../../store/store')
  const store = useAuthStore()
  return axios({
    url: `${import.meta.env.VITE_API_URL}/api/solicitudes-permiso/exportar/excel`,
    method: 'get',
    params: { estado },
    responseType: 'blob',
    headers: { Authorization: `Bearer ${store.token}` }
  }).then(res => res.data)
}
