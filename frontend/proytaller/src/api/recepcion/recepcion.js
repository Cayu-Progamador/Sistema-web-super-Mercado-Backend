import request from '../../util/request'

export function listarRecepciones(params) {
    return request({
        url: '/api/recepciones',
        method: 'get',
        params
    })
}

export function crearRecepcion(data) {
    return request({
        url: '/api/recepciones',
        method: 'post',
        data
    })
}

export function anularRecepcion(id) {
    return request({
        url: `/api/recepciones/${id}/anular`,
        method: 'put'
    })
}

export function distribuirRecepcion(id, data) {
    return request({
        url: `/api/recepciones/${id}/distribuir`,
        method: 'put',
        data
    })
}

export function listarEstados() {
    return request({
        url: '/api/recepciones/estados',
        method: 'get'
    })
}

export function listarFaltantes() {
    return request({
        url: '/api/recepciones/faltantes',
        method: 'get'
    })
}

export function listarDevoluciones() {
    return request({
        url: '/api/recepciones/devoluciones',
        method: 'get'
    })
}

export function obtenerKpi() {
    return request({
        url: '/api/recepciones/kpi',
        method: 'get'
    })
}

export async function descargarPdfRecepcion(id) {
    const { default: axios } = await import('axios')
    const { useAuthStore } = await import('../../store/store')
    const store = useAuthStore()
    return axios({
        url: `${import.meta.env.VITE_API_URL}/api/recepciones/${id}/pdf`,
        method: 'get',
        responseType: 'blob',
        headers: { Authorization: `Bearer ${store.token}` }
    }).then(res => res.data)
}