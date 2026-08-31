import request from '../../util/request'

export function listarMotivos() {
    return request({
        url: '/api/devoluciones/motivos',
        method: 'get'
    })
}