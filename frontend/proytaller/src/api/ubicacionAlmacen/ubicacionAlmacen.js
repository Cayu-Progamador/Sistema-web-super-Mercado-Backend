import request from '../../util/request'

export function listarUbicaciones(params) {
    return request({
        url: '/api/almacenes/ubicaciones',
        method: 'get',
        params
    })
}

export function listarUbicacionesPaginado(params) {
    return request({ url: '/api/almacenes/ubicaciones/pagina', method: 'get', params })
}
export function listarEstadosUbicacion() {
    return request({ url: '/api/almacenes/ubicaciones/estados', method: 'get' })
}
export function listarTiposUbicacion() {
    return request({ url: '/api/almacenes/ubicaciones/tipos', method: 'get' })
}
export function crearUbicacion(data) { return request({ url: '/api/almacenes/ubicaciones', method: 'post', data }) }
export async function descargarInventarioUbicacionPdf(id) {
    const { default: axios } = await import('axios')
    const { useAuthStore } = await import('../../store/store')
    const store = useAuthStore()
    const response = await axios({ url: `${import.meta.env.VITE_API_URL}/api/almacenes/ubicaciones/${id}/inventario/pdf`, method: 'get', responseType: 'blob', headers: { Authorization: `Bearer ${store.token}` } })
    return response.data
}
export function actualizarUbicacion(id, data) { return request({ url:`/api/almacenes/ubicaciones/${id}`, method:'put', data }) }
export function activarUbicacion(id) { return request({ url:`/api/almacenes/ubicaciones/${id}/activar`, method:'patch' }) }
export function desactivarUbicacion(id) { return request({ url:`/api/almacenes/ubicaciones/${id}/desactivar`, method:'patch' }) }
export function listarProductosUbicacion(id) { return request({ url:`/api/almacenes/ubicaciones/${id}/inventario`, method:'get' }) }
