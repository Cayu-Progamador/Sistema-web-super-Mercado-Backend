import request from '../../util/request'

//listar roles para usuario
export function getListRoles() {
    return request({
        url: '/api/roles/select-rol',
        method: 'get'
    })
}


//listar roles para usuario
export function listarRoles() {
    return request({
        url: '/api/roles/listar',
        method: 'get'
    })
}

//crear un nuevo rol
export function crearRol(data) {
    return request({
        url: '/api/roles/crear',
        method: 'post',
        data: data
    })
}