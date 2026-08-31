import request from '../../util/request'

export function listarPedidos(params) {
    return request({
        url: '/api/pedidos',
        method: 'get',
        params
    })
}

export function obtenerPedido(id) {
    return request({
        url: `/api/pedidos/${id}`,
        method: 'get'
    })
}

export function obtenerKpi() {
    return request({
        url: '/api/pedidos/kpi',
        method: 'get'
    })
}

export function listarEstados() {
    return request({
        url: '/api/pedidos/estados',
        method: 'get'
    })
}

export function crearPedido(data) {
    return request({
        url: '/api/pedidos',
        method: 'post',
        data
    })
}

export function actualizarPedido(id, data) {
    return request({
        url: `/api/pedidos/${id}`,
        method: 'put',
        data
    })
}

export function enviarPedido(id) {
    return request({
        url: `/api/pedidos/${id}/enviar`,
        method: 'patch'
    })
}

export function cancelarPedido(id) {
    return request({
        url: `/api/pedidos/${id}/cancelar`,
        method: 'patch'
    })
}

export async function descargarPdfPedido(id) {
    const { default: axios } = await import('axios')
    const { useAuthStore } = await import('../../store/store')
    const store = useAuthStore()
    return axios({
        url: `${import.meta.env.VITE_API_URL}/api/pedidos/${id}/pdf`,
        method: 'get',
        responseType: 'blob',
        headers: { Authorization: `Bearer ${store.token}` }
    }).then(res => res.data)
}