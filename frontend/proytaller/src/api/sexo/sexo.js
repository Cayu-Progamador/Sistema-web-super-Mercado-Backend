import request from '../../util/request'

export function listarSexos() {
    return request({
        url: '/api/sexos',
        method: 'get'
    })
}
