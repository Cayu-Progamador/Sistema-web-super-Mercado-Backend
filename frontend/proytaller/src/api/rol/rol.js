import request from '../../util/request'

//listar roles
export function getListRoles() {
    return request({
        url: '/api/roles/listar',
        method: 'get'
    })
}